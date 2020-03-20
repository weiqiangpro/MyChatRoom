package com.wq.linck.callback;

import java.util.concurrent.Callable;

public abstract class ReadCallBack implements Callable {

    @Override
    public String call() {
        return canProviderInput();
    }

    protected abstract String canProviderInput();
}

