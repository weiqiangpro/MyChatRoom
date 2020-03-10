package com.wq.linck.core.impl;

import com.wq.linck.callback.OnArrivedAndReadNext;
import com.wq.linck.callback.ReadCallBack;
import com.wq.linck.core.IoArgs;
import com.wq.linck.core.IoProvider;
import com.wq.linck.core.Receiver;
import com.wq.utils.constants.CloseUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketChannelAdapter implements Receiver, Closeable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final SocketChannel channel;
    private final IoProvider ioProvider;
    private OnArrivedAndReadNext onArrivedAndReadNext;

    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        channel.configureBlocking(false);
    }

    @Override
    public boolean receiveAsync(OnArrivedAndReadNext onArrivedAndReadNext) throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }
        this.onArrivedAndReadNext = onArrivedAndReadNext;
        return ioProvider.registerInput(channel, readCallBack);
    }

    private final ReadCallBack readCallBack = new ReadCallBack() {
        @Override
        protected void canProviderInput() {
            if (isClosed.get()) {
                return;
            }

            IoArgs args = new IoArgs();
            OnArrivedAndReadNext onArrivedAndReadNext =  SocketChannelAdapter.this.onArrivedAndReadNext;
//            if (onArrivedAndReadNext != null) {
//                onArrivedAndReadNext.onStarted(args);
//            }
            try {
                // 具体的读取操作
                if (args.read(channel) > 0 && onArrivedAndReadNext != null) {
                    // 读取完成回调
                    onArrivedAndReadNext.onCompleted(args);
                } else {
                    throw new IOException("Cannot read any data!");
                }
            } catch (IOException ignored) {
                CloseUtil.close(SocketChannelAdapter.this);
            }
        }
    };

    @Override
    public void close() throws IOException {

    }
}
