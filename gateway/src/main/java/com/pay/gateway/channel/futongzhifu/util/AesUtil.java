package com.pay.gateway.channel.futongzhifu.util;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AesUtil {
    private static Log log = LogFactory.getLog(AesUtil.class);

    public AesUtil() {
    }

    public static String encrypt(String input, String key) {
        byte[] crypted = null;

        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, skey);
            crypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception var5) {
            log.error("加密错误", var5);
        }

        return parseByte2HexStr(crypted);
    }

    public static String decrypt(String hexStr, String key) {
        byte[] output = null;

        try {
            byte[] decByte = parseHexStr2Byte(hexStr);
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, skey);
            output = cipher.doFinal(decByte);
        } catch (Exception var7) {
            log.error("解密错误", var7);
            return null;
        }

        return new String(output, StandardCharsets.UTF_8);
    }

    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < buf.length; ++i) {
            String hex = Integer.toHexString(buf[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / 2];

            for (int i = 0; i < hexStr.length() / 2; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }

            return result;
        }
    }
}