package com.wq.clink.dispather.box.abs;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午2:20
 */
public abstract class SendPacket<T extends InputStream> extends Packet<T> {
    private boolean isCanceled;
    public   String name;

    public boolean isCanceled() {
        return isCanceled;
    }

    public void cancel(){
        isCanceled = true;
    }

}
