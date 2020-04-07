package com.wq.clink.dispather.dispathercal;

import com.wq.clink.core.IoArgs;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午2:47
 */
public interface IoArgsEventProcessor {

    IoArgs provideIoArgs();
    void onFiled(IoArgs args,Exception e);
    void onCompleted(IoArgs args);
}
