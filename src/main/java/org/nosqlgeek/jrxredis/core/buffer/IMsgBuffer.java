package org.nosqlgeek.jrxredis.core.buffer;

import java.util.concurrent.TimeoutException;

public interface IMsgBuffer<T> {


    /**
     * Add a message to the buffer
     * @param msg
     */
    public void add(T msg);

    /**
     * Retrieve the last message from the buffer by waiting until it becomes available
     * @return
     */
    public T retrieveBlocking(long timout) throws TimeoutException;

    /**
     * Retrieves the last message from the buffer
     * @return
     */
    public T retrieveNow();

    /**
     * Size of the buffer
     *
     * @return
     */
    public int getSize();

    /**
     * Flush the buffer
     */
    public void flush();

}
