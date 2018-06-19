package org.nosqlgeek.jrxredis.core.message;

import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nosqlgeek.jrxredis.core.helper.ByteBufHelper;
import org.nosqlgeek.jrxredis.core.netty.RedisClientBootstrap;
import org.nosqlgeek.jrxredis.core.netty.RedisClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


import static org.junit.jupiter.api.Assertions.*;
import static org.nosqlgeek.jrxredis.core.message.TestConstants.*;


/**
 * Test if Getting a message works as expected
 */
class GetMsgTest {

    /**
     * Client bootstrap
     */
    private static RedisClientBootstrap bootstrap;

    /**
     * Client handler
     */
    private static RedisClientHandler handler;

    @BeforeAll
    static void setUp() {

        try {

            bootstrap = new RedisClientBootstrap(HOST, PORT);
            handler = bootstrap.getHandler();

        } catch (Exception e) {

            throw new RuntimeException("Init failed");
        }
    }

    @AfterAll
    static void tearDown() {

        bootstrap.disconnect();
    }

    @Test
    void getMsgTest() throws Exception {

        System.out.println("-- getMsgTest");


        if (bootstrap.isConnected()) {

            System.out.println("Setting a single value ...");
            SetMsg set = new SetMsg("hello", "world");
            RedisMessage setRes = handler.sendMessage(set).retrieveResponse(1000);
            assertEquals("OK", ((SimpleStringRedisMessage) setRes).content());

            System.out.println("Getting a singly value async ...");
            GetMsg get = new GetMsg("hello");
            Future<RedisMessage> getRes = handler.sendMessage(get).retrieveResponse();

            System.out.print("Waiting until the Future is 'done' ...");
            while (!getRes.isDone()) {

                Thread.sleep(100);
            }

            System.out.print("Checking if we retrieved some data ...");
            assertNotNull(getRes.get());
        }
    }

    @Test
    void get5KMessagesTest() throws Exception {

        System.out.println("-- get5KMessagesTest");

        System.out.println("Flushing all message buffers ...");
        handler.getIn().flush();
        handler.getOut().flush();

        if (bootstrap.isConnected()) {



            System.out.println("Setting 5000 values ...");
            for (int i = 0; i < 5000 ; i++) {

                SetMsg set = new SetMsg("hello:" + i, "world:" + i);
                handler.sendMessage(set).retrieveResponse(1000);
            }

            System.out.println("Getting 5000 values async ...");
            for (int i = 0; i < 5000 ; i++) {

                GetMsg get = new GetMsg("hello:" + i);
                handler.sendMessage(get);
            }

            System.out.println("Waiting 5 secs ...");
            Thread.sleep(5000);

            System.out.println("Checking that 5000 resonses arrived ... ");
            assertEquals(5000, handler.getOut().getSize());
            assertEquals(5000, handler.getIn().getSize());


            System.out.println("Consuming the 5000 responses from the buffer ...");
            for (int i = 0; i < 5000 ; i++) {

                handler.retrieveResponse().get();
            }

            System.out.println("Checking that all responses are consumed ...");
            assertEquals(0, handler.getOut().getSize());
            assertEquals(0, handler.getIn().getSize());


        }
    }

    @Test
    void checkMessageReplyOrderTest() throws Exception {

        System.out.println("-- checkMessageReplyOrderTest");


        System.out.println("Flushing all message buffers ...");
        handler.getIn().flush();
        handler.getOut().flush();


        System.out.println("Setting 5000 values ...");
        for (int i = 0; i < 5000 ; i++) {

            SetMsg set = new SetMsg("hello:" + i, "world:" + i);
            handler.sendMessage(set).retrieveResponse(1000);
        }

        System.out.println("Getting 1000 values async ...");
        List<Future<RedisMessage>> results = new ArrayList<Future<RedisMessage>>();

        for (int i = 0; i < 1000 ; i++) {

            GetMsg get = new GetMsg("hello:" + i);
            Future<RedisMessage> resultFuture = handler.sendMessage(get).retrieveResponse();
            results.add(resultFuture);
        }

        System.out.println("Waiting 5 secs ...");
        Thread.sleep(5000);

        System.out.println("Checking that the execution order is preserved ...");
        for (int i = 0; i < 1000 ; i++) {

            Future<RedisMessage> f = results.get(i);

            String responseStr = ByteBufHelper.fromByteBuf(((FullBulkStringRedisMessage) f.get()).content());
            assertEquals("world:" + i, responseStr);
        }

    }

}