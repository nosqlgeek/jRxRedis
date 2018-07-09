package org.nosqlgeek.jrxredis.core.netty;

import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.helper.StopWatch;
import org.nosqlgeek.jrxredis.core.netty.error.NoResultErr;
import org.nosqlgeek.jrxredis.core.netty.error.TimeoutErr;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class SettableRedisMsgFuture implements Future<RedisMessage> {

    private static Logger LOG = Logger.getLogger(SettableRedisMsgFuture.class.getName());

    /**
     * Request
     */
    private RedisMessage out;

    /**
     * Response
     */
    private RedisMessage in;

    /**
     * In order to wait for the result
     */
    private CountDownLatch completeSignal = new CountDownLatch(1);


    /**
     * Just for benchmarking purposes
     */
    private StopWatch sw = new StopWatch();


    /**
     * Init the future by passing the request
     *
     * @param out
     */
    public SettableRedisMsgFuture(RedisMessage out) {

        //DEBUG: Request sent
        sw.start();

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

        if (in == null) throw new NoResultErr();

        return this.in;
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

        boolean success = completeSignal.await(timeout, unit);

        if (!success) throw new TimeoutErr();

        return this.in;
    }

    /**
     * Provide the response to the future
     */
    public void setIn(RedisMessage in) {

        //Response received
        this.in = in;
        completeSignal.countDown();


        //DEBUG: Response received
        LOG.finest( "cmd = " + this.out.toString() + ", time = " + sw.elapsed());
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
