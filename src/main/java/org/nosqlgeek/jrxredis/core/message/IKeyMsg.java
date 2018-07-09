package org.nosqlgeek.jrxredis.core.message;

import io.netty.buffer.ByteBuf;

/**
 * Describes a message which is just working on the key
 */
public interface IKeyMsg {


    /**
     * Get the key of this message
     *
     * @return
     */
    public ByteBuf getKey();

}
