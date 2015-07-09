package com.cse.p2a.aseangame.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.R;
import com.cse.p2a.aseangame.data.dao.SessionDAO;
import com.cse.p2a.aseangame.data.dao.UserDAO;
import com.cse.p2a.aseangame.data.model.Session;
import com.cse.p2a.aseangame.data.model.User;
import com.cse.p2a.aseangame.utils.GeneralHelper;
import com.cse.p2a.aseangame.utils.ImageUtils;
import com.cse.p2a.aseangame.utils.P2AClientServiceProvider;
import com.cse.p2a.aseangame.utils.P2AHttpHeaderConstants;
import com.cse.p2a.aseangame.utils.P2AResponsedCodePattern;
import com.cse.p2a.aseangame.utils.P2ASharedSystemPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import libs.BCrypt;

/**
 * Created by HanhDTRAN on 12/7/13.
 *
 * @author HanhDTRAN
 */
public class FinishGameActivity extends Activity implements View.OnClickListener {
    public static final String FINISHED_SESSION_ID_FLAG = "finished_session_id";
    // image naming and path  to include sd card appending name you choose for file
    final static String SCREENSHOTS_LOCATIONS = Environment.getExternalStorageDirectory().toString() + "/Pictures/";
    private int finishedSessionId = -1;
    private ImageView imgSharedFacebook, imgSharedP2A;
    private TextView txtCountryName;
    private TextView txtUsername;
    private TextView txtTotalScore;
    private TextView txtScoreDescription;
    //  private ImageView imgHome;
    private SessionDAO sessionDAO = null;
    private Session mSession = null;
    private SubmitP2AScoreTask submitP2AScoreTask = null;
    private View mSubmitScoreStatusView;
    private View mFinishGameView;
    private TextView mSubmitScoreStatusMessage;
    private String mQuestionImagePath;
    private String mQuestionScreenShotName;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private int screenWidth, screenHeight;
    private View.OnTouchListener motionButtonListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(.9f).scaleY(.9f).setInterpolator(MainActivity.sDecelerator);
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
                        v.animate().scaleX(1).scaleY(1).setInterpolator(MainActivity.sAccelerator);
                        v.setPressed(false);
                    } else {
                        v.animate().scaleX(1).scaleY(1).setInterpolator(MainActivity.sAccelerator);
                    }
                    break;
            }
            return true;
        }
    };

    public void onCreate(Bundle savedInstanceState) {
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
        try {
            setContentView(R.layout.activity_finish_game);
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            // Try to garbage collect first, and have enough memory to create bitmap
            System.gc();

            try {
                setContentView(R.layout.activity_finish_game);
            } catch (OutOfMemoryError oome2) {
                oome2.printStackTrace();
                // TODO Give me solution!
            }
        }
        finishedSessionId = getIntent().getIntExtra(FINISHED_SESSION_ID_FLAG, -1);
        loadFinishAudio();
        loadUI();
        loadAnimations();

        if (finishedSessionId != -1) { // Session has existed
            attemptSessionFinish();
        } else {
            // Catch exception when has got problem for Session id transfer.
            goBackHomeScreen();
        }
    }

    private void loadFinishAudio() {
        P2AContext appContext = (P2AContext) getApplicationContext();
        if (appContext.isSoundOn()) {
            SoundPool soundPool = appContext.getSoundPool();
            int finishAudio = appContext.getFinishAudio();
            this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.play(finishAudio, volume, volume, 1, 0, 1f);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        onTrimMemory(TRIM_MEMORY_COMPLETE);
        // Applied window animation for back action
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
    }

    /**
     * Loading User Interface Objects from XML
     */
    private void loadUI() {

//        imgHome = (ImageView) findViewById(R.id.finish_game_home_btn);
//        imgHome.setOnClickListener(this);

        imgSharedFacebook = (ImageView) findViewById(R.id.finish_game_share_on_facebook);
        imgSharedFacebook.setOnClickListener(this);

        imgSharedP2A = (ImageView) findViewById(R.id.finish_game_share_on_p2a);
        imgSharedP2A.setOnClickListener(this);
        txtTotalScore = (TextView) findViewById(R.id.finish_game_total_score);

        mFinishGameView = findViewById(R.id.finish_game_frame);

        mSubmitScoreStatusView = findViewById(R.id.submit_score_status);
        mSubmitScoreStatusMessage = (TextView) findViewById(R.id.submit_score_status_message);

        final P2AContext appContext = (P2AContext) getApplicationContext();
        txtUsername = (TextView) findViewById(R.id.finish_game_username_txt);
        txtUsername.setText(appContext.getcUser().get_user_name());

        mLoginStatusView = findViewById(R.id.finish_game_login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.finish_game_login_status_message);

        txtCountryName = (TextView) findViewById(R.id.finish_game_country_txt);
        txtCountryName.setText(appContext.getSelectedCountry().get_country_name());

        txtScoreDescription = (TextView) findViewById(R.id.finish_game_score_description);
    }

    /**
     * Loading animations for UI Objects through xml
     */
    private void loadAnimations() {
        loadImageButtonAnimations(imgSharedFacebook, R.anim.activity_finish_fadein);
        loadImageButtonAnimations(imgSharedP2A, R.anim.activity_finish_fadein);
        loadImageButtonAnimations(txtTotalScore, R.anim.activity_finish_fadein);
    }

    /**
     * @param view              view will apply to the animation
     * @param animationResource Resource id for animation xml file
     */
    private void loadImageButtonAnimations(final View view, final int animationResource) {
        final Animation anim = AnimationUtils.loadAnimation(
                P2AContext.getContext(), animationResource);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    /**
     *
     */
    private void attemptSessionFinish() {
        // find the finished session by the session id
        sessionDAO = SessionDAO.getInstance();
        final Session finishedSession = sessionDAO.findFinishedSessionById(finishedSessionId);
        if (finishedSession != null) {
            // Found the session has finished
            // Show total score of the session
            mSession = finishedSession;

            showDescriptionScoreOfTheSession(finishedSession);

        } else {
            // Can't find the session, there no session is suitable!
            Toast.makeText(this, "Can't find the session, there's no session is suitable!!!", Toast.LENGTH_SHORT).show();
            goBackHomeScreen();
        }
    }

    private void showDescriptionScoreOfTheSession(final Session finishedSession) {
        sessionDAO = SessionDAO.getInstance();
        // If user finished 10 countries.
        Set<Integer> finishedCountrySet = sessionDAO.getFinishedCountryOfUser(mSession.get_session_user_id());
        if (finishedCountrySet.size() == 10
                && !P2ASharedSystemPreferences.getPassedAllCountriesFlag(P2AContext.getContext())
                && !P2ASharedSystemPreferences.getUserIdPassedAllCountries(P2AContext.getContext()).
                equals(String.valueOf(mSession.get_session_user_id()))) {

            try {
                mFinishGameView.setBackground(new BitmapDrawable(getResources(),
                        ImageUtils.decodeSampledBitmapFromResource(getResources(),
                                R.drawable.cup_form_1228x768_final, screenWidth, screenHeight)
                ));
                txtTotalScore.setVisibility(View.GONE);
                txtScoreDescription.setText("Passed All Countries");

                P2ASharedSystemPreferences.setPassedAllCountriesFlag(P2AContext.getContext(), true);
                P2ASharedSystemPreferences.setUserIdPassedAllCountried(P2AContext.getContext(),
                        String.valueOf(mSession.get_session_user_id()));

            } catch (OutOfMemoryError oome) {
                oome.printStackTrace();
                // Call garbage collect
                System.gc();
                try {
                    mFinishGameView.setBackground(new BitmapDrawable(getResources(),
                            ImageUtils.decodeSampledBitmapFromResource(getResources(),
                                    R.drawable.cup_form_1228x768_final, screenWidth, screenHeight)
                    ));
                    txtTotalScore.setVisibility(View.GONE);
                    txtScoreDescription.setText("Passed All Countries");
                    P2ASharedSystemPreferences.setPassedAllCountriesFlag(P2AContext.getContext(), true);
                    P2ASharedSystemPreferences.setUserIdPassedAllCountried(P2AContext.getContext(),
                            String.valueOf(mSession.get_session_user_id()));
                } catch (OutOfMemoryError oome2) {
                    oome2.printStackTrace();
                }
            }

        } else if (sessionDAO.isHighestScoreOfThisCountry(finishedSession.get_session_user_id(),
                finishedSession.get_session_country_id(), finishedSession.get_session_id())) {
            try {
                mFinishGameView.setBackground(new BitmapDrawable(getResources(),
                        ImageUtils.decodeSampledBitmapFromResource(getResources(),
                                R.drawable.end_form_1280x768, screenWidth, screenHeight)
                ));
                txtScoreDescription.setText("Highest Score of The Country");
                // Show total score on text view
                showTotalScoreOfTheSession(finishedSession);
            } catch (OutOfMemoryError oome) {
                oome.printStackTrace();
                // Call garbage collect
                System.gc();
                try {
                    mFinishGameView.setBackground(new BitmapDrawable(getResources(),
                            ImageUtils.decodeSampledBitmapFromResource(getResources(),
                                    R.drawable.end_form_1280x768, screenWidth, screenHeight)
                    ));
                    txtScoreDescription.setText("Highest Score of The Country");
                    // Show total score on text view
                    showTotalScoreOfTheSession(finishedSession);
                } catch (OutOfMemoryError oome2) {
                    oome2.printStackTrace();
                }
            }

        } else {
            try {
                mFinishGameView.setBackground(new BitmapDrawable(getResources(),
                        ImageUtils.decodeSampledBitmapFromResource(getResources(),
                                R.drawable.end_form_1280x768, screenWidth, screenHeight)
                ));
                txtScoreDescription.setText("High Score");
                // Show total score on text view
                showTotalScoreOfTheSession(finishedSession);
            } catch (OutOfMemoryError oome) {
                oome.printStackTrace();
                // Call garbage collect
                System.gc();
                try {
                    mFinishGameView.setBackground(new BitmapDrawable(getResources(),
                            ImageUtils.decodeSampledBitmapFromResource(getResources(),
                                    R.drawable.end_form_1280x768, screenWidth, screenHeight)
                    ));
                    txtScoreDescription.setText("High Score");
                    // Show total score on text view
                    showTotalScoreOfTheSession(finishedSession);
                } catch (OutOfMemoryError oome2) {
                    oome2.printStackTrace();
                }
            }
        }
    }

    /**
     * Show total score on the text view
     *
     * @param finishedSession The session has finished
     */
    private void showTotalScoreOfTheSession(final Session finishedSession) {
        String totalScore = String.valueOf(finishedSession.get_session_total_score());
        if (totalScore != null) {
            txtTotalScore.setText(totalScore);
        } else {
            txtTotalScore.setText("N/A");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == imgSharedFacebook.getId()) {
            // Setup animation
            loadImageButtonAnimations(view, R.anim.fadein);
            // Check internet connection
            if (GeneralHelper.hasConnection()) {
                shareTotalScore(getResources().getString(R.string.action_share_on_facebook));

            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).create();
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

        } else if (view.getId() == imgSharedP2A.getId()) {
            // Setup animation
            loadImageButtonAnimations(view, R.anim.fadein);
            // Check internet connection
            if (GeneralHelper.hasConnection()) {
                shareTotalScore(getResources().getString(R.string.action_share_on_p2a));
            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).create();
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
        }
    }

    private void goBackHomeScreen() {
        // Relaunch application
        final Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                getBaseContext().getPackageName());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        // Release memory for UI hidden
        onTrimMemory(TRIM_MEMORY_COMPLETE);
        this.finish();
        System.gc();
    }

    /**
     * Check type of sharing
     *
     * @param typeOfShare share on Facebook or P2A
     */
    private void shareTotalScore(String typeOfShare) {
        final String fbType = getResources().getString(R.string.action_share_on_facebook);

        if (typeOfShare.equals(fbType)) {
            // Share total score on Facebook social network
            shareOnFacebook();
        } else {
            // Share total score on P2A web
            shareOnP2A();
        }
    }

    /**
     * Share total score on Facebook social network
     */
    private void shareOnFacebook() {
        // Take a screen shot
        takeScreenShot(imgSharedFacebook);
        final File screenShotfile = new File(SCREENSHOTS_LOCATIONS, mQuestionScreenShotName);
        final Uri screenShotUri = Uri.fromFile(screenShotfile);
        final String shareSubject = "Congratulation!";
        String shareBody = "http://p2a.asia";
        if (mSession != null) {
            shareBody = "You won P2A ASEAN Game with " + mSession.get_session_total_score() +
                    " score. Visit our site here: http://www.p2a.asia";
        }
        final Intent sharingTotalScoreIntent = new Intent(Intent.ACTION_SEND);
        sharingTotalScoreIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        sharingTotalScoreIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingTotalScoreIntent.setType("image/*");
        sharingTotalScoreIntent.putExtra(Intent.EXTRA_STREAM, screenShotUri);
        sharingTotalScoreIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingTotalScoreIntent, "Share your score via"));
    }

    /**
     * Share total score on P2A web
     */
    private void shareOnP2A() {
        if (mSession != null) {
            final int userId = mSession.get_session_user_id();
            if (userId > 1 && !P2ASharedSystemPreferences.getToken(P2AContext.getContext()).equals("")) {
                // Has existed the P2A user
                // Get user's account information
                final P2AContext appContext = (P2AContext) getApplicationContext();

                final User p2aUser = appContext.getcUser();
                if (p2aUser != null) {
                    // Invoke submit score task to P2A server.
                    invokeSubmitP2AScoreService(p2aUser);
                }

            } else {
                // This is Anonymous now.
                // Allow Anonymous to commit on P2A server? OK -> Request user log in
                // Show Request Login form
                showRequestLoginForm();
            }
        } else {
        }
    }

    /**
     * Show Login pop-up window.
     */
    private void showRequestLoginForm() {
        final LayoutInflater li = LayoutInflater.from(FinishGameActivity.this);
        final View loginView = li.inflate(R.layout.popup_login_confirm, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FinishGameActivity.this);
        final ImageView loginBtn = (ImageView) loginView.findViewById(R.id.login_confirm_button);
        mUsernameView = (EditText) loginView.findViewById(R.id.login_confirm_username);
        mPasswordView = (EditText) loginView.findViewById(R.id.login_confirm_password);
        final AlertDialog loginAlertDialog = alertDialogBuilder.create();
        loginAlertDialog.setView(loginView);
        loginBtn.setOnTouchListener(motionButtonListener);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.postOnAnimation(new Runnable() {
                    @Override
                    public void run() {
                        if (GeneralHelper.hasConnection()) {
                            attemptLogin(loginAlertDialog);
                        } else {
                            final AlertDialog alertDialog = new AlertDialog.Builder(FinishGameActivity.this, AlertDialog.THEME_HOLO_DARK).create();
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
            }
        });
        loginAlertDialog.show();
    }

    /**
     * Call committing score task.
     *
     * @param p2aUser User registered on P2A website
     */
    private void invokeSubmitP2AScoreService(User p2aUser) {
        if (submitP2AScoreTask != null) {
            return;
        }
        mSubmitScoreStatusMessage.setText("Committing score to P2A ...");
        showSubmitScoreProgress(true);
        submitP2AScoreTask = new SubmitP2AScoreTask();
        submitP2AScoreTask.execute(p2aUser);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showSubmitScoreProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mSubmitScoreStatusView.setBackground(new BitmapDrawable(getResources(),
                    ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.end_form_1280x768, screenWidth, screenHeight)));

            mSubmitScoreStatusView.setVisibility(View.VISIBLE);

            mSubmitScoreStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSubmitScoreStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mFinishGameView.setVisibility(View.VISIBLE);
            mFinishGameView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFinishGameView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSubmitScoreStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFinishGameView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Capturing the recent question screen
     */
    private void takeScreenShot(final View v) {
        // Get device dimmensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // create bitmap screen capture

        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        // Create the bitmap to use to draw the screenshot
        final Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);

        // Get current theme to know which background to use
        final Activity activity = this;
        final Resources.Theme theme = activity.getTheme();
        final TypedArray ta = theme
                .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        final int res = ta.getResourceId(0, 0);
        final Drawable background = activity.getResources().getDrawable(res);

        // Draw background
        background.draw(canvas);

        // Draw views
        view.draw(canvas);

        // Save the screenshot to the file system
        FileOutputStream fos;
        try {

            final File sddir = new File(SCREENSHOTS_LOCATIONS);

            if (!sddir.exists()) {
                sddir.mkdirs();
            }

            mQuestionScreenShotName = "question_capture"
                    + System.currentTimeMillis() + ".jpg";

            mQuestionImagePath = SCREENSHOTS_LOCATIONS + mQuestionScreenShotName;

            fos = new FileOutputStream(mQuestionImagePath);

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)) {
                return;
            }
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    void attemptLogin(AlertDialog loginAlertDialog) {
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
            mAuthTask.execute(loginAlertDialog);
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

            mLoginStatusView.setBackground(new BitmapDrawable(getResources(),
                    ImageUtils.decodeSampledBitmapFromResource(getResources(),
                            R.drawable.end_form_1280x768, screenWidth, screenHeight)
            ));

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mFinishGameView.setVisibility(View.VISIBLE);
            mFinishGameView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFinishGameView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFinishGameView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     *
     */
    private class SubmitP2AScoreTask extends AsyncTask<User, Void, Boolean> {
        @Override
        protected Boolean doInBackground(User... users) {
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }
            final int countryId = mSession.get_session_country_id();
//            final float totalScore = sessionDAO.computeTotalScoreOfUser(mUser.get_user_id());
            final float totalScore = mSession.get_session_total_score();
            final String finishDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
            final String finishTime = new SimpleDateFormat("HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
            // Find token of session
            final String token = P2ASharedSystemPreferences.getToken(P2AContext.getContext());

            // Request GET questions service here.
            final P2AClientServiceProvider p2aClientServiceProvider =
                    P2AClientServiceProvider.getInstance(FinishGameActivity.this);
            final DefaultHttpClient defaultClient = p2aClientServiceProvider.getDefaultClient();
            final HttpPost httpPost = new HttpPost(p2aClientServiceProvider.getOriginalServicePath() + "commitScoreHandler.ashx");
            httpPost.setHeader(P2AHttpHeaderConstants.TOKEN, token);
            httpPost.setHeader(P2AHttpHeaderConstants.COUNTRY_ID, String.valueOf(countryId));
            httpPost.setHeader(P2AHttpHeaderConstants.TOTAL_SCORE, String.valueOf(totalScore));
            httpPost.setHeader(P2AHttpHeaderConstants.FINISH_DATE, finishDate);
            httpPost.setHeader(P2AHttpHeaderConstants.FINISH_TIME, finishTime);
            try {
                final HttpResponse response = defaultClient.execute(httpPost);

                P2AClientServiceProvider.setResponsedCode(response.getStatusLine().getStatusCode());

//                final HttpEntity httpEntity = response.getEntity();
//                InputStream is;
//                is = httpEntity.getContent();
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
////                    Log.w(LOG_TAG, json + " " + token + ";" + countryId + ";"
////                            + totalScore + ";" + finishDate + "; " + finishTime);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (P2AClientServiceProvider.getResponsedCode() == P2AResponsedCodePattern.SUCCESS) {
                mSession.set_session_token(token);
                sessionDAO.updateSession(mSession);
                return true;
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            submitP2AScoreTask = null;
            showSubmitScoreProgress(false);
            if (aBoolean) {
                final AlertDialog alertDialog = new AlertDialog.Builder(FinishGameActivity.this, AlertDialog.THEME_HOLO_DARK).create();
                // Setting Dialog Title
                alertDialog.setTitle("ASEAN Game");
                alertDialog.setMessage("Your score is committed successfully!");
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.cancel();
//                        FinishGameActivity.this.finish(); // Exist Finish screen.

                    }
                });
                alertDialog.show();
            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(FinishGameActivity.this, AlertDialog.THEME_HOLO_DARK).create();
                // Setting Dialog Title
                alertDialog.setTitle("ASEAN Game");
                alertDialog.setMessage("Committed score fail, please LOGIN again.");
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.cancel();
                        FinishGameActivity.this.finish(); // Exist Finish screen.
                    }
                });
                alertDialog.show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showSubmitScoreProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<AlertDialog, Void, Boolean> {
        @Override
        protected Boolean doInBackground(AlertDialog... params) {
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }
            final P2AClientServiceProvider p2aClientServiceProvider =
                    P2AClientServiceProvider.getInstance(FinishGameActivity.this);
            User user = null;
            try {
                final String md5password = GeneralHelper.HexMD5ForString(mPassword);
                final String username = mEmail;
                final DefaultHttpClient defaultHttpClient = p2aClientServiceProvider.getDefaultClient();
                final HttpPost httpPost = new HttpPost(p2aClientServiceProvider.getOriginalServicePath() + "loginHandler.ashx");
                final Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put(P2AHttpHeaderConstants.AUTHORIZATION, md5password);
                headerMap.put(P2AHttpHeaderConstants.ACCESS_KEY, username);
                httpPost.setHeader(P2AHttpHeaderConstants.AUTHORIZATION, headerMap.get(P2AHttpHeaderConstants.AUTHORIZATION));
                httpPost.setHeader(P2AHttpHeaderConstants.ACCESS_KEY, headerMap.get(P2AHttpHeaderConstants.ACCESS_KEY));

                try {
                    final HttpResponse response = defaultHttpClient.execute(httpPost);

                    P2AClientServiceProvider.setResponsedCode(response.getStatusLine().getStatusCode());
                    final HttpEntity httpEntity = response.getEntity();
                    InputStream is;
                    is = httpEntity.getContent();

                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is, "utf-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        String json;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        is.close();
                        json = sb.toString();
                        if (!json.isEmpty()) {
                            user = User.parseUserJson(json);
                            if (user.get_user_id() > 0 && user.get_user_token() != null) {
                                // Save into database
                                final UserDAO userDAO = UserDAO.getInstance();
                                if (!userDAO.isDuplicatedUser(user)) {
                                    user.set_user_name(mEmail);
                                    user.set_user_password(mPassword);
                                    userDAO.insertUser(user);
                                } else {
                                    userDAO.updateUser(user);
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            // Authentication is valid
            if (P2AClientServiceProvider.getResponsedCode() == P2AResponsedCodePattern.SUCCESS && user != null) {
//                Log.w(LOG_TAG, "Token: " + user.get_user_token());
                // Set current token.
                P2ASharedSystemPreferences.setToken(P2AContext.getContext(), user.get_user_token());
                params[0].dismiss();
                return true;
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showLoginProgress(false);
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
                if (appContext.getcUser() != null && mSession != null) {
                    // Set user id of the current session for user logged
                    mSession.set_session_user_id(appContext.getcUser().get_user_id());
                    mSession.set_session_token(appContext.getcUser().get_user_token());
                    sessionDAO.updateSession(mSession);
                    // Invoke submit score task to P2A server.
                    invokeSubmitP2AScoreService(appContext.getcUser());
                }
                // reset text view
                mUsernameView.setText("");
                mPasswordView.setText("");
            } else {
                mPasswordView
                        .setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showLoginProgress(false);

        }
    }

}
