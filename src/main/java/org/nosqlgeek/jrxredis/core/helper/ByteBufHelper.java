package org.nosqlgeek.jrxredis.core.helper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

/**
 * Helper methods for byte buffers
 */
public class ByteBufHelper {

    //The used Bytebuffer allocator
    private  final static ByteBufAllocator byteBufAlloc = ByteBufAllocator.DEFAULT;

    /**
     * Converts a UTF8 String into a ByteBuffer
     *
     * @param str
     * @return
     */
    public static ByteBuf toByteBuf(String str) {

        return ByteBufUtil.writeUtf8(byteBufAlloc, str);
    }


    /**
     * Converts a ByteBuffer into a UTF8 String
     *
     * @param byteBuf
     * @return
     */
    public static String fromByteBuf(ByteBuf byteBuf) {

        return new String(ByteBufUtil.getBytes(byteBuf));
    }

}
