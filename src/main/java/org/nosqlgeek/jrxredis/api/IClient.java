package org.nosqlgeek.jrxredis.api;

import io.reactivex.Observable;
import org.nosqlgeek.jrxredis.api.error.ConnectionErr;

public interface IClient {

    /**
     * Connect to a database
     *
     * @param host
     * @param port
     * @param password
     * @return
     */
    public Observable<Boolean> connect(String host, int port, String password) throws ConnectionErr;

    /**
     * Connect w/o a password
     * @param host
     * @param port
     * @return
     */
    public Observable<Boolean> connect(String host, int port) throws ConnectionErr;


    /**
     * Disconnect from the client
     *
     * @return
     */
    public Observable<Boolean> disconnect();

    /**
     * Get a value by key
     *
     * @param key
     * @return
     */
    public Observable<byte[]> get(byte[] key);

    /**
     * Get a value by key, whereby the key is expressed as a Java String
     * @param key
     * @return
     */
    public Observable<byte[]> get(String key);

    /**
     * Set a value
     * @param key
     * @param value
     * @return
     */
    public Observable<Boolean> set(byte[] key, byte[] value);

    /**
     * Set a value, whereby the key is expressed as a Java String
     *
     * @param key
     * @param value
     * @return
     */
    public Observable<Boolean> set(String key, byte[] value);

}
