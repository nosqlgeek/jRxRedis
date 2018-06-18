package org.nosqlgeek.jrxredis.core.message;

import io.netty.handler.codec.redis.RedisMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nosqlgeek.jrxredis.core.netty.RedisClientBootstrap;
import org.nosqlgeek.jrxredis.core.netty.RedisClientHandler;

import java.util.concurrent.Future;

import static org.nosqlgeek.jrxredis.core.message.TestConstants.*;

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

            /**
             * Execute a sync SET
             */
            SetMsg set = new SetMsg("hello", "world");
            RedisMessage setRes = handler.sendMessage(set).retrieveResponse(1000);
            System.out.println("SET response:" + setRes);


            /**
             * Execute an async GET
             */
            GetMsg get = new GetMsg("hello");
            Future<RedisMessage> getRes = handler.sendMessage(get).retrieveResponse();

            int i = 0;

            //Synchronize for testing purposes
            while (!getRes.isDone()) {

                System.out.println("i = " + i);
                Thread.sleep(100);

                i++;
            }

            System.out.println("GET response:" + getRes.get());
        }
    }

    @Test
    void get5KMessages() throws Exception {

        System.out.println("-- getMsgTest");


        if (bootstrap.isConnected()) {


            for (int i = 0; i < 5000 ; i++) {

                SetMsg set = new SetMsg("hello:" + i, "world:" + i);
                handler.sendMessage(set).retrieveResponse(1000);
            }

            //Execute some Gets and buffer them
            for (int i = 0; i < 5000 ; i++) {

                GetMsg get = new GetMsg("hello:" + i);
                handler.sendMessage(get);
            }

            Thread.sleep(5000);

            System.out.println("Number of outgoing messages: " + handler.getOut().getSize());
            System.out.println("Number of incoming messages: " + handler.getIn().getSize());

            //Now consume 2000 of them
            for (int i = 0; i < 2000 ; i++) {

                handler.retrieveResponse().get();
            }

            System.out.println("Number of outgoing messages: " + handler.getOut().getSize());
            System.out.println("Number of incoming messages: " + handler.getIn().getSize());


        }
    }


}