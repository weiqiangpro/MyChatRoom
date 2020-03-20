package com.wq.linck.core;

import com.wq.server.handle.ServerHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:26
 */
public class IoArgs {
    private byte[] bytes = new byte[4];
    private ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

    public int read(SocketChannel channel) throws IOException {
        byteBuffer.clear();
        return channel.read(byteBuffer);
    }


    public int write(SocketChannel channel,String str) throws IOException {
        byteBuffer.clear();
        byteBuffer.put((str + "\n").getBytes());
        byteBuffer.flip();
        return channel.write(byteBuffer);
    }

    public String bufferString() {
        // 丢弃换行符
        return new String(bytes, 0, byteBuffer.position() - 1);
    }
}
