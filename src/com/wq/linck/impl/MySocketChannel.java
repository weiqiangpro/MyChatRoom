package com.wq.linck.impl;

import com.wq.linck.Connector;
import com.wq.linck.IoArgs;
import com.wq.linck.IoProvider;
import com.wq.linck.Receiver;
import com.wq.utils.constants.CloseUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:50
 */
public class MySocketChannel implements Receiver, Closeable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final SocketChannel channel;
    private final IoProvider ioProvider;
    private final   OnChannelStatusChangedListener listener;
    private IoArgs.IoArgsEventListener receiveIoEventListener;
    public MySocketChannel(SocketChannel channel, IoProvider ioProvider,OnChannelStatusChangedListener listener) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        channel.configureBlocking(false);
        this.listener = listener;
    }


    @Override
    public boolean receiveAsync(IoArgs.IoArgsEventListener listener) throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }

        receiveIoEventListener = listener;

        return ioProvider.registerInput(channel, inputCallback);
    }

    @Override
    public void close() throws IOException {

    }

    private final IoProvider.HandleInputCallback inputCallback = new IoProvider.HandleInputCallback() {
        @Override
        protected void canProviderInput() {
            if (isClosed.get()) {
                return;
            }

            IoArgs args = new IoArgs();
            IoArgs.IoArgsEventListener listener = MySocketChannel.this.receiveIoEventListener;

            if (listener != null) {
                listener.onStarted(args);
            }

            try {
                // 具体的读取操作
                if (args.read(channel) > 0 && listener != null) {
                    // 读取完成回调
                    listener.onCompleted(args);
                } else {
                    throw new IOException("Cannot read any data!");
                }
            } catch (IOException ignored) {
                CloseUtil.close(MySocketChannel.this);
            }


        }
    };

    public interface OnChannelStatusChangedListener {
        void onChannelClosed(SocketChannel channel);
    }
}
