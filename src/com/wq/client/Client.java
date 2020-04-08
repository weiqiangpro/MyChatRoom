package com.wq.client;


import com.wq.client.bean.ServerInfo;
import com.wq.clink.Context;
import com.wq.clink.core.impl.MyProvider;
import com.wq.clink.dispather.box.FileSendPacket;
import com.wq.utils.Foo;

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        File file = Foo.getCacheDir("client");
        Context.setup(new MyProvider());
        ServerInfo info = UDPSearcher.searchServer(10000);
        System.out.println("Server:" + info);
        if (info != null) {
            TCPClient tcpClient = null;
            try {
                tcpClient = TCPClient.startWith(info,file);
                if (tcpClient == null) {
                    return;
                }
                write(tcpClient);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (tcpClient != null) {
                    tcpClient.exit();
                }
            }
        }
        Context.close();
    }

    private static void write(TCPClient tcpClient) throws IOException {
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        do {
            String str = input.readLine();
            if ("00bye00".equalsIgnoreCase(str) || null == str || str.length() == 0) {
                break;
            }
            if (str.startsWith("--f")){
                String[] s = str.split(" ");
                if (s.length >=2){
                    String filePath = s[1];
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()){
                        FileSendPacket packet = new FileSendPacket(file,file.getName());
                        tcpClient.send(packet);
                        continue;
                    }
                }
            }
            tcpClient.send(str);

        } while (true);
    }

}
