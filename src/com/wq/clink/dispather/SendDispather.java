package com.wq.clink.dispather;

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
    private final Object lock = new Object();
    private final AsycPacketReader reader = new AsycPacketReader(this);

    public SendDispather(Sender sender) {
        this.sender = sender;
        sender.setSendProcessor(this);
    }

    public void send(SendPacket packet) {
        synchronized (lock) {
            queue.add(packet);
            if (isSending.compareAndSet(false, true))
                if (reader.requestTackPacket())
                    requestSend();
        }
    }

    public void cancel(SendPacket packet) {
        boolean res;
        synchronized (lock) {
            res = queue.remove(packet);
        }
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
        try {
            sender.senderAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IoArgs provideIoArgs() {
        return reader.fillData();
    }

    @Override
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) {
            isSending.set(false);
            reader.close();
        }
    }

    @Override
    public SendPacket tackPacket() {
        SendPacket packet;
        synchronized (lock) {
            packet = queue.poll();
            if (packet == null) {
                isSending.set(false);
                return null;
            }
        }
        if (packet.isCanceled())
            return tackPacket();
        return packet;
    }

    @Override
    public void onFiled(IoArgs args, Exception e) {
        if (args != null) {
            e.printStackTrace();
        } else {
            //TODO
        }
    }

    @Override
    public void onCompleted(IoArgs args) {
        if (reader.requestTackPacket())
            requestSend();
    }
    @Override
    public void completePacket(SendPacket packet, boolean isSucceed) {
        CloseUtil.close(packet);
    }
}