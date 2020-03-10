package com.wq.linck.callback;

public abstract class ReadCallBack implements Runnable {

    @Override
    public void run() {
        canProviderInput();
    }

    protected abstract void canProviderInput();
}

