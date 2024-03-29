package com.deepspring.demo.udp;

/**
 * Created by fzy on 2019/7/5.
 */
public class MessageCreator {
    private static final String SN_HEADER = "sn收到口令:";
    public static final String PORT_HEADER = "口令,回复端口:";

    public static String buildWithPort(int port) {
        return PORT_HEADER + port;
    }

    public static int parsePort(String data) {
        if (data.startsWith(PORT_HEADER)) {
            return Integer.parseInt(data.substring(PORT_HEADER.length()));
        }
        return -1;
    }

    public static String buildWithSn(String sn) {
        return SN_HEADER +sn;
    }

    public static String parseSn(String data) {
        if (data.startsWith(SN_HEADER)) {
            return data.substring(SN_HEADER.length());
        }
        return null;
    }
}
