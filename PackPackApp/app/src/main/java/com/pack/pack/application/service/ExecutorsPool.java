package com.pack.pack.application.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Saurav on 24-02-2017.
 */
public class ExecutorsPool {

    public static ExecutorsPool INSTANCE = new ExecutorsPool();

    private ExecutorService tPool;

    private ExecutorsPool() {
        tPool = Executors.newCachedThreadPool();
    }

    public void submit(Runnable runnable) {
        tPool.submit(runnable);
    }
}
