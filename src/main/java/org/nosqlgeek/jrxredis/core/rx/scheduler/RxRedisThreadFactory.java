package org.nosqlgeek.jrxredis.core.rx.scheduler;

import java.util.concurrent.ThreadFactory;

/**
 * A thread factory for our scheduler
 */
public class RxRedisThreadFactory implements ThreadFactory {

    /**
     * The name of the thread
     */
    private static final String NAME = "rx-redis";


    /**
     * Thread count
     */
    private int count = 0;

    /**
     * Creates a new thread
     *
     * @param r
     * @return
     */
    @Override
    public Thread newThread(Runnable r) {

        return new Thread(r, NAME + "-" + count++);
    }
}
