package com.deepspring.demol5.server;

import com.deepspring.demol5.constans.TCPConstants;

import java.io.IOException;

/**
 * Created by fzy on 2019/7/8.
 */
public class Server {
    public static void main(String[] args) {
        ServerProvider.start(TCPConstants.PORT_SERVER);

        try{
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerProvider.stop();
    }
}
