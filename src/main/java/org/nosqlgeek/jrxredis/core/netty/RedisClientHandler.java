package org.nosqlgeek.jrxredis.core.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.*;
import org.nosqlgeek.jrxredis.core.buffer.AsyncRedisMsgBuffer;
import org.nosqlgeek.jrxredis.core.buffer.IMsgBuffer;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Handle Redis Messages
 *
 * BTW: Netty is an asynchronous event-driven network application framework for rapid development of maintainable high
 * performance protocol servers & clients.
 *
 * Messages can be passed as a String the format like 'SET key value'. In this case we will convert this String into
 * a Array consisting of just Bulk Strings.
 *
 *
 */
public class RedisClientHandler extends BaseRedisClientHandler {


    /**
     * The handler's logger
     */
    public static final Logger LOG = Logger.getLogger(RedisClientHandler.class.getName());


    /**
     * An async messge buffer. Futures are placeholders until the response arrives.
     */
    private IMsgBuffer<Future<RedisMessage>> futures = new AsyncRedisMsgBuffer();


    /**
     * Handle a read from the channel
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RedisMessage retrieved = (RedisMessage) msg;
        SettableRedisMsgFuture f = (SettableRedisMsgFuture) this.futures.retrieveNow();
        f.setIn(retrieved);
    }


    /**
     * Sends a message by creating a Future for it. The future is put into the message buffer and acts as placeholder
     * until the response arrives.
     *
     * @param msg
     * @return
     */
    public Future<RedisMessage> sendAsyncMessage(ArrayRedisMessage msg) {

        Future<RedisMessage> f = new SettableRedisMsgFuture(msg);

        this.futures.add(f);
        this.ctx.writeAndFlush(msg);
        return f;
    }



    /**
     * Only for debugging purposes
     *
     * @return
     */
    public IMsgBuffer<Future<RedisMessage>> getFutures() {
        return futures;
    }
}
