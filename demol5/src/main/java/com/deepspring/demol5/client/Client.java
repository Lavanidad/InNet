package com.deepspring.demol5.client;

import com.deepspring.demol5.client.bean.ServerInfo;
import com.deepspring.demol5.server.Server;

/**
 * Created by fzy on 2019/7/8.
 */
public class Client {
    public static void main(String[] args) {
        ServerInfo info = ClientSearcher.searchServer(100000);//超时
        System.out.println("Server:" + info);
    }
}
