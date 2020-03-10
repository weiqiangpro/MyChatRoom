package com.wq.client;

import com.wq.client.bean.ServerInfo;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;

public class TcpClient {
    public PrintStream socketPrintStream ;

    public TcpClient(PrintStream socketPrintStream) {
        this.socketPrintStream = socketPrintStream;
    }

    public static TcpClient linkTest(ServerInfo info) throws Exception {
        Socket socket = new Socket();
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000);
        try {
            CountDownLatch start = new CountDownLatch(1);
            ReadHandle readHandle = new ReadHandle(socket.getInputStream(), start);
            readHandle.start();
            start.await();
        } catch (Exception e) {
            System.out.println("异常关闭");
            return null;
        }
        return new TcpClient(new PrintStream(socket.getOutputStream()));

    }

    public static void link(ServerInfo info) throws Exception {
        Socket socket = new Socket();
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000);
        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());
        try {
            CountDownLatch start = new CountDownLatch(1);

            ReadHandle readHandle = new ReadHandle(socket.getInputStream(), start);
            readHandle.start();
            start.await();
            // 发送接收数据
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        // 释放资源
        socket.close();
        System.out.println("客户端已退出～");
    }

    private static void todo(Socket client) throws IOException {
        // 构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        // 得到Socket输出流，并转换为打印流
        PrintStream socketPrintStream = new PrintStream(client.getOutputStream());
        boolean flag = true;
        do {
            // 键盘读取一行
            String str = input.readLine();
            // 发送到服务器
            socketPrintStream.println(str);
            // 从服务器读取一行
            if ("00bye00".equalsIgnoreCase(str)) {
                break;
            }
        } while (flag);
        // 资源释放
        socketPrintStream.close();
    }

    private static class ReadHandle extends Thread {
        private boolean done = false;
        private InputStream inputStream = null;
        private CountDownLatch start = null;

        public ReadHandle(InputStream inputStream, CountDownLatch start) {
            this.inputStream = inputStream;
            this.start = start;
        }

        @Override
        public void run() {
            super.run();
            // 得到Socket输入流，并转换为BufferedReader
            BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String echo = null;
                start.countDown();
                while (!done) {
                    try {
                        // 客户端拿到一条数据
                        echo = socketBufferedReader.readLine();
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                    if (echo == null) {
                        System.out.println("连接已关闭，无法读取数据！");
                        exit();
                        break;
                    }
                    if ("bye".equalsIgnoreCase(echo)) {
                        done = true;
                    } else {
                        System.out.println(echo);
                    }
                }
            } catch (IOException e) {
                exit();
                e.printStackTrace();
                System.out.println("出错");
            }
        }
        void exit() {
            done = true;
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
