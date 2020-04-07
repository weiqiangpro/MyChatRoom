package com.wq.clink.core;

import com.wq.clink.dispather.box.abs.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @Author: weiqiang
 * @Time: 2020/3/10 上午10:26
 */
public class IoArgs {
    private byte[] bytes = new byte[262];
    private ByteBuffer buffer = ByteBuffer.wrap(bytes);
    private int limit = 262;

    public int readFrom(byte[] bytes, int offset, int count) {
        int size = Math.min(count, buffer.remaining());
        if (size <= 0)
            return 0;
        buffer.put(bytes, offset, size);
        return size;
    }

    public int setEmpty(int size) {
        int emptySize = Math.min(size, buffer.remaining());
        buffer.position(buffer.position() + emptySize);
        return emptySize;
    }

    public int read(SocketChannel channel) throws IOException {
        startWriting();
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.read(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
            if (buffer.limit() == 7 && bytes[5] == Packet.TYPE_STREAM_FILE) {
                buffer.limit(buffer.limit() + bytes[6]);
            }
        }
        finishWriting();
        return bytesProduced;
    }

    public int readFrom(ReadableByteChannel channel) throws IOException {
//        startWriting();
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.read(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
//        finishWriting();
        return bytesProduced;
    }

    public int write(SocketChannel channel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.write(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
        return bytesProduced;
    }

    public int writeTo(byte[] bytes, int offset) {
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.get(bytes, offset, size);
        return size;
    }

    public int writeTo(WritableByteChannel channel) throws IOException {

        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.write(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
        return bytesProduced;
    }

    public void startWriting() {
        buffer.clear();
        buffer.limit(limit);
    }

    public void finishWriting() {
        buffer.flip();
    }

    public void limt(int limit) {
        this.limit = Math.min(limit, buffer.capacity());
    }

    public int capacity() {
        return buffer.capacity();
    }

    public int readLength() {
        return buffer.getInt();
    }

    public boolean remained() {
        return buffer.remaining() > 0;
    }

    public int fillEmpty(int size) {
        int fillSize = Math.min(size, buffer.remaining());
        buffer.position(buffer.position() + fillSize);
        return fillSize;

    }

}
