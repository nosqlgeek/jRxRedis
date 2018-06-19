package org.nosqlgeek.jrxredis.core.buffer;

import io.netty.handler.codec.redis.RedisMessage;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class SimpleRedisMsgBuffer implements IMsgBuffer<RedisMessage> {

    /**
     * The Message buffer's logger
     */
    private static final Logger LOG = Logger.getLogger(SimpleRedisMsgBuffer.class.getName());


    /**
     * A synchronized linked list
     */
    private LinkedBlockingDeque<RedisMessage> inner = new LinkedBlockingDeque<RedisMessage>();


    /**
     * Add a message to the inner queue
     *
     * @param msg
     */
    public void add(RedisMessage msg) {
        inner.addFirst(msg);
    }

    /**
     * Retrieve a message from the inner queue by blocking if not available
     *
     * @return
     */
    public RedisMessage retrieveBlocking(long timeout) throws TimeoutException {

        RedisMessage result = null;

        try {

            result = inner.pollLast(timeout, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {

                throw new TimeoutException("Could not retrieve message due to a timeout.");
        }

        return result;
    }


    /**
     *  Retrieve a message from the inner queue
     *
     * @return
     */
    public RedisMessage retrieveNow() {

        RedisMessage result = null;

        result = inner.pollLast();

        return result;

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
