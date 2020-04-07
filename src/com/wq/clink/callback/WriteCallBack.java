package com.wq.clink.callback;

import com.wq.clink.core.IoArgs;

public abstract class WriteCallBack implements Runnable {

    @Override
    public void run() {
        canProviderOutput();
    }

    protected abstract void canProviderOutput();
}

