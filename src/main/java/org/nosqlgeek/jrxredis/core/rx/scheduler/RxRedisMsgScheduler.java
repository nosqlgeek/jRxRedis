package org.nosqlgeek.jrxredis.core.rx.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

/**
 * Observables can run on schedulers, this means that actions on the observables
 * are executed on specific threads.
 *
 * This is a simple scheduler which uses a fixed size thread pool
 */
public class RxRedisMsgScheduler extends Scheduler {


    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(RxRedisMsgScheduler.class.getName());

    /**
     * A fixed size thread pool
     */
    private final Executor exec;

    /**
     * Wrapping the fixed size thread pool
     */
    private final Scheduler inner;

    /**
     * A thread factory
     */
    private final ThreadFactory factory;


    /**
     * Number of threads
     */
    private int numThreads;

    /**
     * Default Ctor
     *
     * Uses 8 threads
     */
    public RxRedisMsgScheduler() {

        this(8);
    }

    /**
     * Ctor which accepts the number of threads
     *
     * @param numThreads
     */
    public RxRedisMsgScheduler(int numThreads) {

        this.numThreads = numThreads;

        factory = new RxRedisThreadFactory();
        exec = Executors.newFixedThreadPool(numThreads, factory);
        inner = Schedulers.from(exec);
    }


    /**
     * Returns a worker of our thread pool
     *
     * @return
     */
    @Override
    public Worker createWorker() {

        return inner.createWorker();
    }

    /**
     * Retrieve the thread pool size
     *
     * @return
     */
    public int getNumThreads() {
        return numThreads;
    }
}
