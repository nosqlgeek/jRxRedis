package org.nosqlgeek.jrxredis.core.netty;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.netty.error.UnknownMsgTypeErr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class BaseRedisClientHandler extends ChannelDuplexHandler {

    /**
     * The handler's logger
     */
    public static final Logger LOG = Logger.getLogger(BaseRedisClientHandler.class.getName());

    /**
     * The channel context
     */
    protected ChannelHandlerContext ctx;


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
     * From redis.io: A client sends to the Redis server a RESP Array consisting of just Bulk Strings
     *
     * @param strMsg
     * @param ctx
     * @return
     */
    protected static ArrayRedisMessage toCmdMsg(String strMsg, ChannelHandlerContext ctx) {

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
    protected static boolean validateCmdMsg(ArrayRedisMessage cmd) {


        for (Iterator<RedisMessage> it = cmd.children().iterator(); it.hasNext(); ) {

            if ( !(it.next() instanceof FullBulkStringRedisMessage) ) {

                return false;
            }
        }

        return true;
    }
}
