package org.nosqlgeek.jrxredis.core.message;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.helper.ByteBufHelper;

import java.util.ArrayList;

import static org.nosqlgeek.jrxredis.core.helper.ByteBufHelper.toByteBuf;

public class SetMsg extends ArrayRedisMessage {

    public static String CMD = "SET";

    /**
     * Cto which accepts strings
     * @param key
     * @param value
     */
    public SetMsg(String key, String value) {

        this(ByteBufHelper.toByteBuf(key), ByteBufHelper.toByteBuf(value));

    }

    /**
     * The Cto which constructs a SET message
     *
     * @param key
     * @param value
     */
    public SetMsg(ByteBuf key, ByteBuf value) {

        super(new ArrayList<RedisMessage>());

        this.children().add(new FullBulkStringRedisMessage(toByteBuf(CMD)));
        this.children().add(new FullBulkStringRedisMessage(key));
        this.children().add(new FullBulkStringRedisMessage(value));
    }
}
