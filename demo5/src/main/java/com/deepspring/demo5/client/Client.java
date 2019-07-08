package com.deepspring.demo5.client;

import com.deepspring.demo5.client.bean.ServerInfo;

import java.io.IOException;

/**
 * Created by fzy on 2019/7/8.
 */
public class Client {
    public static void main(String[] args) {
        ServerInfo info = UDPSearcher.searchServer(100000);//超时
        System.out.println("Server:" + info);

        if (info != null) {
            try {
               TCPClient.linkwith(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
