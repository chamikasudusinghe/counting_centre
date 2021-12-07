package com.example.counting_center.util;

public class GCM {

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String asciiToHex(String asciiStr) {
        char[] ch = asciiStr.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (char c : ch) {
            builder.append(Integer.toHexString(c).toUpperCase());
        }
        return builder.toString();
    }

}