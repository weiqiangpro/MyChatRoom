package com.wq.clink.core.impl;

import com.wq.clink.callback.ReadCallBack;
import com.wq.clink.callback.WriteCallBack;
import com.wq.clink.core.IoArgs;
import com.wq.clink.core.IoProvider;
import com.wq.clink.core.Receiver;
import com.wq.clink.core.Sender;
import com.wq.clink.dispather.dispathercal.IoArgsCallback;
import com.wq.clink.dispather.dispathercal.IoArgsEventProcessor;
import com.wq.utils.constants.CloseUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketChannelAdapter implements Sender, Receiver, Closeable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final SocketChannel channel;
    private final IoProvider ioProvider;
    private IoArgsEventProcessor receiveDisCallback;
    private IoArgsEventProcessor sendDisCallback;
    private IoArgs ioArgs;
    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        channel.configureBlocking(false);
    }

    @Override
    public boolean receiveAsync() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }

        return ioProvider.readRegister(channel, readCallBack);
    }

    @Override
    public void setReceiveProcessor(IoArgsEventProcessor callBack) {
        this.receiveDisCallback = callBack;
    }

    @Override
    public boolean senderAsync() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }

        return ioProvider.writeRegister(channel, writeCallBack);
    }

    @Override
    public void setSendProcessor(IoArgsEventProcessor callBack) {
        this.sendDisCallback = callBack;
    }

    public final ReadCallBack readCallBack = new ReadCallBack() {
        @Override
        protected void canProviderInput() {
            if (isClosed.get()) {
                return;
            }
            IoArgsEventProcessor processor = SocketChannelAdapter.this.receiveDisCallback;
            IoArgs args = processor.provideIoArgs();
            try {
                if (args == null){
                    processor.onFiled(null,new Exception());
                }
                // 具体的读取操作
               else if (args.read(channel) > 0) {
                    // 读取完成回调
                    processor.onCompleted(args);
                } else {
                    processor.onFiled(args,new IOException("Cannot read any data!"));
                }
            } catch (IOException ignored) {
                CloseUtil.close(SocketChannelAdapter.this);
            }
        }
    };

    public final WriteCallBack writeCallBack = new WriteCallBack() {
        @Override
        protected void canProviderOutput() {
            if (isClosed.get()) {
                return;
            }
            IoArgsEventProcessor processor = SocketChannelAdapter.this.sendDisCallback;
            IoArgs args = processor.provideIoArgs();
            try {
                // 具体的读取操
                if (args.write(channel) > 0) {
                    processor.onCompleted(args);
                    // 读取完成回调
                } else {
                    processor.onFiled(args,new IOException("Cannot write any data!"));
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