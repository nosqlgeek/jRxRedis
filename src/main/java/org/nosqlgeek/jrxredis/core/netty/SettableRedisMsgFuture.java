package org.nosqlgeek.jrxredis.core.netty;

import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.helper.StopWatch;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SettableRedisMsgFuture implements Future<RedisMessage> {

    /**
     * Request
     */
    private RedisMessage out;

    /**
     * Response
     */
    private RedisMessage in;


    /**
     * Init the future by passing the request
     *
     * @param out
     */
    public SettableRedisMsgFuture(RedisMessage out) {

        this.out = out;
    }


    /**
     * Canceling this future is not supported
     *
     * @param mayInterruptIfRunning
     * @return
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * Canceling this future is not supported
     *
     * @return
     */
    @Override
    public boolean isCancelled() {
        return false;
    }

    /**
     * The future is done if the request and the response are set
     *
     * @return
     */
    @Override
    public boolean isDone() {

        if ( in != null && out != null) return true;

        return false;
    }

    /**
     * Provides access to the response
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public RedisMessage get() throws InterruptedException, ExecutionException {

        return in;
    }

    /**
     * Waits until the response arrived or the request timed out
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Override
    public RedisMessage get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (TimeUnit.MILLISECONDS == unit) {

            StopWatch sw = new StopWatch();
            sw.start();

            while (in == null && sw.elapsed() < timeout) {

                //0.1 ms
                Thread.sleep(0, 100000);
            }

            if (in == null) throw new TimeoutException("Operation timed out!");
            else return in;

        } else {

            throw new ExecutionException(new Exception("Only milliseconds are supported as timeout value."));
        }
    }

    /**
     * Provide the response to the future
     */
    public void setIn(RedisMessage in) {
        this.in = in;
    }

    /**
     * Provide access to the outgoing message
     *
     * @return
     */
    public RedisMessage getOut() {
        return out;
    }
}
