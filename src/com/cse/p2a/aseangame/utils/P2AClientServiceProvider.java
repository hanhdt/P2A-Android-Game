/**
 *
 */
package com.cse.p2a.aseangame.utils;

import android.content.Context;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * @author DHanh
 */
public class P2AClientServiceProvider {

    public static final String HOST = "http://p2a.asia/";
    public static final String PORT = "8050/";
    public static final String URL = "DTU/";
    private static P2AClientServiceProvider instance = null;
    private static int responsedCode;
    protected DefaultHttpClient defaultClient;
    protected String originalServicePath = HOST + URL;
    private Context context;

    public P2AClientServiceProvider(Context context) {
        this.context = context;
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        defaultClient = new DefaultHttpClient();
        defaultClient.setParams(httpParameters);
    }

    public static final P2AClientServiceProvider getInstance(Context context) {
        if (instance == null) {
            instance = new P2AClientServiceProvider(context);
        }
        return instance;
    }

    public static synchronized int getResponsedCode() {
        return responsedCode;
    }

    public static synchronized void setResponsedCode(int responsedCode) {
        P2AClientServiceProvider.responsedCode = responsedCode;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public DefaultHttpClient getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(DefaultHttpClient defaultClient) {
        this.defaultClient = defaultClient;
    }

    public String getOriginalServicePath() {
        return originalServicePath;
    }

    public void setOriginalServicePath(String originalServicePath) {
        this.originalServicePath = originalServicePath;
    }
}
