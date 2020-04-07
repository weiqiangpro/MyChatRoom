package com.wq.clink.callback;

import com.wq.clink.core.IoArgs;

import java.util.concurrent.Callable;

public abstract class ReadCallBack implements Runnable {
    @Override
    public void run() {
         canProviderInput();
    }

    protected abstract void canProviderInput();
}

