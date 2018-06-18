package org.nosqlgeek.jrxredis.core.netty.error;

import io.netty.handler.codec.CodecException;

/**
 * Indicates that a message of a wrong type or format was passed
 *
 */
public class UnknownMsgTypeErr extends CodecException {

    private final static String MSG = "Unknown message type: ";


    public UnknownMsgTypeErr(Object msg) {

        super( MSG + msg );
        this.printStackTrace();
    }

}
