package org.nosqlgeek.jrxredis.core.buffer;

import io.netty.handler.codec.redis.RedisMessage;
import java.util.concurrent.TimeoutException;

public interface IRedisMsgBuffer {


    /**
     * Add a message to the buffer
     * @param msg
     */
    public void add(RedisMessage msg);

    /**
     * Retrieve the last message from the buffer by waiting until it becomes available
     * @return
     */
    public RedisMessage retrieveBlocking(long timout) throws TimeoutException;

    /**
     * Retrieves the last message from the buffer
     * @return
     */
    public RedisMessage retrieveNow();

    /**
     * Size of the buffer
     *
     * @return
     */
    public int getSize();

}
