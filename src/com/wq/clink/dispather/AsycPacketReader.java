package com.wq.clink.dispather;

import com.wq.clink.core.BytePriorityNode;
import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;
import com.wq.clink.core.frames.AbsSendPacketFrame;
import com.wq.clink.core.frames.CancelSendFrame;
import com.wq.clink.core.frames.SendEntityFrame;
import com.wq.clink.core.frames.SendHeaderFrame;
import com.wq.clink.dispather.box.abs.SendPacket;
import java.io.Closeable;
import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:52
 */
public class AsycPacketReader implements Closeable {
    private volatile IoArgs args = new IoArgs();
    private final PacketProvider provider;
    private volatile BytePriorityNode<Frame> node;
    private volatile int nodeSize = 0;
    private short lastIndentifier = 0;

    AsycPacketReader(PacketProvider provider) {
        this.provider = provider;
    }

    synchronized void cancel(SendPacket packet) {
        if (nodeSize == 0) {
            return;
        }
        for (BytePriorityNode<Frame> x = node, before = null; x != null; before = x, x = x.next) {
            Frame frame = x.item;
            if (frame instanceof AbsSendPacketFrame) {
                AbsSendPacketFrame packetFrame = (AbsSendPacketFrame) frame;
                if (packetFrame.getPacket() == packet) {
                    boolean abort = packetFrame.abort();
                    if (abort) {
                        removeFrame(x, before);
                        if (packetFrame instanceof SendHeaderFrame) {
                            break;
                        }
                    }

                    CancelSendFrame cancelSendFrame = new CancelSendFrame(packetFrame.getBodyIndetifier());
                    appendNewFrame(cancelSendFrame);
                    //意外终止，失败
                    provider.completePacket(packet, false);
                    break;
                }
            }
        }

    }

    private synchronized void removeFrame(BytePriorityNode<Frame> x, BytePriorityNode<Frame> before) {
        if (before == null) {
            // a b c    b c
            node = x.next;
        } else {
            //a b c  a c
            before.next = x.next;
        }
        nodeSize--;
        if (node == null)
            requestTackPacket();
    }

    boolean requestTackPacket() {
        synchronized (this) {
            if (nodeSize >= 1)
                return true;
        }
        SendPacket packet = provider.tackPacket();
        if (packet != null) {
            short identifier = generateIdentifier();
            SendHeaderFrame frame = new SendHeaderFrame(identifier, packet);
            appendNewFrame(frame);
        }
        synchronized (this) {
            return nodeSize != 0;
        }
    }

    @Override
    public synchronized void close() {
        while (node != null) {
            Frame frame = node.item;
            if (frame instanceof AbsSendPacketFrame) {
                SendPacket packet = ((AbsSendPacketFrame) frame).getPacket();
                provider.completePacket(packet, false);
            }
            node = node.next;
        }


        nodeSize = 0;
        node = null;
    }

    private synchronized void appendNewFrame(Frame frame) {
        BytePriorityNode<Frame> newNode = new BytePriorityNode<>(frame);
        if (node != null) {
            node.appendWithPriority(newNode);
        } else {
            node = newNode;
        }
        nodeSize++;
    }

    IoArgs fillData() {
        Frame currentFrame = getCurrentFrame();
        if (currentFrame == null)
            return null;
        try {
            if (currentFrame.handle(args)) {
                //消费完本真
                //尝试构造后帧
                Frame next = currentFrame.nextFrame();
                if (next != null) {
                    appendNewFrame(next);
                } else if (currentFrame instanceof SendEntityFrame) {
                    //末尾
                    provider.completePacket(((SendEntityFrame) currentFrame).getPacket(), true);
                }
                popCurrentFrame();
            }
            return args;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private synchronized void popCurrentFrame() {
        node = node.next;
        nodeSize--;
        if (node == null) {
            requestTackPacket();
        }
    }

    private Frame getCurrentFrame() {
        if (node == null) {
            return null;
        }
        return node.item;
    }

    private short generateIdentifier() {
        short identifier = ++lastIndentifier;
        if (identifier == 255) {
            lastIndentifier = 0;
        }
        return identifier;
    }

    interface PacketProvider {

        SendPacket tackPacket();

        void completePacket(SendPacket packet, boolean isSucceed);
    }
}
