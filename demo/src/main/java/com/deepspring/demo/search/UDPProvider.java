package com.deepspring.demo.search;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * UDP提供者，提供服务
 * Created by fzy on 2019/7/5.
 */
public class UDPProvider {

    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider start");
        //作为接受者，指定一个端口进行接收
        DatagramSocket ds = new DatagramSocket(20000);

        //构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

        //接收
        ds.receive(receivePack);
        //打印接收到的信息与发送者的信息
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int dataLen = receivePack.getLength();
        String data = new String(receivePack.getData(), 0, dataLen);
        System.out.println("UDPProvider receive from ip:" + ip + "\tport:" + port
                + "\tdata:" + data);

        //构建回送数据
        String responseData = "Receive data with len:" + dataLen;
        byte[] responseDataBytes = responseData.getBytes();
        //直接根据发送者构建一份回送信息
        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes, responseDataBytes.length,
                receivePack.getAddress(), receivePack.getPort());
        ds.send(responsePacket);
        //完成
        System.out.println("UDPProvider Finished");
        ds.close();
    }
}
