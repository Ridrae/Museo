package com.g4.museo;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppStarter implements Runnable{

    public static void main(String[] args) {
        MuseoApplication.main(args);
    }

    public static void stop() {
        System.out.println("shutdown");
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
    }
}
