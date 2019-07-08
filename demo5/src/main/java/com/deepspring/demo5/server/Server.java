package com.deepspring.demo5.server;

import com.deepspring.demol5.constants.TCPConstants;

import java.io.IOException;

/**
 * Created by fzy on 2019/7/8.
 */
public class Server {
    public static void main(String[] args) {
       TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP Server failed!");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);
        try{
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UDPProvider.stop();
        tcpServer.stop();
    }
}
