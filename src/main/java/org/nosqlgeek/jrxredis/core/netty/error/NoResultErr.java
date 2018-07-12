package org.nosqlgeek.jrxredis.core.netty.error;

import java.util.concurrent.ExecutionException;

public class NoResultErr extends ExecutionException {

    public static final String MSG = "Result is not yet available!";

    /**
     * Default Ctor
     */
    public NoResultErr() {

        super(MSG);
    }
}
