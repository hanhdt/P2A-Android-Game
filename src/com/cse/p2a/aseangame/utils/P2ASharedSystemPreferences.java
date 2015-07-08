package com.cse.p2a.aseangame.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.cse.p2a.aseangame.R;

/**
 * Created with IntelliJ IDEA.
 * User: rafe
 * Date: 11/30/13
 * Time: 2:54 PM
 * <p/>
 * This is class that provides manipulation of system preferences.
 */
public class P2ASharedSystemPreferences {
    public static final String PREFS_NAME = "p2a_prefs";

    public static boolean getFirstTimeInstallationFlag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(context.getString(R.string.pref_key_flag_installation), true);
    }

    public static void setFirstTimeInstallationFlag(Context context, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.pref_key_flag_installation),
                value);
        prefsEditor.commit();
    }

    public static boolean getAuthenticationFlag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.pref_key_flag_authenticated), false);
    }

    public static void setAuthenticatedFlag(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(context.getString(R.string.pref_key_flag_authenticated),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getPassedAllCountriesFlag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.pref_key_flag_passed_all_countries), false);
    }

    public static void setPassedAllCountriesFlag(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(context.getString(R.string.pref_key_flag_passed_all_countries),
                newValue);
        prefsEditor.commit();
    }

    public static String getCurrentUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(context.getString(R.string.pref_key_flag_username), "");
    }

    public static void setCurrentUserName(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.pref_key_flag_username),
                newValue);
        prefsEditor.commit();
    }

    public static String getCurrentPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(context.getString(R.string.pref_key_flag_password), "");
    }

    public static void setCurrentPassword(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.pref_key_flag_password),
                newValue);
        prefsEditor.commit();
    }

    public static void setUserCreatedDate(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.pref_key_flag_user_created_timestamp),
                newValue);
        prefsEditor.commit();
    }

    public static String getUserCreatedTimestamp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(context.getString(R.string.pref_key_flag_user_created_timestamp), "");
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getString(context.getString(R.string.pref_key_token), "");
    }

    public static void setToken(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.pref_key_token),
                newValue);
        prefsEditor.commit();
    }

    public static void setUserIdPassedAllCountried(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.pref_key_flag_passed_all_countries),
                newValue);
        prefsEditor.commit();
    }

    public static String getUserIdPassedAllCountries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getString(context.getString(R.string.pref_key_flag_passed_all_countries), "1");
    }

}
