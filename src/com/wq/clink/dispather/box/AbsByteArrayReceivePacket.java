package com.wq.clink.dispather.box;

import com.wq.clink.dispather.box.abs.ReceivePacket;

import java.io.ByteArrayOutputStream;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午7:57
 */
public  abstract  class AbsByteArrayReceivePacket<Entity> extends ReceivePacket<ByteArrayOutputStream,Entity> {
    public AbsByteArrayReceivePacket(long len) {
        super(len);
    }

    @Override
    protected ByteArrayOutputStream createStream() {
        return new ByteArrayOutputStream((int) length);
    }
}
