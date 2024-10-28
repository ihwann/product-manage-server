package com.musinsa.productmanageserver.common.util;

public class RedisKeyGenerator {
    public static String generateKey(String prefix, String... params) {
        StringBuilder sb = new StringBuilder(prefix);

        for (String param : params) {
            sb.append(":");
            sb.append(param);
        }

        return sb.toString();
    }
}
