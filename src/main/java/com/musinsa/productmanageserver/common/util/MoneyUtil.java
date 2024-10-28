package com.musinsa.productmanageserver.common.util;

public class MoneyUtil {

    public static String convertStringFormat(Integer money) {
        return String.format("%,d", money);
    }

}
