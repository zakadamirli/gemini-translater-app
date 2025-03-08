package com.zekademirli.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class GeminiAuthUtil {

    public static void main(String[] args) throws Exception {
        generateSignature("demirli", "1233");
    }

    public static void generateSignature(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA384");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(payload.getBytes());
        System.out.println(Base64.getEncoder().encodeToString(hash));
    }
}