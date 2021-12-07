package com.example.counting_center.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AESHelper {
    private static String padRight(String s, int n) {
        StringBuilder out = new StringBuilder(s);
        for (int i = 0; i < n; i++) {
            out.append(" ");
        }
        return out.toString();
    }

    public static String encrypt(String key, String message) {
        AES aes = new AES(key);
        String msg = padRight(message, 16 - (message.length() % 16));
        String encodedMsg = GCM.asciiToHex(msg);

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < encodedMsg.length() / 32; i++) {
            out.append(aes.encrypt(encodedMsg.substring(i * 32, (i + 1) * 32)));
        }
        return out.toString();
    }

    public static String decrypt(String key, String message) {
        AES aes = new AES(key);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < message.length() / 32; i++) {
            String msg = aes.decrypt(message.substring(i * 32, (i + 1) * 32));
            out.append(GCM.hexToAscii(msg));
        }
        return out.toString();
    }
}
