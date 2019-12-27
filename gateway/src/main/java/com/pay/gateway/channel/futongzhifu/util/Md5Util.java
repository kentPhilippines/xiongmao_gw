package com.pay.gateway.channel.futongzhifu.util;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Md5Util {
    private static Log log = LogFactory.getLog(Md5Util.class);

    public Md5Util() {
    }

    public static String MD5Encode(String aData, String key) throws SecurityException {
        String resultString = null;

        try {
            aData = aData + "&key=" + key;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = bytes2HexString(md.digest(aData.getBytes(StandardCharsets.UTF_8)));
            return resultString;
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            throw new SecurityException("MD5运算失败");
        }
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";

        for (int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            ret = ret + hex.toUpperCase();
        }

        return ret;
    }
}