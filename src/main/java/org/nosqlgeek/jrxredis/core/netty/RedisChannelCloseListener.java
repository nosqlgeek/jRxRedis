package org.nosqlgeek.jrxredis.core.netty;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.logging.Logger;

/**
 * Executed when the connection is closed
 *
 */
public class RedisChannelCloseListener implements GenericFutureListener<Future<? super Void>> {


    public static final Logger LOG = Logger.getLogger(RedisChannelCloseListener.class.getName());


    public void operationComplete(Future<? super Void> future) throws Exception {

        LOG.finest("Connection closed!");
    }
}
