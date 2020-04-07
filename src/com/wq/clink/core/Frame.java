package com.wq.clink.core;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:52
 */
public abstract class Frame {
    public static final int FRAME_HEADER_LEN = 6;
    protected final byte[] header = new byte[6];
    public static final int MAX_CAPACITY = 64 * 1024 - 1;

    public static final byte TYPE_PACKET_HEADER = 11;
    public static final byte TYPE_PACKET_ENTITY = 12;

    public static final byte TYPE_COMMAND_SEND_CANCEL = 41;
    public static final byte TYPE_COMMAND_RECEIVE_REJECT = 42;

    public static final byte FLAG_NONE = 0;


    public Frame(int len, byte type, byte flag, short indetifier) {
        if (len < 0 || len > MAX_CAPACITY) {
            throw new RuntimeException("");
        }
        if (indetifier < 1 || indetifier > 255) {
            throw new RuntimeException("");
        }
        header[0] = (byte) (len >> 8);
        header[1] = (byte) len;
        header[2] = type;
        header[3] = flag;
        header[4] = (byte) indetifier;
        header[5] = 0;

    }

    public Frame(byte[] header) {
        System.arraycopy(header,0,this.header,0,FRAME_HEADER_LEN);
    }

    public int getBodyLength() {
        return ((((int) header[0]) & 0xFF) << 8) | (((int) header[1]) & 0xFF);
    }

    public byte getBodyType() {
        return header[2];
    }

    public byte getBodyFlag() {
        return header[3];
    }

    public short getBodyIndetifier() {
        return (short) (((short) header[4]) & 0xFF);
    }

    public abstract  boolean handle(IoArgs args) throws IOException;

    public abstract Frame nextFrame();

    public abstract int getConsumableLength();
}
