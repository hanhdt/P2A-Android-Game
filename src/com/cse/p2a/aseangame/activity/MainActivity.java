package com.cse.p2a.aseangame.activity;

import android.animation.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.R;
import com.cse.p2a.aseangame.data.dao.UserDAO;
import com.cse.p2a.aseangame.data.model.User;
import com.cse.p2a.aseangame.utils.*;
import libs.BCrypt;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
    static final BounceInterpolator BOUNCE_INTERPOLATOR = new BounceInterpolator();
    //    static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    static long BASE_DURATION = 300;
//    static float sDurationScale = 1f;

    private ViewGroup mHomeView;
    private ViewGroup mSelectGameModeView;
    private ImageView btnLogin;
    private ImageView btnPlayNow;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;
    // User's information response if authentication is valid
    private User mUser = null;
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private int screenWidth, screenHeight;
    private ImageView imgSoundControl;
    private ImageView imgPointer;

    private View.OnTouchListener funButtonListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(.9f).scaleY(.9f).setInterpolator(sDecelerator);
                    v.setPressed(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    boolean isInside = (x > 0 && x < v.getWidth() &&
                            y > 0 && y < v.getHeight());
                    if (v.isPressed() != isInside) {
                        v.setPressed(isInside);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (v.isPressed()) {
                        v.performClick();
                        v.animate().scaleX(1).scaleY(1).setInterpolator(sAccelerator);
                        v.setPressed(false);
                    } else {
                        v.animate().scaleX(1).scaleY(1).setInterpolator(sAccelerator);
                    }
                    break;
            }
            return true;
        }
    };

    private View.OnTouchListener pointerMotionListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // get masked (not specific to a pointer) action
            int maskedAction = event.getActionMasked();
            float eventX = event.getX();
            float eventY = event.getY();
            imgPointer.setVisibility(View.VISIBLE);
            switch (maskedAction) {
                case MotionEvent.ACTION_DOWN: {
                    imgPointer.setX(eventX);
                    imgPointer.setY(eventY);
                    break;
                }
                case MotionEvent.ACTION_MOVE: { // a pointer was moved
                    imgPointer.setX(eventX);
                    imgPointer.setY(eventY);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
//                    imgPointer.setVisibility(View.INVISIBLE);
                    break;
                }
                default:
                    return false;
            }
            v.invalidate();
            return true;
        }
    };

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            mHomeView.getViewTreeObserver().removeOnPreDrawListener(this);
            mHomeView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSelectGameModeView = (ViewGroup) findViewById(R.id.main_select_game_mode);
                    setupGameModeLayoutAnimation(mSelectGameModeView, screenHeight / 22, 100);
                }
            }, 300);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        // setContentView(R.layout.activity_main);
        // loadUI();
        try {
            setContentView(R.layout.activity_main);
            loadUI();
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            System.gc();
            try {
                setContentView(R.layout.activity_main);
                loadUI();
            } catch (OutOfMemoryError oome2) {
                oome2.printStackTrace();
            }
        }
        // Set authentication flag FALSE
        P2ASharedSystemPreferences.setAuthenticatedFlag(MainActivity.this, false);

        mSelectGameModeView = (ViewGroup) findViewById(R.id.main_select_game_mode);
    }

    /*
     *  Matching UI objects to XML
     */
    private void loadUI() {
        mHomeView = (ViewGroup) findViewById(R.id.home_form);
        if (mHomeView.getBackground() == null) {
            mHomeView.setBackground(new BitmapDrawable(getResources(),
                    ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.game_background_final,
                            screenWidth + 150, screenHeight + 150)));
        }

        btnLogin = (ImageView) findViewById(R.id.home_login_btn);
        btnLogin.setOnTouchListener(funButtonListener);
        btnLogin.setOnClickListener(this);
        btnLogin.animate().setDuration(100);

        btnPlayNow = (ImageView) findViewById(R.id.home_play_now_btn);
        btnPlayNow.setOnTouchListener(funButtonListener);
        btnPlayNow.setOnClickListener(this);
        btnPlayNow.animate().setDuration(100);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.login_username_txt);

        mPasswordView = (EditText) findViewById(R.id.login_password_txt);
        mPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            // Check internet connection first
                            if (GeneralHelper.hasConnection()) {
                                attemptLogin();
                            } else {
                                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK).create();
                                // Setting Dialog Title
                                alertDialog.setTitle("ASEAN Game");
                                alertDialog.setMessage("No Internet connection!");
                                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        alertDialog.cancel();
                                    }
                                });
                                alertDialog.show();
                            }

                            return true;
                        }
                        return false;
                    }
                });
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        // Instance sound control image
        imgSoundControl = (ImageView) findViewById(R.id.sound_control);
        P2AContext appContext = (P2AContext) getApplicationContext();
        if (appContext.getSoundPool() != null) {
            imgSoundControl.setImageResource(R.drawable.ic_speaker_on);
            imgSoundControl.setContentDescription(getResources().getString(R.string.sound_turn_on));
            appContext.setSoundOn(true);
        }
        imgSoundControl.setOnTouchListener(funButtonListener);
        imgSoundControl.setOnClickListener(this);

        imgPointer = (ImageView) findViewById(R.id.pointer);
        mHomeView.setOnTouchListener(pointerMotionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHomeView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        imgPointer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnLogin.getId()) {
            v.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    // Setup login animation
                    loadButtonAnimation(btnLogin, R.animator.fadein);
                    // Attempt Login activity
                    // Check internet connection first
                    if (GeneralHelper.hasConnection()) {
                        attemptLogin();
                    } else {
                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK).create();
                        // Setting Dialog Title
                        alertDialog.setTitle("ASEAN Game");
                        alertDialog.setMessage("No Internet connection!");
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Invokes inserting bundled data into the database.
                                alertDialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                }
            });
        } else if (v.getId() == btnPlayNow.getId()) {
            v.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    // Setup play now animation
                    loadButtonAnimation(btnPlayNow, R.animator.fadein);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Start Play Now activity
                            loadPlayNowActivity();
                        }
                    });
                }
            });

        } else if (v.getId() == imgSoundControl.getId()) {
            v.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    if (imgSoundControl.getContentDescription().equals(getResources().getString(R.string.sound_turn_on))) {
                        imgSoundControl.setImageResource(R.drawable.ic_speaker_off);
                        imgSoundControl.setContentDescription(getResources().getString(R.string.sound_turn_off));
                        P2AContext p2AContext = (P2AContext) getApplicationContext();
                        p2AContext.setSoundOn(false);
                    } else {
                        imgSoundControl.setImageResource(R.drawable.ic_speaker_on);
                        imgSoundControl.setContentDescription(getResources().getString(R.string.sound_turn_on));
                        P2AContext p2AContext = (P2AContext) getApplicationContext();
                        p2AContext.setSoundOn(true);
                    }
                }
            });
        }
//        v.removeCallbacks(mSquishRunnable);
    }

    private void loadButtonAnimation(View view, int animationResource) {
        Animation anim = AnimationUtils.loadAnimation(
                P2AContext.getContext(), animationResource);
        view.startAnimation(anim);
    }

    private void loadPlayNowActivity() {
        final Intent selectCountryIntent = new Intent(MainActivity.this,
                SelectCountryActivity.class);
        final Bundle translateBundle = ActivityOptions.makeCustomAnimation(
                MainActivity.this, R.animator.slide_in_left,
                R.animator.slide_out_left).toBundle();
        // Transfer anonymous user info
        final UserDAO userDAO = UserDAO.getInstance();
        // Set Anonymous on Global context
        final P2AContext appContext = (P2AContext) getApplicationContext();
        final User anonymous = userDAO.getAnonymous();
        if (anonymous != null)
            appContext.setcUser(anonymous);

        selectCountryIntent.putExtra(SelectCountryActivity.USER_NAME_FLAG, appContext.getcUser().get_user_name());
        selectCountryIntent.putExtra(SelectCountryActivity.USER_FLAG, 1);     // anonymous

        // Set flags for anonymous user
        P2ASharedSystemPreferences.setAuthenticatedFlag(MainActivity.this, false);
        P2ASharedSystemPreferences.setCurrentUserName(MainActivity.this, "anonymous");
        P2ASharedSystemPreferences.setCurrentPassword(MainActivity.this, BCrypt.hashpw("anonymous", BCrypt.gensalt()));
        P2ASharedSystemPreferences.setUserCreatedDate(MainActivity.this, new Date().toString());

        startActivity(selectCountryIntent, translateBundle);
        // Release memory of UI hidden
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    private void setupGameModeLayoutAnimation(final View view,
                                              final float y, long delay) {
        long animationDuration = BASE_DURATION * 2;

        // Scale around bottom/middle to simplify squash against the window
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());

        // Animate the view down, accelerating, while also stretching in Y and squashing in X
        final PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y, -550, y);
        PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, .7f);
        PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f);
        ObjectAnimator downAnim = ObjectAnimator
                .ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        downAnim.setStartDelay(delay);
        downAnim.setDuration(animationDuration * 3);
        downAnim.setInterpolator(sAccelerator);
        downAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        // Stretch in X, squash in Y, then reverse
        pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 2);
        pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .5f);
        ObjectAnimator stretchAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhSX, pvhSY);
        stretchAnim.setRepeatCount(1);
        stretchAnim.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim.setInterpolator(sDecelerator);
        stretchAnim.setDuration(animationDuration);

        // Animate back to the start
        pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
        pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
        ObjectAnimator upAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhSX, pvhSY);
        upAnim.setDuration(animationDuration * 2);
        upAnim.setInterpolator(sDecelerator);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(downAnim, stretchAnim, upAnim);
        set.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Hide Login form
        mSelectGameModeView.setVisibility(View.INVISIBLE);
    }

    private void loadSelectCountryActivity() {
        Intent selectCountryIntent = new Intent(MainActivity.this,
                SelectCountryActivity.class);

        final String usernameWelcome = mUser.get_user_name();

        selectCountryIntent.putExtra(SelectCountryActivity.USER_NAME_FLAG, usernameWelcome);
        selectCountryIntent.putExtra(SelectCountryActivity.USER_FLAG, 0); // User logged in

        final Bundle translateBundle = ActivityOptions.makeCustomAnimation(
                MainActivity.this, R.animator.slide_in_left,
                R.animator.slide_out_left).toBundle();
        // Release memory of UI hidden
        startActivity(selectCountryIntent, translateBundle);
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
//		else if (!mEmail.contains("@")) {
//            mUsernameView.setError(getString(R.string.error_invalid_email));
//			focusView = mUsernameView;
//			cancel = true;
//		}

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showLoginProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute();
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showLoginProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            BitmapDrawable loginBackground = new BitmapDrawable(getResources(),
                    ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.game_background_final,
                            screenWidth + 150, screenHeight + 150));
            mLoginStatusView.setBackground(loginBackground);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mHomeView.setVisibility(View.VISIBLE);
            mHomeView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mHomeView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mHomeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }


            try {
                final String md5password = GeneralHelper.HexMD5ForString(mPassword);
                final String username = mEmail;

                User validUser = invokeLoginService(md5password, username);
                // Authentication is valid
                if (P2AClientServiceProvider.getResponsedCode() == P2AResponsedCodePattern.SUCCESS) {

                    P2ASharedSystemPreferences.setToken(P2AContext.getContext(),
                            validUser != null ? validUser.get_user_token() : "");
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mAuthTask = null;
            //showLoginProgress(false);

            if (success) {
                // Set flags to authenticated user
                P2ASharedSystemPreferences.setAuthenticatedFlag(P2AContext.getContext(), true);
                P2ASharedSystemPreferences.setCurrentUserName(P2AContext.getContext(), mEmail);
                P2ASharedSystemPreferences.setCurrentPassword(P2AContext.getContext(),
                        BCrypt.hashpw(mPassword, BCrypt.gensalt()));  // Need to encrypted critical these information
                P2ASharedSystemPreferences.setUserCreatedDate(P2AContext.getContext(), new Date().toString());

                // Set user information from the database to the global various
                final UserDAO userDAO = UserDAO.getInstance();
                P2AContext appContext = (P2AContext) getApplicationContext();
                appContext.setcUser(userDAO.findUserLogged(mEmail, mPassword));
                mUser = appContext.getcUser();
                // reset text view
                mUsernameView.setText("");
                mPasswordView.setText("");

                loadSelectCountryActivity();
                onStop();
            } else {
                mPasswordView
                        .setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showLoginProgress(false);
        }

        private User invokeLoginService(String pass, String username){

            final P2AClientServiceProvider p2aClientServiceProvider =
                    P2AClientServiceProvider.getInstance(MainActivity.this);
            final User[] user = new User[1];
            final Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put(P2AHttpHeaderConstants.AUTHORIZATION, pass);
            headerMap.put(P2AHttpHeaderConstants.ACCESS_KEY, username);

            // Volley's JSON Object request
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                    p2aClientServiceProvider.getOriginalServicePath() + "loginHandler.ashx",
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            user[0] = User.parseUserJson(response.toString());
                            if (user[0].get_user_id() > 0 && user[0].get_user_token() != null) {
                                // Save into database
                                final UserDAO userDAO = UserDAO.getInstance();
                                if (!userDAO.isDuplicatedUser(user[0])) {
                                    user[0].set_user_name(mEmail);
                                    user[0].set_user_password(mPassword);
                                    userDAO.insertUser(user[0]);
                                } else {
                                    userDAO.updateUser(user[0]);
                                }
                            }

                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
//                            Log.e(MainActivity.TAG, volleyError.getMessage());
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(P2AHttpHeaderConstants.ACCESS_KEY, headerMap.get(P2AHttpHeaderConstants.ACCESS_KEY));
                    params.put(P2AHttpHeaderConstants.AUTHORIZATION, headerMap.get(P2AHttpHeaderConstants.AUTHORIZATION));
                    return params;
                }
            };
            // Add request to queue
            P2AContext.getInstance().addToRequestQueue(req);

//            final DefaultHttpClient defaultHttpClient = p2aClientServiceProvider.getDefaultClient();
//            final HttpPost httpPost = new HttpPost(p2aClientServiceProvider.getOriginalServicePath() + "loginHandler.ashx");
//            httpPost.setHeader(P2AHttpHeaderConstants.AUTHORIZATION, headerMap.get(P2AHttpHeaderConstants.AUTHORIZATION));
//            httpPost.setHeader(P2AHttpHeaderConstants.ACCESS_KEY, headerMap.get(P2AHttpHeaderConstants.ACCESS_KEY));
//
//            try {
//                final HttpResponse response = defaultHttpClient.execute(httpPost);
//
//                P2AClientServiceProvider.setResponsedCode(response.getStatusLine().getStatusCode());
//                final HttpEntity httpEntity = response.getEntity();
//                InputStream is;
//                is = httpEntity.getContent();
//
//                try {
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(is, "utf-8"), 8);
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    String json;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line).append("\n");
//                    }
//                    is.close();
//                    json = sb.toString();
//                    if (!json.isEmpty()) {
//                        user = User.parseUserJson(json);
//                        if (user.get_user_id() > 0 && user.get_user_token() != null) {
//                            // Save into database
//                            final UserDAO userDAO = UserDAO.getInstance();
//                            if (!userDAO.isDuplicatedUser(user)) {
//                                user.set_user_name(mEmail);
//                                user.set_user_password(mPassword);
//                                userDAO.insertUser(user);
//                            } else {
//                                userDAO.updateUser(user);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return user[0];
        }
    }

}
