package org.nosqlgeek.jrxredis.core.rx;

import io.netty.handler.codec.redis.RedisMessage;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import org.nosqlgeek.jrxredis.core.rx.scheduler.RxRedisMsgScheduler;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Helps to generate Redis Message Observables
 *
 * The core was initially implemented in a way that it returns futures
 */
public class RxRedisMsgObservableFactory {

    /**
     * The scheduler on which Redis Message Observables are running
     */
    private static final Scheduler scheduler = new RxRedisMsgScheduler();


    /**
     * Retrieve an Observable from a Future. A background thread of the
     * scheduler will be used in order to check if the future returned any value
     *
     * @param f
     * @return
     */
    public static Observable<RedisMessage> newObservable(Future<RedisMessage> f) {

        return Observable.fromFuture(f, scheduler);
    }


    /**
     * Should throw a timeout exception when the future doesn't return within
     * n miliseconds
     *
     * @param f
     * @param timeout
     * @return
     */
    public static Observable<RedisMessage> newObservable(Future<RedisMessage> f, long timeout) {

        return Observable.fromFuture(f, timeout, TimeUnit.MILLISECONDS, scheduler);
    }



}
