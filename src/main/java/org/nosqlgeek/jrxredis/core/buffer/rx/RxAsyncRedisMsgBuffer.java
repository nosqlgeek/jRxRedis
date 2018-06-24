package org.nosqlgeek.jrxredis.core.buffer.rx;

import io.netty.handler.codec.redis.RedisMessage;
import io.reactivex.Observable;
import org.nosqlgeek.jrxredis.core.buffer.IMsgBuffer;

import java.util.concurrent.TimeoutException;

public class RxAsyncRedisMsgBuffer implements IMsgBuffer<Observable<RedisMessage>> {

    @Override
    public void add(Observable<RedisMessage> msg) {

    }

    @Override
    public Observable<RedisMessage> retrieveBlocking(long timout) throws TimeoutException {
        return null;
    }

    @Override
    public Observable<RedisMessage> retrieveNow() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void flush() {

    }
}
