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
     * Incoming messages
     */
    private final IRedisMsgBuffer in;

    /**
     * Outgoing messages
     */
    private final IRedisMsgBuffer out;


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

            RedisMessage received = in.retrieveNow();
            RedisMessage sent = out.retrieveNow();


            return received;
        } else {
            return null;
        }


    }

    @Override
    public RedisMessage get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (unit.equals(TimeUnit.MILLISECONDS)) {

            RedisMessage received = in.retrieveBlocking(timeout);
            RedisMessage sent = out.retrieveNow();

            return received;

        } else {

            throw new ExecutionException(new Exception("Only miliseconds are supported as timout value."));
        }


    }
}
