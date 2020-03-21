package com.wq.clink.dispather.box;

import java.io.Closeable;
import java.io.IOException;

public class StringSendPacket implements Closeable {

    private final byte[] bytes;
    private final int length;

    public StringSendPacket(String msg) {
        this.bytes = msg.getBytes();
        this.length = bytes.length;
    }

    public int getLength(){
        return length;
    }

    public byte[] bytes() {
        return bytes;
    }

    @Override
    public void close() throws IOException {

    }
}