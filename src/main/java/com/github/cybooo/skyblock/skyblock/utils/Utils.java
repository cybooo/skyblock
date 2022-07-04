package com.github.cybooo.skyblock.skyblock.utils;

public class Utils {

    public static String millisToDate(long millis) {
        return new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date(millis));
    }

}
