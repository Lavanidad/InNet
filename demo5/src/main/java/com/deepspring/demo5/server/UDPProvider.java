package com.deepspring.demo5.server;

import com.deepspring.demol5.constants.UDPConstants;
import com.deepspring.lib2.utils.ByteUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by fzy on 2019/7/8.
 */
public class UDPProvider {
    private static Provider PROVIDER_INSTANCE;

    static void start(int port) {
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn, port);
        provider.start();
        PROVIDER_INSTANCE = provider;
    }

    static void stop() {
        if (PROVIDER_INSTANCE != null) {
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread {
        private final byte[] sn;
        private final int port;
        private boolean done = false;
        private DatagramSocket ds = null;
        //存储消息的buffer
        final byte[] buffer = new byte[128];

        Provider(String sn, int port) {
            super();
            this.sn = sn.getBytes();
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider started");
            try {
                // 监听20000端口
                ds = new DatagramSocket(UDPConstants.PORT_SERVER);
                // 接收消息packet
                DatagramPacket receiverPacket = new DatagramPacket(buffer, buffer.length);
                while (!done) {
                    //接收
                    ds.receive(receiverPacket);
                    // 打印接收到的信息与发送者的信息
                    //发送者的IP地址
                    String clientIp = receiverPacket.getAddress().getHostAddress();
                    int clientPort = receiverPacket.getPort();
                    int clientDataLength = receiverPacket.getLength();
                    byte[] clientData = receiverPacket.getData();
                    boolean isValid = clientDataLength >= (UDPConstants.HEADER.length + 2 + 4) &&
                           ByteUtils.startsWith(clientData, UDPConstants.HEADER);
                    System.out.println("ServerProvider receiver from ip:" + clientIp + "\tport:" +
                            clientPort + "\tdatavalid:" + isValid);
                    if (!isValid) {
                        //无效继续
                        continue;
                    }
                    //解析命令与发送回送端口
                    int index = UDPConstants.HEADER.length;
                    short cmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xff));
                    int responsePort = (clientData[index++] << 24) | ((clientData[index++] & 0xff) << 16) |
                            ((clientData[index++] & 0xff) << 8) | (clientData[index] & 0xff);
                    //判断合法性
                    if (cmd == 1 && responsePort > 0) {
                        //构建回送数据
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        byteBuffer.put(UDPConstants.HEADER);
                        byteBuffer.putShort((short) 2);
                        byteBuffer.putInt(port);
                        byteBuffer.put(sn);
                        int len = byteBuffer.position();
                        //直接根据发送者构建回送消息
                        DatagramPacket responsePacket = new DatagramPacket(buffer, len,
                                receiverPacket.getAddress(), responsePort);
                        ds.send(responsePacket);
                        System.out.println("ServerProvider response to:" + clientIp + "\tport:"
                                + responsePort + "\tdataLen" + len);
                    } else {
                        System.out.println("ServerProvider receive cmd nonsupport; cmd:"
                                + cmd + "\tport" + port);
                    }
                }
            } catch (Exception e) {
            } finally {
                close();
            }
            System.out.println("Server Finished");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        private void exit() {
            done = true;
            close();
        }
    }
}
