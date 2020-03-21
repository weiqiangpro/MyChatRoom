package com.wq.clink.dispather.box;

import java.io.Closeable;
import java.io.IOException;

public class StringReceivePacket implements Closeable {

    private byte[] buffer;
    private int positon;
    private int length;
    public StringReceivePacket(int len){
        buffer = new byte[len];
        this.length = len;
    }

    public void save(byte[] bytes, int count) {
        System.arraycopy(bytes,0,buffer,positon,count);
        positon += count;
    }
    public String string(){
        return new String(buffer);
    }

    @Override
    public void close() throws IOException {

    }
}