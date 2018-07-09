package org.nosqlgeek.jrxredis.core.buffer;

import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.netty.error.TimeoutErr;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncRedisMsgBuffer implements IMsgBuffer<Future<RedisMessage>>{

    /**
     * A synchronized linked list
     */
    private LinkedBlockingDeque<Future<RedisMessage>> inner = new LinkedBlockingDeque<Future<RedisMessage>>();


    @Override
    public void add(Future<RedisMessage> msg) {

        inner.addFirst(msg);
    }

    /**
     * We are putting the future already in when sending the request, so it's not necessary for wait for it
     *
     * @param timeout
     * @return
     * @throws TimeoutException
     */
    @Override
    public Future<RedisMessage> retrieveBlocking(long timeout) throws TimeoutException {

        Future<RedisMessage> result = null;

        try {

            result = inner.pollLast(timeout, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {

            throw new TimeoutErr();
        }

        return result;
    }


    @Override
    public Future<RedisMessage> retrieveNow() {

        return inner.pollLast();
    }

    @Override
    public int getSize() {

        return this.inner.size();
    }

    @Override
    public void flush() {

        this.inner.clear();
    }
}
