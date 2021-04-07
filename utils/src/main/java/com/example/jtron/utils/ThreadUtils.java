package com.example.jtron.utils;

public final class ThreadUtils {

    private ThreadUtils() {}

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}