package com.wq.linck;

import com.wq.linck.impl.MySocketChannel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:58
 */
public class Connector implements Closeable, MySocketChannel.OnChannelStatusChangedListener {
    private UUID key = UUID.randomUUID();
    private SocketChannel channel;
    private Receiver receiver;

    public void setup(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;

        Context context = Context.get();
        MySocketChannel adapter = new MySocketChannel(channel, context.getIoProvider(), this);

        this.receiver = adapter;

        readNextMessage();
    }

    private void readNextMessage() {
        if (receiver != null) {
            try {
                receiver.receiveAsync(echoReceiveListener);
            } catch (IOException e) {
                System.out.println("开始接收数据异常：" + e.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {

    }




    private IoArgs.IoArgsEventListener echoReceiveListener = new IoArgs.IoArgsEventListener() {
        @Override
        public void onStarted(IoArgs args) {

        }

        @Override
        public void onCompleted(IoArgs args) {
            // 打印
            onReceiveNewMessage(args.bufferString());
            // 读取下一条数据
            readNextMessage();
        }
    };

    protected void onReceiveNewMessage(String str) {
      //  System.out.println(key.toString() + ":" + str);
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {

    }
}
