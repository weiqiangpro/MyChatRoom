package com.wq.clink.core.frames;

import com.wq.clink.core.Frame;
import com.wq.clink.core.IoArgs;

import java.io.IOException;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:54
 */
public class CancelSendFrame  extends  AbsSendFrame{
    public CancelSendFrame(short indetifier) {
        super(0, Frame.TYPE_COMMAND_SEND_CANCEL, Frame.FLAG_NONE, indetifier,null);
    }

    @Override
    protected int consumeBody(IoArgs args) throws IOException {
        return 0;
    }

    @Override
    public Frame nextFrame() {
        return null;
    }
}
