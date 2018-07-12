package org.nosqlgeek.jrxredis.core.netty.error;

/**
 * Error which occurs if the bootstrap of the Redis client is failing
 *
 */
public class BootstrapErr extends Exception {

    public final static String MSG = "Could not bootstrap the client.";

    public BootstrapErr(Exception e) {

        super(e);
    }

    @Override
    public String toString() {

        return MSG;
    }
}
