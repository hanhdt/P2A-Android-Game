/**
 *
 */
package com.cse.p2a.aseangame.utils;

/**
 * @author DHanh
 */
public class P2AThreadPattern {
    public static final int TIME_INTERVAL = 10; // 10 seconds.
    private static P2AThreadPattern INSTANCE = null;
    private static Thread myThread;
    private static boolean stopRequest;

    private P2AThreadPattern(final Runnable run) {
        myThread = new Thread(run);
    }

    public static synchronized void requestStop() {
        stopRequest = true;
    }

    public static synchronized boolean stopRequest() {
        return stopRequest;
    }

    public static P2AThreadPattern getInstance(final Runnable run) {
        if (INSTANCE == null) {
            INSTANCE = new P2AThreadPattern(run);
        }
        return INSTANCE;
    }

    public static Thread getMyThread() {
        return myThread;
    }

    /**
     * Sleep thread for seconds.
     *
     * @param milisecond
     */
    @SuppressWarnings("static-access")
	public static void sleepForInSecs(Integer milisecond) {
        try {
            // do what you want to do before sleeping
            Thread.currentThread().sleep(milisecond);// sleep for ms
            // do what you want to do after sleeptig
        } catch (InterruptedException ie) {
            // If this thread was intrrupted by nother thread
            ie.printStackTrace();
        }
    }
}
