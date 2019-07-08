package com.deepspring.demo.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * UDP 搜索者，用于搜索服务支持方
 * Created by fzy on 2019/7/5.
 */
public class UDPSearcher {

    private static final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher started");
        Listener listener = listen();
        sendBroadcast();
        //读取任意信息，然后退出
        System.in.read();
        List<Device> devices = listener.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println("Device:" + device.toString());
        }
        System.out.print("UDPSearcher Finished");
    }

    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher start listen");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast started");

        //作为搜索方无需指定端口，让系统自动分配
        DatagramSocket ds = new DatagramSocket();

        //构建一份请求数据
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();
        //直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes, requestDataBytes.length);
        //20000端口，广播地址
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(20000);
        //发送
        ds.send(requestPacket);
        ds.close();
        //完成
        System.out.println("UDPSearcher sendBroadcast Finished");
    }

    private static class Device {
        int port;
        String ip;
        String sn;

        public Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    private static class Listener extends Thread {
        private final int linstenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds = null;

        public Listener(int linstenPort, CountDownLatch countDownLatch) {
            super();
            this.linstenPort = linstenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();
            //通知已启动
            countDownLatch.countDown();
            try {
                ds = new DatagramSocket(linstenPort);
                while (!done) {
                    //构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receiverPack = new DatagramPacket(buf, buf.length);
                    //接收
                    ds.receive(receiverPack);
                    //打印接收到的信息与发送者信息
                    //发送者的IP地址
                    String ip = receiverPack.getAddress().getHostAddress();
                    int port = receiverPack.getPort();
                    int dataLen = receiverPack.getLength();
                    String data = new String(receiverPack.getData(), 0, dataLen);
                    System.out.println("UDPSearcher receive from ip:" + ip + "\tport:" +
                            port + "\tdata" + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        Device device = new Device(port, ip, sn);
                        devices.add(device);
                    }
                }
            } catch (Exception e) {

            } finally {
                close();
            }
            System.out.println("UDPSearcher listener finished");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }
}
