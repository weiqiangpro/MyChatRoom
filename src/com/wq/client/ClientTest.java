package com.wq.client;

import com.wq.client.UDPSearcher;
import com.wq.client.bean.ServerInfo;
import com.wq.clink.Context;
import com.wq.clink.core.impl.MyProvider;
import com.wq.utils.Foo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    private static boolean done;

    public static void main(String[] args) throws IOException {
        File cachePath = Foo.getCacheDir("client/test");
        Context.setup(new MyProvider());
        ServerInfo info = UDPSearcher.searchServer(10000);
        System.out.println("Server:" + info);
        if (info == null) {
            return;
        }
        // 当前连接数量
        int size = 0;
        final List<TCPClient> tcpClients = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            System.out.println(i);
            try {
                TCPClient tcpClient = TCPClient.startWith(info, cachePath);
                if (tcpClient == null) {
                   throw  new NullPointerException();
                }

                tcpClients.add(tcpClient);

                System.out.println("连接成功：" + (++size));

            } catch (IOException | NullPointerException e) {
                System.out.println("连接异常");
                break;
            }

//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }


        System.in.read();

        Runnable runnable = () -> {
            while (!done) {
                for (TCPClient tcpClient : tcpClients) {
                    tcpClient.send("Hello~~");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        System.in.read();

        // 等待线程完成
        done = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 客户端结束操作
        for (TCPClient tcpClient : tcpClients) {
            tcpClient.exit();
        }

        Context.close();

    }


}
