package org.nosqlgeek.jrxredis.core.message;

import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import org.nosqlgeek.jrxredis.core.helper.ByteBufHelper;
import java.util.ArrayList;

import static org.nosqlgeek.jrxredis.core.helper.ByteBufHelper.toByteBuf;

public class AuthMsg extends ArrayRedisMessage {


    /**
     * Command name
     */
    public static String CMD = "AUTH";

    /**
     * Successful response
     */
    public static String SUCCESS = "OK";

    /**
     * The password
     */
    private String passwd;

    /**
     * The constructor
     *
     * @param passwd
     */
    public AuthMsg(String passwd) {

        super(new ArrayList<RedisMessage>());

        this.passwd = passwd;
        this.children().add(new FullBulkStringRedisMessage(toByteBuf(CMD)));
        this.children().add(new FullBulkStringRedisMessage(ByteBufHelper.toByteBuf(passwd)));

    }
}
