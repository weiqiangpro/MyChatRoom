package com.wq.clink.dispather.box;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.ReceivePacket;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.*;

public class StringReceivePacket extends AbsByteArrayReceivePacket<String> {

    private String string;

    public StringReceivePacket(long len) {
       super(len);
    }

    @Override
    protected String buildEntity(ByteArrayOutputStream stream) {
        return new String(stream.toByteArray());
    }

    @Override
    public byte type() {
        return TYPE_MEMORY_STRING;
    }

}