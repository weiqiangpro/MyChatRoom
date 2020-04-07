package com.wq.clink.dispather;

import com.wq.clink.callback.OnArrivedAndReadNext;
import com.wq.clink.core.IoArgs;
import com.wq.clink.core.Receiver;
import com.wq.clink.dispather.box.StringReceivePacket;
import com.wq.clink.dispather.box.StringSendPacket;
import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.ReceivePacket;
import com.wq.clink.dispather.box.abs.SendPacket;
import com.wq.clink.dispather.dispathercal.IoArgsCallback;
import com.wq.clink.dispather.dispathercal.IoArgsEventProcessor;
import com.wq.utils.constants.CloseUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: weiqiang
 * @Time: 2020/3/20 下午10:14
 */
public class ReceiveDispather implements IoArgsEventProcessor, Closeable , AsycPacketWriter.PacketProvider {
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final Receiver receiver;
    private  final OnArrivedAndReadNext onArrivedAndReadNext;
    private final AsycPacketWriter writer = new AsycPacketWriter(this);

    public ReceiveDispather(Receiver receiver, OnArrivedAndReadNext onArrivedAndReadNext) {
        this.receiver = receiver;
        this.receiver.setReceiveProcessor(this);
        this.onArrivedAndReadNext = onArrivedAndReadNext;
    }

    public void start() {
        try {
            receiver.receiveAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IoArgs provideIoArgs() {
        return  writer.takeIoArgs();
    }

    @Override
    public void onFiled(IoArgs args, Exception e) {
e.printStackTrace();
    }

    @Override
    public void onCompleted(IoArgs args) {
        do {
            writer.consumeIoArgs(args);
        }while (args.remained());
        start();
    }

    @Override
    public void close() throws IOException {
    if (isClosed.compareAndSet(false,true))
        writer.close();
    }


    @Override
    public ReceivePacket tackPacket(byte type, long length, byte[] headInfo,String fileName) {
        return onArrivedAndReadNext.onArrivedNewPacket(type,length,fileName);
    }

    @Override
    public void completePacket(ReceivePacket packet, boolean isSucceed) {
        CloseUtil.close(packet);
        onArrivedAndReadNext.onCompleted(packet);
    }
}
