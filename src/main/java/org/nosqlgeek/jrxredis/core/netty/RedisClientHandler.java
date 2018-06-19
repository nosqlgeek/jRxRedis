package org.nosqlgeek.jrxredis.core.netty;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import org.nosqlgeek.jrxredis.core.buffer.IRedisMsgBuffer;
import org.nosqlgeek.jrxredis.core.buffer.SimpleRedisMsgBuffer;
import org.nosqlgeek.jrxredis.core.helper.RedisMsgHelper;
import org.nosqlgeek.jrxredis.core.netty.error.UnknownMsgTypeErr;

import java.util.*;
import java.util.concurrent.TimeoutException;
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
     * Simple message buffers
     */
    private IRedisMsgBuffer in = new SimpleRedisMsgBuffer();
    private IRedisMsgBuffer out = new SimpleRedisMsgBuffer();




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
        LOG.finest("Retrieved msg:" + retrieved);
        RedisMsgHelper.printMsg(retrieved, LOG);
        this.in.add(retrieved);

    }


    /**
     * Retrieves the next response from the channel or null if the response is not yet available
     *
     * @return
     */
    public RedisMsgFuture retrieveResponse() {

        return new RedisMsgFuture(in, out);
    }

    /**
     * Retrieves the next response from the channel by waiting until it becomes available
     *
     * @return
     */
    public RedisMessage retrieveResponse(long timeout) throws TimeoutException {

        RedisMessage received = this.in.retrieveBlocking(timeout);
        RedisMessage sent = this.out.retrieveNow();

        return received;
    }


    /**
     * Writes a message to the channel and returns the handler itself
     *
     * @param msg
     */
    public RedisClientHandler sendMessage(ArrayRedisMessage msg) {

        this.out.add(msg);
        this.ctx.writeAndFlush(msg);
        return this;
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
     * Only for debugging and testing purposes
     *
     * @return
     */
    public IRedisMsgBuffer getOut() {
        return out;
    }

    /**
     * Only for debugging and testing purposes
     *
     * @return
     */
    public IRedisMsgBuffer getIn() {
        return in;
    }
}
