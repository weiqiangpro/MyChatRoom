package com.wq.clink.dispather;

import com.wq.clink.Connector;
import com.wq.clink.core.IoArgs;
import com.wq.clink.core.Sender;
import com.wq.clink.dispather.box.StringSendPacket;
import com.wq.clink.dispather.box.abs.SendPacket;
import com.wq.clink.dispather.dispathercal.IoArgsCallback;
import com.wq.clink.dispather.dispathercal.IoArgsEventProcessor;
import com.wq.utils.constants.CloseUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: weiqiang
 * @Time: 2020/3/20 下午10:14
 */
public class SendDispather implements IoArgsEventProcessor, Closeable, AsycPacketReader.PacketProvider {
    private final Queue<SendPacket> queue = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isSending = new AtomicBoolean();
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final Sender sender;
    private final AsycPacketReader reader = new AsycPacketReader(this);
    private final Connector.OnClose close ;
    public SendDispather(Sender sender, Connector.OnClose onClose) {
        this.sender = sender;
        this.close = onClose;
        sender.setSendProcessor(this);
    }

    public void send(SendPacket packet) {
        queue.add(packet);
        requestSend();
    }

    public void cancel(SendPacket packet) {
        boolean res;
        res = queue.remove(packet);
        if (res) {
            packet.cancel();
            return;
        }
        reader.cancel(packet);
    }

    /**
     * 请求网络发送
     */
    private void requestSend() {
        synchronized (isSending) {
            if (isSending.get()) {
                return;
            }

            if (reader.requestTackPacket()) {
                try {
                    boolean b = sender.senderAsync();
                    if (b)
                        isSending.set(true);
                } catch (IOException e) {
                   CloseUtil.close(this);
                }
            }
        }
    }

    @Override
    public IoArgs provideIoArgs() {
        return isClosed.get() ? null : reader.fillData();
    }

    @Override
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) {
            reader.close();
            queue.clear();
            close.onclose();
            synchronized (isSending) {
                isSending.set(false);
            }
        }
    }

    @Override
    public SendPacket tackPacket() {
        SendPacket packet = queue.poll();
        if (packet == null) {
            return null;
        }
        if (packet.isCanceled())
            return tackPacket();
        return packet;
    }

    @Override
    public void onFiled(IoArgs args, Exception e) {
        e.printStackTrace();
        synchronized (isSending) {
            isSending.set(false);
        }
        requestSend();
    }

    @Override
    public void onCompleted(IoArgs args) {
        synchronized (isSending) {
            isSending.set(false);
        }
        requestSend();
    }

    @Override
    public void completePacket(SendPacket packet, boolean isSucceed) {
        CloseUtil.close(packet);
    }
}