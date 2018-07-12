package org.nosqlgeek.jrxredis.api;

import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.reactivex.Observable;
import org.nosqlgeek.jrxredis.api.error.ConnectionErr;
import org.nosqlgeek.jrxredis.core.message.AuthMsg;
import org.nosqlgeek.jrxredis.core.netty.RedisClientBootstrap;
import org.nosqlgeek.jrxredis.core.netty.RedisClientHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Client implements IClient {


    /**
     * Connection timeout
     */
    public static final int TIMEOUT=5000;

    /**
     * The connection bootstrap
     */
    private RedisClientBootstrap bootstrap;

    /**
     * The main connection handler
     */
    private RedisClientHandler handler;


    @Override
    public Observable<Boolean> connect(String host, int port, String password) throws ConnectionErr {

        Observable<Boolean> result = Observable.just(false);

        if (bootstrap == null) {

            handler = new RedisClientHandler();

            try {

                bootstrap = new RedisClientBootstrap(host, port, handler);

                if (!password.equals("")) {

                    SimpleStringRedisMessage authResult = (SimpleStringRedisMessage) handler
                            .sendAsyncMessage(new AuthMsg(password)).get(TIMEOUT, TimeUnit.MILLISECONDS);

                    if (!authResult.content().equals(AuthMsg.SUCCESS))
                        throw new ConnectionErr(host, port, false, true);
                }

                result = Observable.just(true);

            } catch (Exception e) {

                boolean timeoutErr=false;
                if (e instanceof TimeoutException) timeoutErr = true;

                throw new ConnectionErr(host, port, timeoutErr, false);
            }
        }

        return result;

    }

    @Override
    public Observable<Boolean> connect(String host, int port) throws ConnectionErr {

        return connect(host, port, "");
    }

    @Override
    public Observable<Boolean> disconnect() {
        return null;
    }

    @Override
    public Observable<byte[]> get(byte[] key) {
        return null;
    }

    @Override
    public Observable<byte[]> get(String key) {
        return null;
    }

    @Override
    public Observable<Boolean> set(byte[] key, byte[] value) {
        return null;
    }

    @Override
    public Observable<Boolean> set(String key, byte[] value) {
        return null;
    }
}
