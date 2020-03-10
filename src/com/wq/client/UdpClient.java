package com.wq.client;


import com.wq.client.bean.ServerInfo;
import com.wq.utils.constants.ByteUtils;
import com.wq.utils.util.UDPConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UdpClient {
    public static List<ServerInfo> search() {
        CountDownLatch receiveCount = new CountDownLatch(1);
        CountDownLatch startCount = new CountDownLatch(1);
        Listener listener = null;
        try {
          listener = new Listener(UDPConstants.PORT_CLIENT_RESPONSE, startCount,receiveCount);
            listener.start();
            startCount.await();
            sendBroadcast();
            receiveCount.await();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return listener.getAndExit();
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDP广播开始");

        // 作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份请求数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        // 头部
        byteBuffer.put(UDPConstants.HEADER);
        // CMD命名
        byteBuffer.putShort((short) 1);
        // 回送端口信息
        byteBuffer.putInt(UDPConstants.PORT_CLIENT_RESPONSE);
        // 直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(byteBuffer.array(),
                byteBuffer.position() + 1);
        // 广播地址
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        // 设置服务器端口
        requestPacket.setPort(UDPConstants.PORT_SERVER);

        // 发送
        ds.send(requestPacket);
        ds.close();

        // 完成
        System.out.println("UDP广播结束");
    }
    private static class Listener extends Thread {
        private final int listenPort;
        private DatagramSocket ds = null;
        private boolean done = false;
        private int minlen = UDPConstants.HEADER.length + 2 + 4;
        private final List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>();
        private CountDownLatch startCount;
        private CountDownLatch receiveCount;

        public Listener(int listenPort, CountDownLatch startCount, CountDownLatch receiveCount) {
            super();
            this.listenPort = listenPort;
            this.startCount = startCount;
            this.receiveCount = receiveCount;
        }

        public void run() {
            super.run();
            try {
                ds = new DatagramSocket(listenPort);
                byte[] buffer = new byte[128];
                DatagramPacket receiveDP = new DatagramPacket(buffer, buffer.length);
                startCount.countDown();
                while (!done) {
                    ds.receive(receiveDP);
                    String serverIp = receiveDP.getAddress().getHostAddress();
                    int serverPort = receiveDP.getPort();
                    int serverDataLen = receiveDP.getLength();
                    byte[] serverData = receiveDP.getData();
                    boolean isValid = serverDataLen >= minlen
                            && ByteUtils.startsWith(serverData, UDPConstants.HEADER);
                    System.out.println("UDP搜索到服务器， ip:" + serverIp + "\tport:" + serverPort + "\tdataValid:" + isValid);
                    if (!isValid)
                        continue;

                    int index = UDPConstants.HEADER.length;
                    short cmd = (short) ((serverData[index++] << 8) | (serverData[index++] & 0xff));
                    int responsePort = (((serverData[index++]) << 24) |
                            ((serverData[index++] & 0xff) << 16) |
                            ((serverData[index++] & 0xff) << 8) |
                            ((serverData[index] & 0xff)));
                    if (cmd != 2 && responsePort <= 0) {
                        System.out.println("收到错误服务器消息");
                        continue;
                    }
                    String sn = new String(buffer, minlen, serverDataLen - minlen);
                    ServerInfo server = new ServerInfo(responsePort, serverIp, sn);
                    serverInfoList.add(server);
                    receiveCount.countDown();
                }
            } catch (IOException e) {
                System.out.println("UDP监听断开");
            } finally {
                close();
            }

        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        private List<ServerInfo> getAndExit() {
            done = true;
            close();
            return serverInfoList;
        }
    }


}
