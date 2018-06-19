package org.nosqlgeek.jrxredis.core.netty;

import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.buffer.IRedisMsgBuffer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Redis Message Future
 *
 */
public class RedisMsgFuture implements Future<RedisMessage> {

    /**
     * Reference to the incoming messages buffer
     */
    private final IRedisMsgBuffer in;

    /**
     * Reference to the outgoing messages
     */
    private final IRedisMsgBuffer out;


    /**
     * The request which belongs to this Future
     */
    private RedisMessage request;

    /**
     * The response which belongs to this Future
     */
    private RedisMessage response;


    /**
     * Cto which accepts the incoming message buffer as argument
     *
     * @param in
     */
    public RedisMsgFuture(IRedisMsgBuffer in, IRedisMsgBuffer out) {

        this.in = in;
        this.out = out;
    }


    /**
     * We don't allow to cancel
     *
     * @param mayInterruptIfRunning
     * @return
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {

        //Do nothing
        return false;
    }

    /**
     * We don't allow to cancel
     * @return
     */
    @Override
    public boolean isCancelled() {
        return false;
    }


    @Override
    public boolean isDone() {

        boolean result = false;

        if ( (in.getSize() == out.getSize()) && (in.getSize() > 0) ) result = true;

        return result;
    }



    @Override
    public RedisMessage get() throws InterruptedException, ExecutionException {


        if (isDone()) {

            this.response = in.retrieveNow();
            this.request = out.retrieveNow();

            return this.response;

        } else {
            return null;
        }


    }

    @Override
    public RedisMessage get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (unit.equals(TimeUnit.MILLISECONDS)) {

            this.response = in.retrieveBlocking(timeout);
            this.request = out.retrieveNow();

            return this.response;

        } else {

            throw new ExecutionException(new Exception("Only miliseconds are supported as timout value."));
        }


    }

    /**
     * Access the request which belongs to this Future
     * @return
     */
    public RedisMessage getRequest() {
        return request;
    }

    /**
     * Access the response which belongs to this Future
     * @return
     */
    public RedisMessage getResponse() {
        return response;
    }
}
