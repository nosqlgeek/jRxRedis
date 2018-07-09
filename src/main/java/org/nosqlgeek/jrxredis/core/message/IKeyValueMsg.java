package org.nosqlgeek.jrxredis.core.message;

import io.netty.buffer.ByteBuf;

public interface IKeyValueMsg extends IKeyMsg {

    /**
     * Get the value of the message
     *
     * @return
     */
    public ByteBuf getValue();

}
