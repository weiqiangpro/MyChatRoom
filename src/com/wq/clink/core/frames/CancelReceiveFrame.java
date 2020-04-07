package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public class CancelReceiveFrame extends  AbsReceiveFrame{

    public CancelReceiveFrame(byte[] hander) {
        super(hander);
    }

    @Override
    protected int consumBody(IoArgs args) {
        return 0;
    }


}
