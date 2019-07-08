package com.deepspring.demo.csmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by fzy on 2019/7/4.
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000), 3000);
        System.out.println("已发起服务器连接");
        System.out.println("客户端信息:" + socket.getLocalAddress() + " port:" + socket.getLocalPort());
        System.out.println("服务器信息:" + socket.getInetAddress() + " port:" + socket.getPort());
        try {
            //发送数据
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }
        socket.close();
        System.out.println("客户端退出");
    }

    private static void todo(Socket client) throws IOException {
        //数据输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //得到socket输出流，转化为打印流
        OutputStream out = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(out);

        //得到socket输入流，转化为bufferreader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do {
            //读取一行
            String str = input.readLine();
            //发送服务器
            socketPrintStream.println(str);

            //从服务器读取一行
            String echo = socketBufferReader.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
            } else {
                System.out.println(echo);
            }
        } while (flag);

        //资源释放
        socketPrintStream.close();
        socketBufferReader.close();
    }
}
