package com.wq.client;


import com.wq.client.bean.ServerInfo;

import java.util.List;

public class Client {
    public static void main(String[] args) throws Exception {
        List<ServerInfo> search = UdpClient.search();

        for (ServerInfo serverInfo : search) {
            System.out.println(serverInfo);
            TcpClient.link(serverInfo);
        }
    }
}
