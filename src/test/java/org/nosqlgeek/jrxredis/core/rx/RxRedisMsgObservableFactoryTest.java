package org.nosqlgeek.jrxredis.core.rx;

import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nosqlgeek.jrxredis.core.message.SetMsg;
import org.nosqlgeek.jrxredis.core.netty.RedisClientBootstrap;
import org.nosqlgeek.jrxredis.core.netty.RedisClientHandler;

import java.util.logging.Logger;

import static org.nosqlgeek.jrxredis.core.message.TestConstants.*;


public class RxRedisMsgObservableFactoryTest {


    public static Logger LOG = Logger.getLogger(RxRedisMsgObservableFactoryTest.class.getName());

    /**
     * Client bootstrap
     */
    private static RedisClientBootstrap bootstrap;

    /**
     * Client handler
     */
    private static RedisClientHandler handler = new RedisClientHandler();


    @BeforeAll
    static void setUp() {

        try {

            bootstrap = new RedisClientBootstrap(HOST, PORT, handler);

        } catch (Exception e) {

            throw new RuntimeException("Init failed");
        }
    }

    @AfterAll
    static void tearDown() {

        bootstrap.disconnect();
    }

    @Test
    public void getTest() throws Exception {




        LOG.info("Executing on: " + Thread.currentThread().getName());

        LOG.info("Execute async.");

        for (int i = 0; i < 10000 ; i++) {

            System.out.println("i = " + i);
            SetMsg set = new SetMsg("hello", "world");
            RxRedisMsgObservableFactory.newObservable(handler.sendAsyncMessage(set), 1000)
                    .subscribe(m -> {
                        LOG.info("Executing on: " + Thread.currentThread().getName());
                        LOG.info("Retrieved message " + ((SimpleStringRedisMessage)m).content());
                    });
        }


        LOG.info("Execution completed");
    }


}
