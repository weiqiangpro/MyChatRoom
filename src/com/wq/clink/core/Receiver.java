package com.wq.clink.core;

import com.wq.clink.callback.OnArrivedAndReadNext;
import com.wq.clink.dispather.dispathercal.IoArgsCallback;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @Author: weiqiang
 * @Time: 2020/3/20 下午10:21
 */
public interface Receiver {
    boolean receiveAsync(IoArgs ioArgs, IoArgsCallback callBack) throws IOException;
}
