package com.wq.clink.callback;

import com.wq.clink.core.IoArgs;
import com.wq.clink.dispather.box.StringReceivePacket;

public interface  OnArrivedAndReadNext {
     void onCompleted(StringReceivePacket packet);
}