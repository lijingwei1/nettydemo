package com.netty.server;

import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class test {
    public static void main(String[] args) {
        Runnable runnable = () -> System.out.println("8888888888888888888");
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 3000, 10000, TimeUnit.MILLISECONDS);
        System.out.println("-----------------------");
        /*TimerTask task = new TimerTask(){
            @Override
            public void run() {
                System.out.println("+++++++++++++++++++++++");
            }
        };
        Timer timer = new Timer();
        System.out.println("------------------------");
        long delay = 3000;
        long intevalPeriod = 1 * 10000;
        timer.schedule(task, delay, intevalPeriod);*/
    }
}






























