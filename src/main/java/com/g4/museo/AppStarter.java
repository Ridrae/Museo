package com.g4.museo;

public class AppStarter implements Runnable{

    public static void main(String[] args) {
        MuseoApplication.main(args);
    }

    public static void stop() {
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        //no need to run anything here
    }
}
