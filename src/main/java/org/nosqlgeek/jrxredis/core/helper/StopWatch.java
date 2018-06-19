package org.nosqlgeek.jrxredis.core.helper;

/**
 * A simple stop watch
 */
public class StopWatch {

    private long start;
    private long end;
    private boolean running = false;

    /**
     * Start the watch
     */
    public void start() {

        this.running = true;
        this.start = System.currentTimeMillis();
    }

    /**
     * Stop the watch
     */
    public void stop() {

        this.running = false;
        this.end = System.currentTimeMillis();
    }


    /**
     * Check how much time elapsed
     * @return
     */
    public long elapsed() {

        if (running) {

            return System.currentTimeMillis() - start;

        } else {

            return end - start;
        }
    }

}
