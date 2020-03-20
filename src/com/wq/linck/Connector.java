package com.wq.linck;

import com.wq.linck.callback.OnArrivedAndReadNext;
import com.wq.linck.core.IoArgs;
import com.wq.linck.core.Resign;
import com.wq.linck.core.impl.SocketChannelAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:58
 */
public class Connector implements Closeable {
    private UUID key = UUID.randomUUID();
    private SocketChannel channel;
    private Resign receiver;

    public void setup(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        Context context = Context.get();
        SocketChannelAdapter adapter = new SocketChannelAdapter(channel, context.getIoProvider());
        this.receiver = adapter;
      //  adapter.sendAsync();
        readNextMessage();
    }

    private void readNextMessage() {
        System.out.println("readNext");
        if (receiver != null) {
            try {
                receiver.receiveAsync(onArrivedAndReadNext);
            } catch (IOException e) {
                System.out.println("开始接收数据异常：" + e.getMessage());
            }
        }
    }

    private final OnArrivedAndReadNext onArrivedAndReadNext = new OnArrivedAndReadNext() {

        @Override
        public void onCompleted(IoArgs args) {
            onReceiveNewMessage(args.bufferString());
            readNextMessage();
        }
    };

    protected void onReceiveNewMessage(String str) {
         System.out.println(key.toString() + ":" + str);
    }

    @Override
    public void close() throws IOException {

    }

}
