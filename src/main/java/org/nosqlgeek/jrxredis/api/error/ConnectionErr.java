package org.nosqlgeek.jrxredis.api.error;


public class ConnectionErr extends Exception {

    public final static String MSG = "Error to connect to the database";

    /**
     * Host trying to connect to
     */
    private String host;

    /**
     * Port trying to connect to
     */
    private int port;

    /**
     * State of the error
     */
    private boolean isTimeout;
    private boolean isAuth;


    /**
     * Aggregates several causes into a connection error
     *
     * @param host
     * @param port
     * @param timeout
     * @param auth
     */
    public ConnectionErr(String host, int port, boolean timeout, boolean auth) {

        super(toStringMsg(host, port, timeout, auth));

        this.host = host;
        this.port = port;
        this.isAuth = auth;
        this.isTimeout = timeout;

    }

    /**
     * The error as String
     *
     * @return
     */
    @Override
    public String toString() {

        return toStringMsg(host, port, isTimeout, isAuth);
    }

    /**
     * Helper to convert the causes into a String message
     *
     * @param host
     * @param port
     * @param timeout
     * @param auth
     * @return
     */
    private static String toStringMsg(String host, int port, boolean timeout, boolean auth) {

        StringBuilder sb = new StringBuilder(MSG);
        sb.append(":");
        sb.append(host);
        sb.append(", ");
        sb.append(port);

        if (timeout) sb.append(", connection timed out");
        if (auth) sb.append(", authentication failed");

        return sb.toString();

    }

}
