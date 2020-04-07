package com.wq.clink.dispather.box;

import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.ByteArrayInputStream;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午7:54
 */
public class ByteSendPacket extends SendPacket<ByteArrayInputStream> {
    private final byte[] bytes;

    public ByteSendPacket(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
    }

    @Override
    public byte type() {
        return TYPE_MEMORY_BYTES;
    }

    @Override
    protected ByteArrayInputStream createStream() {
        return new ByteArrayInputStream(bytes);
    }
}
