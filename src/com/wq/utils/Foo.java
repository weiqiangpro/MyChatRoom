package com.wq.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Author: weiqiang
 * Time: 2020/4/6 下午8:36
 */
public class Foo {
    private static final String CACHE_DIR = "cach";

    public static File getCacheDir(String dir){

        String path = System.getProperty("user.dir")+(File.separator+CACHE_DIR+File.separator+dir);
        File file = new File(path);
        if (!file.exists()){
            if (!file.mkdirs()){
                throw new RuntimeException("创建文件目录失败");
            }
        }
        return file;
    }

    public static File createRandomTemp(File parent){
        String name = UUID.randomUUID().toString()+".temp";
        File file = new File(parent,name);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;

    }
}
