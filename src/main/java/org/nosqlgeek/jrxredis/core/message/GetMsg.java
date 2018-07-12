package org.nosqlgeek.jrxredis.core.message;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.helper.ByteBufHelper;

import static org.nosqlgeek.jrxredis.core.helper.ByteBufHelper.*;

import java.util.ArrayList;

/**
 * The message which is sent to Redis for a GET command
 */
public class GetMsg extends ArrayRedisMessage implements IKeyMsg {

    /**
     * Command name
     */
    public static String CMD = "GET";


    /**
     * The key to get
     */
    private ByteBuf key;


    /**
     * This is the most generic constructor because it is accepting a Byte Buffer as an argument. A key can be in
     * theory any Redis String (which is more than a Java String)
     *
     * @param key
     */
    public GetMsg(ByteBuf key) {

        super(new ArrayList<RedisMessage>());

        this.key = key;
        this.children().add(new FullBulkStringRedisMessage(toByteBuf(CMD)));
        this.children().add(new FullBulkStringRedisMessage(key));
    }

    /**
     * Short cut for using a Java String as the key
     *
     * @param key
     */
    public GetMsg(String key) {

        this(ByteBufHelper.toByteBuf(key));
    }

    /**
     * For debugging purposes
     *
     * @return
     */
    @Override
    public String toString() {


        StringBuilder sb = new StringBuilder(CMD);
        sb.append(" ");
        sb.append(ByteBufHelper.fromByteBuf(this.key));

        return sb.toString();
    }


    /**
     * Get the key
     * @return
     */
    public ByteBuf getKey() {
        return key;
    }
}
