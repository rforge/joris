package org.rosuda.util.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class InTimeBeanFactory {

    public static final int TIMEOUT = 60;

    public static <TYPE> TYPE provide(final Callable<TYPE> callable) {
        return provide(callable, TIMEOUT);
    }

    public static <TYPE> TYPE provide(final Callable<TYPE> callable, final int timeoutSeconds) {
        return new InTimeBeanFactory().doProvide(callable, timeoutSeconds);

    }

    private <TYPE> TYPE doProvide(final Callable<TYPE> callable, final int timeoutSeconds) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TYPE> future = executor.submit(callable);
        TYPE instance = null;
        try {
            instance = future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }
}
