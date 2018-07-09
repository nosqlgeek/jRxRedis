package org.nosqlgeek.jrxredis.core.rx;

import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.reactivex.Observable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nosqlgeek.jrxredis.core.helper.ByteBufHelper;
import org.nosqlgeek.jrxredis.core.message.GetMsg;
import org.nosqlgeek.jrxredis.core.message.SetMsg;
import org.nosqlgeek.jrxredis.core.netty.RedisClientBootstrap;
import org.nosqlgeek.jrxredis.core.netty.RedisClientHandler;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.nosqlgeek.jrxredis.core.test.TestConstants.*;


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
    public void subscribeOnSetTest() throws Exception {


        LOG.info("Executing on: " + Thread.currentThread().getName());
        assertEquals("main",  Thread.currentThread().getName());

        for (int i = 0; i < 10000 ; i++) {

            LOG.info("i = " + i);
            SetMsg set = new SetMsg("subscribeOnSetTest:" + i, "value:" + i);
            RxRedisMsgObservableFactory.newObservable(handler.sendAsyncMessage(set), 1000)
                    .subscribe(m -> {

                        String tName = Thread.currentThread().getName();
                        LOG.info("Executing on: " + tName);
                        assertEquals(true, tName.startsWith("rx-redis"));

                        LOG.info("Retrieved message " + ((SimpleStringRedisMessage)m).content());
                        assertEquals("OK", ((SimpleStringRedisMessage)m).content());
                    });
        }


        LOG.info("Execution completed");
        Thread.sleep(5000);
    }


    @Test
    public void subscribeOnGetsTest() throws Exception {


        //Set some values
        for (int i = 0; i < 10 ; i++) {

            SetMsg set = new SetMsg("subscribeOnGetsTest" + i, "value:" + i);
            handler.sendAsyncMessage(set);
        }

        //We are just executing w/o handling the result here
        Thread.sleep(5000);


        //Get some values and merge them into one Observable
        Observable<RedisMessage> o = Observable.empty();

        for (int i = 0; i < 10 ; i++) {

            GetMsg get = new GetMsg("subscribeOnGetsTest" + i);
            o = o.mergeWith(RxRedisMsgObservableFactory.newObservable(handler.sendAsyncMessage(get), 1000));
        }


        o.map(m -> ByteBufHelper.fromByteBuf(((FullBulkStringRedisMessage)m).content()))
                .subscribe(s -> {
                    LOG.info("Retrieved message " + s);
                    assertEquals(true, s.startsWith("value:"));
        });

        Thread.sleep(5000);
    }


}
