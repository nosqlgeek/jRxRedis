package org.nosqlgeek.jrxredis.core.netty.error;

import java.util.concurrent.TimeoutException;

public class TimeoutErr extends TimeoutException {

    private static final String MSG = "Operation timed out!";

    /**
     * Default Ctor
     */
    public TimeoutErr() {

        super(MSG);

    }
}
