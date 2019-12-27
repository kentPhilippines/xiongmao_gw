package com.pay.gateway.channel.futongzhifu.util;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import java.util.Iterator;


public class SignUtil {
    public SignUtil() {
    }

    public static String sortData(Map<String, String> sourceMap) {
        Map<String, String> sortedMap = new TreeMap();
        sortedMap.putAll(sourceMap);
        Set<Entry<String, String>> entrySet = sortedMap.entrySet();
        StringBuffer sbf = new StringBuffer();
        Iterator var4 = entrySet.iterator();

        while (var4.hasNext()) {
            Entry<String, String> entry = (Entry) var4.next();
            String value;
            if (!(entry.getValue() instanceof String)) {
                value = String.valueOf(entry.getValue());
            } else {
                value = entry.getValue();
            }

            if (isNotEmpty(value)) {
                sbf.append(entry.getKey()).append("=").append(value).append("&");
            }
        }

        String returnStr = sbf.toString();
        if (returnStr.endsWith("&")) {
            returnStr = returnStr.substring(0, returnStr.lastIndexOf(38));
        }

        return returnStr;
    }

    public static String sortDataWithoutSign(Map<String, String> sourceMap) {
        Map<String, String> sortedMap = new TreeMap();
        sortedMap.putAll(sourceMap);
        Set<Entry<String, String>> entrySet = sortedMap.entrySet();
        StringBuffer sbf = new StringBuffer();
        Iterator var4 = entrySet.iterator();

        while (var4.hasNext()) {
            Entry<String, String> entry = (Entry) var4.next();
            if (isNotEmpty(entry.getValue()) && !"sign".equals(entry.getKey()) && !"sign_type".equals(entry.getKey())) {
                sbf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        String returnStr = sbf.toString();
        if (returnStr.endsWith("&")) {
            returnStr = returnStr.substring(0, returnStr.lastIndexOf(38));
        }

        return returnStr;
    }

    public static String sortValueData(Map<String, String> sourceMap) {
        Map<String, String> sortedMap = new TreeMap();
        sortedMap.putAll(sourceMap);
        Set<Entry<String, String>> entrySet = sortedMap.entrySet();
        StringBuffer sbf = new StringBuffer();
        Iterator var4 = entrySet.iterator();

        while (var4.hasNext()) {
            Entry<String, String> entry = (Entry) var4.next();
            String value;
            if (!(entry.getValue() instanceof String)) {
                value = String.valueOf(entry.getValue());
            } else {
                value = entry.getValue();
            }

            if (isNotEmpty(value)) {
                sbf.append(value);
            }
        }

        String returnStr = sbf.toString();
        return returnStr;
    }

    public static void putInMap(Map<String, String> map, String key, String value) {
        if (isNotEmpty(value)) {
            map.put(key, value);
        }
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
