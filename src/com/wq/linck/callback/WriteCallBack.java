package com.wq.linck.callback;

public abstract class WriteCallBack implements Runnable {
   private String str ;
    public    void setIoArgs(String str){
        this.str = str;
    };
    @Override
    public void run() {
        canProviderOutput(str);
    }

    protected abstract void canProviderOutput(String str);
}

