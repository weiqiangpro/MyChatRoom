package com.wq.clink.dispather.box;

import java.io.ByteArrayOutputStream;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午7:56
 */
public class ByteReceivePacket extends  AbsByteArrayReceivePacket<byte[]>{
    public ByteReceivePacket(long len) {
        super(len);
    }
    public byte type() {
        return TYPE_MEMORY_BYTES;
    }

    protected byte[] buildEntity(ByteArrayOutputStream stream){
        return stream.toByteArray();
    }
}
