package org.nosqlgeek.jrxredis.core.netty.error;

public class RedisPromiseErr  extends  Exception {

    public RedisPromiseErr() {

        super("Could not fullfill the promise!");
    }

}
