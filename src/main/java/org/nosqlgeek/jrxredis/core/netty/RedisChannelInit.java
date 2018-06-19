package org.nosqlgeek.jrxredis.core.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;

/**
 * Initialize the Redis channel by adding Inbound and Outbound handlers to the channel's pipeline
 *
 * - Inbound: RedisDecoder, RedisBulkStringAggregator, RedisArrayAggregatpor
 * - Outbound: RedisEncoder
 * - Custom duplex: RedisClientHander
 *
 */
public class RedisChannelInit extends ChannelInitializer<SocketChannel> {

    private final ChannelDuplexHandler handler;

    public RedisChannelInit(ChannelDuplexHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new RedisDecoder());
        p.addLast(new RedisBulkStringAggregator());
        p.addLast(new RedisArrayAggregator());
        p.addLast(new RedisEncoder());
        p.addLast(handler);
    }
}
