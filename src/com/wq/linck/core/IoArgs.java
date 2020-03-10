package com.wq.linck.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:26
 */
public class IoArgs {
    private byte[] bytes = new byte[256];
    private ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

    public int read(SocketChannel channel) throws IOException {
        byteBuffer.clear();
        return channel.read(byteBuffer);
    }

    public int write(SocketChannel channel) throws IOException {
        return channel.write(byteBuffer);
    }

    public String bufferString() {
        // 丢弃换行符
        return new String(bytes, 0, byteBuffer.position() - 1);
    }

//    public interface IoArgsEventListener {
//        void onStarted(IoArgs args);
//
//        void onCompleted(IoArgs args);
//    }
}
