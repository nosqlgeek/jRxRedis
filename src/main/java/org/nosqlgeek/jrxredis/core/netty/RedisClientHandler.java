package org.nosqlgeek.jrxredis.core.netty;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import org.nosqlgeek.jrxredis.core.buffer.AsyncRedisMsgBuffer;
import org.nosqlgeek.jrxredis.core.buffer.IMsgBuffer;
import org.nosqlgeek.jrxredis.core.netty.error.UnknownMsgTypeErr;

import java.util.*;
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
public class RedisClientHandler extends ChannelDuplexHandler {


    /**
     * The handler's logger
     */
    public static final Logger LOG = Logger.getLogger(RedisClientHandler.class.getName());

    /**
     * The channel context
     */
    private ChannelHandlerContext ctx;


    /**
     * An async messge buffer. Futures are placeholders until the response arrives.
     */
    private IMsgBuffer<Future<RedisMessage>> futures = new AsyncRedisMsgBuffer();




    /**
     * Retrieve the context of the channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
    }

    /**
     * Handle a write to the channel
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {


        LOG.finest("> " + msg);

        if (msg instanceof String) {

            ctx.write(toCmdMsg((String) msg,ctx), promise);

        } else if (msg instanceof ArrayRedisMessage) {


            if (validateCmdMsg((ArrayRedisMessage) msg)) {

                ctx.write(msg, promise);

            } else {

                throw new UnknownMsgTypeErr(msg);
            }


        } else {

            throw new UnknownMsgTypeErr(msg);
        }
    }


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
     * From redis.io: A client sends to the Redis server a RESP Array consisting of just Bulk Strings
     *
     * @param strMsg
     * @param ctx
     * @return
     */
    private static ArrayRedisMessage toCmdMsg(String strMsg, ChannelHandlerContext ctx) {

        String[] cmdStrings = ((String) strMsg).split("\\s+");

        List<RedisMessage> bulkStrings = new ArrayList<RedisMessage>(cmdStrings.length);

        for (String cmdStr : cmdStrings) {

            bulkStrings.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmdStr)));
        }

        return new ArrayRedisMessage(bulkStrings);
    }


    /**
     * Checks if the passed Array message does only contain Bulk Strings
     *
     * @param cmd
     * @return
     */
    private static boolean validateCmdMsg(ArrayRedisMessage cmd) {


        for (Iterator<RedisMessage> it = cmd.children().iterator(); it.hasNext(); ) {

            if ( !(it.next() instanceof FullBulkStringRedisMessage) ) {

                return false;
            }
        }

        return true;
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
