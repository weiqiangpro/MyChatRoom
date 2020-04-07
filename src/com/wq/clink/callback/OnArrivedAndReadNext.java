package com.wq.clink.callback;

import com.wq.clink.core.IoArgs;
import com.wq.clink.dispather.box.StringReceivePacket;
import com.wq.clink.dispather.box.abs.ReceivePacket;

public interface  OnArrivedAndReadNext {

     ReceivePacket<?,?> onArrivedNewPacket(byte type,long len,String fileName);
     void onCompleted(ReceivePacket packet);
}