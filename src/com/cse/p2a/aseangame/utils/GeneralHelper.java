package com.cse.p2a.aseangame.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.cse.p2a.aseangame.P2AContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Tuan TRAN
 * @author Hanh D. TRAN
 */
public class GeneralHelper {

    public static String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static String HexForByte(byte b) {
        String Hex = Integer.toHexString((int) b & 0xff);
        boolean hasTwoDigits = (2 == Hex.length());
        if (hasTwoDigits) return Hex;
        else return "0" + Hex;
    }

    static String HexForBytes(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) sb.append(HexForByte(b));
        return sb.toString();
    }

    public static String HexMD5ForString(String text) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(text.getBytes());
        return HexForBytes(digest);
    }


    public static String getCurrentDate() {
        String date = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        date = dfm.format(cal.getTime());
        return date;
    }

    public static String getCurrentTime() {
        String time = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("HH:mm:ss", Locale.US);
        time = dfm.format(cal.getTime());
        return time;
    }

    public static String timestamp() {
        String timestamp = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.US);
        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
        timestamp = dfm.format(cal.getTime());
        return timestamp;
    }

    /**
     * Checks if the device has Internet connection.
     *
     * @return <code>true</code> if the phone is connected to the Internet.
     */
    public static boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) P2AContext.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * @param is
     * @param os
     */
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
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
            Thread.currentThread().sleep(milisecond);// sleep for 1000 ms
            // do what you want to do after sleeptig
        } catch (InterruptedException ie) {
            // If this thread was intrrupted by nother thread
            ie.printStackTrace();
        }
    }

    public GeneralHelper() {
    }
}
