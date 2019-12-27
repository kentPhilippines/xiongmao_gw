package com.pay.gateway.channel.futongzhifu.util;
import java.util.HashMap;
import java.util.Map;

public class HtmlUtil {
    public static String map2From(String subUrl, Map<String, String> param) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>");
        stringBuilder.append("\r\n");
        stringBuilder.append("<body>");
        stringBuilder.append("\r\n");
        stringBuilder.append("<form id=\"submitForm\" method=\"post\" name=\"submitFomm\" action=\"" + subUrl + "\">");
        stringBuilder.append("\r\n");
        if (param == null) {
            param = new HashMap<>();
        }
        for (Map.Entry<String, String> entry : param.entrySet()) {
            stringBuilder.append(
                    "<input  type=\"text\"  name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\" />");
            stringBuilder.append("\r\n");
        }

        stringBuilder.append("<input type=\"submit\" value=\"提交支付请求\" id=\"submit\">	 </form>");
        stringBuilder.append("\r\n");
        stringBuilder.append("</body>");
        stringBuilder.append("\r\n");
        stringBuilder.append("</html>");
        return stringBuilder.toString();
    }
}
