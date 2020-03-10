package com.wq.client;


import com.wq.client.bean.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.awt.SystemColor.info;

public class ClientTest {
    private static boolean done;

    public static void main(String[] args) throws Exception {
        ServerInfo info = UdpClient.search().get(0);


        // 当前连接数量
        int size = 0;
        final List<TcpClient> tcpClients = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            try {
                TcpClient tcpClient = TcpClient.linkTest(info);
                if (tcpClient == null) {
                    System.out.println("连接异常");
                    continue;
                }
                tcpClients.add(tcpClient);
                System.out.println("连接成功：" + (++size));
            } catch (IOException e) {
                System.out.println("连接异常");
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("11111");
        Scanner c = new Scanner(System.in);
        String s = c.nextLine();
        System.out.println(s);
        Runnable runnable = () -> {
            while (!done) {
                for (TcpClient tcpClient : tcpClients) {
                    tcpClient.socketPrintStream.println("Hello~~");
                    System.out.println("hello");
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
    }


}
