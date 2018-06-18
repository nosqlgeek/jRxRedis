package org.nosqlgeek.jrxredis.core.helper;

import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

public class RedisMsgHelper {

    /**
     * Print the Redis response for for debugging purposes
     *
     * @param msg
     */
    public static void printMsg(RedisMessage msg, Logger log) {
        if (msg instanceof SimpleStringRedisMessage) {
            log.finest(((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            log.finest(((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            log.finest(new Long(((IntegerRedisMessage) msg).value()).toString());
        } else if (msg instanceof FullBulkStringRedisMessage) {

            FullBulkStringRedisMessage bulkStrMsg = (FullBulkStringRedisMessage)msg;

            if (bulkStrMsg.isNull())
                log.finest("(null)");
            else
                log.finest(bulkStrMsg.content().toString(CharsetUtil.UTF_8));

        } else if (msg instanceof ArrayRedisMessage) {
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                printMsg(child, log);
            }
        } else {

            // TODO - Needs to be handled in a validation method: throw new CodecException("Unknown message type: " + msg);
            log.finest("Response was of an unknown message type");

        }
    }
}
