package com.wq.clink.core;

import com.wq.clink.dispather.dispathercal.IoArgsCallback;
import com.wq.clink.dispather.dispathercal.IoArgsEventProcessor;

import java.io.IOException;

/**
 * @Author: weiqiang
 * @Time: 2020/3/20 下午10:21
 */
public interface Sender {
    boolean senderAsync() throws IOException;
    void setSendProcessor(IoArgsEventProcessor callBack) ;
}
