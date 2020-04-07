package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public abstract class AbsReceiveFrame extends Frame {

    volatile int bodyRemaining;

    public AbsReceiveFrame(byte[] hander) {
        super(hander);
        bodyRemaining = getBodyLength();
    }

    @Override
    public int getConsumableLength() {
        return bodyRemaining;
    }

    @Override
    public synchronized boolean handle(IoArgs args) throws IOException {
        if (bodyRemaining == 0)
            return  true;
        bodyRemaining -= consumBody(args);
        return bodyRemaining==0;
    }

    protected abstract int consumBody(IoArgs args) throws IOException;

    @Override
    public final Frame nextFrame() {
        return null;
    }


}
