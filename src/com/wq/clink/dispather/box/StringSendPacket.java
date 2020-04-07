package com.wq.clink.dispather.box;

import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.*;

public class StringSendPacket extends ByteSendPacket {



    public StringSendPacket(String msg)  {
        super(msg.getBytes());
    }

    public byte type(){
        return TYPE_MEMORY_STRING;
    }
}