/**
 *  Provides application context for passing everywhere 
 *  such as SQLiteOpenHelper where context is required.
 */
package com.cse.p2a.aseangame;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cse.p2a.aseangame.data.model.Country;
import com.cse.p2a.aseangame.data.model.Session;
import com.cse.p2a.aseangame.data.model.User;

import java.util.Date;
import java.util.Objects;

/**
 * @author Hanh D. TRAN
 */
public class P2AContext extends Application {
    public static final String TAG = P2AContext.class.getSimpleName();

    private static Context context;

    private Country selectedCountry; // delete selected country

    private Session currentSession; // delegate new session

    private User cUser; // delegate current user object

    // Audio Instances
    private SoundPool soundPool;
    private int wrongAudio;
    private int rightAudio;
    private int finishAudio;
    private boolean isSoundOn;

    // Volley variables
    private RequestQueue mRequestQueue;
    private static P2AContext mInstance;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        P2AContext.context = getApplicationContext();
        mInstance = this;

        selectedCountry = new Country();

        currentSession = new Session();

        cUser = new User();
        cUser.set_user_active(1);
        cUser.set_user_name("anonymous");
        cUser.set_user_created_date(new Date().toString());
        cUser.set_user_token("");
        // Instances sound fool
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        wrongAudio = soundPool.load(this, R.raw.wrong_answer, 1);
        rightAudio = soundPool.load(this, R.raw.right_answer, 1);
        setFinishAudio(soundPool.load(this, R.raw.clap, 1));
        isSoundOn = true;
    }

    public static synchronized P2AContext getInstance(){
        return mInstance;
    }

    /**
     * Get request service queue
     * @return
     */
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(P2AContext.context);
        }

        return mRequestQueue;
    }

    /**
     * Add a (generic type) request to the queue with a defined TAG
     * @param req
     * @param tag
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * Add a request (in generic type) to the queue with a default TAG
     * @param req
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancel all pending requests in the queue
     * @param tag
     */
    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public synchronized Country getSelectedCountry() {
        return selectedCountry;
    }

    public synchronized void setSelectedCountry(Country country) {
        selectedCountry = country;
    }

    public synchronized Session getCurrentSession() {
        return currentSession;
    }

    public synchronized void setCurrentSession(Session cSession) {
        currentSession = cSession;
    }

    /**
     * @return the cUser
     */
    public synchronized User getcUser() {
        return cUser;
    }

    /**
     * @param cUser the cUser to set
     */
    public synchronized void setcUser(User cUser) {
        this.cUser = cUser;
    }

    public synchronized SoundPool getSoundPool() {
        return soundPool;
    }

    public synchronized int getWrongAudio() {
        return wrongAudio;
    }

    public synchronized int getRightAudio() {
        return rightAudio;
    }

    public synchronized boolean isSoundOn() {
        return isSoundOn;
    }

    public synchronized void setSoundOn(boolean value) {
        isSoundOn = value;
    }

    public synchronized int getFinishAudio() {
        return finishAudio;
    }

    public synchronized void setFinishAudio(int finishAudio) {
        this.finishAudio = finishAudio;
    }
}
