package com.lucky.platform.ioDemo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author: Loki
 * @data: 2021-09-21 17:35
 **/
public class SocketClient {


    public static void main(String[] args) throws Exception {
        bioClientTest();
    }
    public static void bioClientTest() throws Exception{
        Socket client = new Socket("192.168.1.5",9090);
        client.setSendBufferSize(20);
        client.setTcpNoDelay(true);
        OutputStream out = client.getOutputStream();
        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while(true){
            String line = reader.readLine();
            if(line != null ){
                byte[] by = line.getBytes();
                for (byte b : by) {
                    out.write(b);
                }
            }
        }
    }

}
