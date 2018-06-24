package org.nosqlgeek.jrxredis.core.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.nosqlgeek.jrxredis.core.netty.error.BootstrapErr;

import java.util.logging.Logger;


/**
 * Bootstraps the Redis client
 *
 * Allows to check via isConnected if a connection to the server could be established.
 * If this is the case then you getChannel can be used to access the communication channel
 *
 */
public class RedisClientBootstrap {

    public static final Logger LOG = Logger.getLogger(RedisClientBootstrap.class.getName());

    private final String host;
    private final int port;
    private Channel channel = null;
    private boolean connected = false;

    private final BaseRedisClientHandler handler;


    /**
     * The default Ctor
     */
    public RedisClientBootstrap(String host, int port, BaseRedisClientHandler handler) throws BootstrapErr {

        this.host = host;
        this.port = port;
        this.handler = handler;

        boostrap();
    }


    /**
     * Bootstrap
     */
    public void boostrap() throws BootstrapErr {

        //Bootstrap and attach handlers
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup());
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new RedisChannelInit(handler));

        // Start the client
        try {

            this.channel = b.connect(host, port).sync().channel();
            channel.closeFuture().addListener(new RedisChannelCloseListener());
            this.connected = true;

        } catch (Exception e) {

            throw new BootstrapErr(e);
        }

    }

    /**
     * Disconnect
     *
     */
    public void disconnect() {

        if (this.connected) {

            this.channel.close();
            this.connected = false;
        }
    }


    /**
     * Get the host
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the port
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the Netty channel
     * @return
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Find out if the connection was established
     * @return
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Access the client handler
     *
     * @return
     */
    public BaseRedisClientHandler getHandler() {
        return handler;
    }
}
