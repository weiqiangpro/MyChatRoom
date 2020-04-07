package com.wq.clink;

import com.wq.clink.callback.OnArrivedAndReadNext;
import com.wq.clink.core.IoArgs;
import com.wq.clink.core.Receiver;
import com.wq.clink.core.impl.SocketChannelAdapter;
import com.wq.clink.dispather.FileReceivePacket;
import com.wq.clink.dispather.ReceiveDispather;
import com.wq.clink.dispather.SendDispather;
import com.wq.clink.dispather.box.ByteReceivePacket;
import com.wq.clink.dispather.box.StringReceivePacket;
import com.wq.clink.dispather.box.StringSendPacket;
import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.ReceivePacket;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:58
 */
public abstract class Connector implements Closeable {
    protected UUID key = UUID.randomUUID();
    private SocketChannel channel;
    private Receiver receiver;
    private SendDispather sendDispather;
    public void setup(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        Context context = Context.get();
        SocketChannelAdapter adapter = new SocketChannelAdapter(channel, context.getIoProvider());
        this.sendDispather = new SendDispather(adapter);
        new ReceiveDispather(adapter,onArrivedAndReadNext).start();

    }

    public void send(String str) {
        StringSendPacket   stringSendPacket = new StringSendPacket(str);
        sendDispather.send(stringSendPacket);
    }

    public void send(SendPacket packet) {
        sendDispather.send(packet);
    }


    private final OnArrivedAndReadNext onArrivedAndReadNext = new OnArrivedAndReadNext() {

        @Override
        public ReceivePacket<?, ?> onArrivedNewPacket(byte type, long len) {
            switch (type){
                case Packet.TYPE_MEMORY_BYTES:
                    return new ByteReceivePacket(len);
                case Packet.TYPE_MEMORY_STRING:
                    return new StringReceivePacket(len);
                case Packet.TYPE_STREAM_FILE:
//                case 0:
                    return new FileReceivePacket(len,createNewFile());
                case Packet.TYPE_STREAM_DIRECT:
                    return new ByteReceivePacket(len);
                default:
                   throw new UnsupportedOperationException("不支持");
            }
        }

        @Override
        public void onCompleted(ReceivePacket packet) {

            onReceivePacket(packet);
}
    };

    protected abstract File createNewFile() ;

    protected void onReceiveNewMessage(String str) {
        System.out.println(key.toString() + ":" + str);
    }
    protected void onReceivePacket(ReceivePacket packet) {
        System.out.println(key.toString() + ":[NEW Packet] type:" +packet.type()+"Length:" +packet.getLength());
    }

    @Override
    public void close() throws IOException {

    }

}
