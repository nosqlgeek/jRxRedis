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

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;
import static org.nosqlgeek.jrxredis.core.test.TestConstants.*;


/**
 * Test if Getting a message works as expected
 */
class SetAndGetMsgTest {

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
    void getMsgTest() throws Exception {

        System.out.println("-- getMsgTest");
        System.out.println("Flushing all message buffers ...");
        handler.getFutures().flush();

        if (bootstrap.isConnected()) {

            System.out.println("Setting a single value ...");
            SetMsg set = new SetMsg("hello", "world");
            RedisMessage setRes = handler.sendAsyncMessage(set).get(1000, TimeUnit.MILLISECONDS);

            assertEquals("OK", ((SimpleStringRedisMessage) setRes).content());

            System.out.println("Getting a singly value async ...");
            GetMsg get = new GetMsg("hello");
            Future<RedisMessage> getRes = handler.sendAsyncMessage(get);

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
        handler.getFutures().flush();

        if (bootstrap.isConnected()) {

            System.out.println("Setting 5000 values ...");
            for (int i = 0; i < 5000 ; i++) {

                SetMsg set = new SetMsg("hello:" + i, "world:" + i);
                handler.sendAsyncMessage(set).get(1000, TimeUnit.MILLISECONDS);
            }

            List<Future<RedisMessage>> results = new ArrayList<Future<RedisMessage>>();

            System.out.println("Getting 5000 values async ...");
            for (int i = 0; i < 5000 ; i++) {

                GetMsg get = new GetMsg("hello:" + i);
                results.add(handler.sendAsyncMessage(get));
            }

            System.out.println("Waiting 5 secs ...");
            Thread.sleep(5000);

            System.out.println("Checking if all 5000 responses arrived ...");

            for ( Future<RedisMessage> f : results ) {

                assertEquals(true, f.isDone());

            }
        }
    }

    @Test
    void checkMessageReplyOrderTest() throws Exception {

        System.out.println("-- checkMessageReplyOrderTest");


        System.out.println("Flushing all message buffers ...");
        handler.getFutures().flush();


        System.out.println("Setting 5000 values ...");
        for (int i = 0; i < 5000 ; i++) {

            SetMsg set = new SetMsg("hello:" + i, "world:" + i);
            handler.sendAsyncMessage(set).get(1000, TimeUnit.MILLISECONDS);
        }

        System.out.println("Getting 1000 values async ...");
        List<Future<RedisMessage>> results = new ArrayList<Future<RedisMessage>>();

        for (int i = 0; i < 1000 ; i++) {

            GetMsg get = new GetMsg("hello:" + i);
            Future<RedisMessage> resultFuture = handler.sendAsyncMessage(get);
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

    @Test
    void checkRandomMessageReplyOrderTest() throws Exception {

        System.out.println("-- checkRandomMessageReplyOrderTest");


        System.out.println("Setting 1000 values ...");
        for (int i = 0; i < 1000 ; i++) {

            SetMsg set = new SetMsg("hello:" + i, "world:" + i);
            //handler.sendMessage(set).retrieveResponse(1000);
            handler.sendAsyncMessage(set).get(1000,TimeUnit.MILLISECONDS);
        }


        System.out.println("Generating 50 random keys ...");
        Random random = new Random();

        Map<String, Future<RedisMessage>> items = new HashMap<String, Future<RedisMessage>>();

        for (int i = 0; i < 50 ; i++) {

            String key = "hello:" + random.nextInt(1000);
            items.put(key, null);
        }


        System.out.println("Getting async randomly ...");
        for ( String key : items.keySet()) {

            items.put(key, handler.sendAsyncMessage(new GetMsg(key)));

        }

        System.out.println("Waiting 5 secs ...");
        Thread.sleep(10000);

        System.out.println("Checking if we retrieved the right values ...");


        int idx1 = random.nextInt(20);
        int idx2 = random.nextInt(20);

        String key = items.keySet().toArray()[idx1].toString();
        String reqId  = key.split(":")[1];
        String respId = ByteBufHelper.fromByteBuf(((FullBulkStringRedisMessage) items.get(key).get()).content()).split(":")[1];
        assertEquals(reqId, respId);

        key = items.keySet().toArray()[idx2].toString();
        reqId  = key.split(":")[1];
        respId = ByteBufHelper.fromByteBuf(((FullBulkStringRedisMessage) items.get(key).get()).content()).split(":")[1];
        assertEquals(reqId, respId);

    }

}