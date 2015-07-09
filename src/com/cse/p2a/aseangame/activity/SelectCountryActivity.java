package com.cse.p2a.aseangame.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.R;
import com.cse.p2a.aseangame.data.P2AGameDbHelper;
import com.cse.p2a.aseangame.data.dao.AnswerDAO;
import com.cse.p2a.aseangame.data.dao.CountryDAO;
import com.cse.p2a.aseangame.data.dao.QuestionDAO;
import com.cse.p2a.aseangame.data.dao.SessionDAO;
import com.cse.p2a.aseangame.data.dao.UserDAO;
import com.cse.p2a.aseangame.data.model.Answer;
import com.cse.p2a.aseangame.data.model.Country;
import com.cse.p2a.aseangame.data.model.LeaderBoard;
import com.cse.p2a.aseangame.data.model.Question;
import com.cse.p2a.aseangame.data.model.Session;
import com.cse.p2a.aseangame.data.model.User;
import com.cse.p2a.aseangame.utils.ActionItem;
import com.cse.p2a.aseangame.utils.GeneralHelper;
import com.cse.p2a.aseangame.utils.ImageFragment;
import com.cse.p2a.aseangame.utils.ImageUtils;
import com.cse.p2a.aseangame.utils.OnTextFragmentAnimationEndListener;
import com.cse.p2a.aseangame.utils.P2AClientServiceProvider;
import com.cse.p2a.aseangame.utils.P2AHttpHeaderConstants;
import com.cse.p2a.aseangame.utils.P2ASharedSystemPreferences;
import com.cse.p2a.aseangame.utils.QuickAction;
import com.cse.p2a.aseangame.utils.QuickAction.OnActionItemClickListener;
import com.cse.p2a.aseangame.utils.TextFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Hanh D. TRAN
 */
public class SelectCountryActivity extends Activity implements
        OnClickListener, OnTextFragmentAnimationEndListener,
        FragmentManager.OnBackStackChangedListener {
    public static final String USER_NAME_FLAG = "user_name_welcome";
    public static final String USER_FLAG = "user_flag";       // 1 - anonymous; 0 - defined user
    private static final int ID_INFO = 0;
    private final ArrayList<ArrayList<Float>> locationCoordinates = new ArrayList<ArrayList<Float>>();
    private final CountryDAO selectCountryDAO;
    private ImageView imgVietnamLocation, imgLaoLocation, imgCambodiaLocation,
            imgThailandLocation, imgPhilippinesLocation, imgIndonesiaLocation,
            imgSingaporeLocation, imgMalaysiaLocation, imgBruneiLocation,
            imgMyanmarLocation;
    private Country mSelectedCountry = null;
    private Session mSession = null;
    private SyncQuestionTask mSyncQuestionTask = null;
    private CheckNewQuestion checkNewQuestionTask = null;
    private GetTopAllScoreTask mGetTopAllScoreTask = null;
    private View mSyncStatusView;
    private View mSelectCountryView;
    private ImageView btnSyncQuestion;
    private ImageView btnHelp;
    private ImageView btnLeaderBoard;
    // User's information response if authentication is valid
    private User mUser = null;
    // Animating Help fragment instances
    private ImageFragment mImageFragment;
    private TextFragment mTextFragment;
    private View mDarkHoverView;
    private boolean mDidSlideOut = false;
    private boolean mIsAnimating = false;
    private int screenWidth, screenHeight;
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mSelectCountryView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mSelectCountryView.postDelayed(new Runnable() {
                        public void run() {
                            loadLocationsUI();
                            getCoordinatesOfLocations(screenWidth, screenHeight);
                            loadInteractedUI();
                        }
                    }, 100);
                    return true;
                }
            };

    public SelectCountryActivity() {
        selectCountryDAO = CountryDAO.getInstance();
    }

    @Override
    public void onBackStackChanged() {
        if (!mDidSlideOut) {
            slideForward(null);
        }
    }

    @Override
    public void onAnimationEnd() {
        mIsAnimating = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        try {

            setContentView(R.layout.activity_select_country);

            mSelectCountryView = findViewById(R.id.select_country_container);

            mSelectCountryView.setBackground(new BitmapDrawable(getResources(),
                    ImageUtils.decodeSampledBitmapFromResource(getResources(),
                            R.drawable.map_jpg_final, screenWidth, screenHeight)));
            mSelectCountryView.setOnClickListener(this);

        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            System.gc();
            try {
                setContentView(R.layout.activity_select_country);

                mSelectCountryView = findViewById(R.id.select_country_container);

                mSelectCountryView.setBackground(new BitmapDrawable(getResources(),
                        ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.map_jpg_final, screenWidth, screenHeight)));
                mSelectCountryView.setOnClickListener(this);
            } catch (OutOfMemoryError oome2) {
                oome2.printStackTrace();
                // TODO Ask user to restart

            }

        }

        // load welcome board
        View mWelcomeBoard = findViewById(R.id.welcome_board);
        setupBounceAnimation(mWelcomeBoard, mWelcomeBoard.getX(), mWelcomeBoard.getX(), 500);

        mSyncStatusView = findViewById(R.id.sync_status);

        btnSyncQuestion = (ImageView) findViewById(R.id.home_sync_question_btn);
        btnSyncQuestion.setOnClickListener(this);

        btnHelp = (ImageView) findViewById(R.id.help_btn);
        btnHelp.setOnClickListener(this);

        btnLeaderBoard = (ImageView) findViewById(R.id.leader_board_btn);
        btnLeaderBoard.setOnClickListener(this);
        try {
            // Set Help fragment
            mDarkHoverView = findViewById(R.id.dark_hover_view);
            mDarkHoverView.setAlpha(0);
            mImageFragment = (ImageFragment) getFragmentManager().findFragmentById(R.id.move_fragment);
            mTextFragment = new TextFragment(screenWidth, screenHeight);
            getFragmentManager().addOnBackStackChangedListener(this);
            mTextFragment.setClickListener(this);
            mTextFragment.setOnTextFragmentAnimationEnd(this);
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            System.gc();
            try {
                // Set Help fragment
                mDarkHoverView = findViewById(R.id.dark_hover_view);
                mDarkHoverView.setAlpha(0);
                mImageFragment = (ImageFragment) getFragmentManager().findFragmentById(R.id.move_fragment);
                mTextFragment = new TextFragment(screenWidth, screenHeight);
                getFragmentManager().addOnBackStackChangedListener(this);
                mTextFragment.setClickListener(this);
                mTextFragment.setOnTextFragmentAnimationEnd(this);
            } catch (OutOfMemoryError oome2) {
                oome2.printStackTrace();
                // TODO Handle can't load Help screen
            }
        }
        loadLocationsUI();
        checkNewQuestionTask = new CheckNewQuestion(SelectCountryActivity.this);
        checkNewQuestionTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onTrimMemory(TRIM_MEMORY_BACKGROUND);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mSelectCountryView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load XML UI through objects.
     */
    private void loadInteractedUI() {
        // Set current user name
        final TextView mUserNameView = (TextView) findViewById(R.id.welcome_user_name);
        final P2AContext appContext = (P2AContext) getApplicationContext();
        String mUserNameWelcome = appContext.getcUser().get_user_name();
        mUserNameView.setText(mUserNameWelcome);
        loadLocationAnimations();
    }

    private void loadLocationsUI() {
        P2AContext appContext = (P2AContext) getApplicationContext();
        User cUser = appContext.getcUser();
        List<Integer> nonFinishContries = nonFinishCountriesOfUser(cUser.get_user_id());
        // Brunei's location
        imgBruneiLocation = (ImageView) findViewById(R.id.location_1);
        imgBruneiLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgBruneiLocation.getContentDescription().toString()))) {
            imgBruneiLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgBruneiLocation.setImageResource(R.drawable.location_final);
        }

        // Cambodia's location
        imgCambodiaLocation = (ImageView) findViewById(R.id.location_2);
        imgCambodiaLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgCambodiaLocation.getContentDescription().toString()))) {
            imgCambodiaLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgCambodiaLocation.setImageResource(R.drawable.location_final);
        }
        // Indonesia's location
        imgIndonesiaLocation = (ImageView) findViewById(R.id.location_3);
        imgIndonesiaLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgIndonesiaLocation.getContentDescription().toString()))) {
            imgIndonesiaLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgIndonesiaLocation.setImageResource(R.drawable.location_final);
        }
        // Lao's location
        imgLaoLocation = (ImageView) findViewById(R.id.location_4);
        imgLaoLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgLaoLocation.getContentDescription().toString()))) {
            imgLaoLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgLaoLocation.setImageResource(R.drawable.location_final);
        }
        // Malaysia's location
        imgMalaysiaLocation = (ImageView) findViewById(R.id.location_5);
        imgMalaysiaLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgMalaysiaLocation.getContentDescription().toString()))) {
            imgMalaysiaLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgMalaysiaLocation.setImageResource(R.drawable.location_final);
        }
        // Myanmar's location
        imgMyanmarLocation = (ImageView) findViewById(R.id.location_6);
        imgMyanmarLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgMyanmarLocation.getContentDescription().toString()))) {
            imgMyanmarLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgMyanmarLocation.setImageResource(R.drawable.location_final);
        }
        // Philippines's location
        imgPhilippinesLocation = (ImageView) findViewById(R.id.location_7);
        imgPhilippinesLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgPhilippinesLocation.getContentDescription().toString()))) {
            imgPhilippinesLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgPhilippinesLocation.setImageResource(R.drawable.location_final);
        }
        // Singapore's location
        imgSingaporeLocation = (ImageView) findViewById(R.id.location_8);
        imgSingaporeLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgSingaporeLocation.getContentDescription().toString()))) {
            imgSingaporeLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgSingaporeLocation.setImageResource(R.drawable.location_final);
        }
        // Thailand's location
        imgThailandLocation = (ImageView) findViewById(R.id.location_9);
        imgThailandLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgThailandLocation.getContentDescription().toString()))) {
            imgThailandLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgThailandLocation.setImageResource(R.drawable.location_final);
        }
        // Vietnam's location
        imgVietnamLocation = (ImageView) findViewById(R.id.location_10);
        imgVietnamLocation.setOnClickListener(this);
        if (nonFinishContries != null
                && nonFinishContries.contains(Integer.parseInt(imgVietnamLocation.getContentDescription().toString()))) {
            imgVietnamLocation.setImageResource(R.drawable.location_final_hover);
        } else {
            imgVietnamLocation.setImageResource(R.drawable.location_final);
        }
    }

    /**
     * Load locations animation
     */
    private void loadLocationAnimations() {
        // Brunei start animations
        setupBounceAnimation(imgBruneiLocation,
                locationCoordinates.get(0).get(0), locationCoordinates.get(0)
                .get(1), 600);
        // Cambodia
        setupBounceAnimation(imgCambodiaLocation, locationCoordinates.get(1)
                .get(0), locationCoordinates.get(1).get(1), 600);
        // Indonesia
        setupBounceAnimation(imgIndonesiaLocation, locationCoordinates.get(2)
                .get(0), locationCoordinates.get(2).get(1), 1000);
        // Lao
        setupBounceAnimation(imgLaoLocation, locationCoordinates.get(3).get(0),
                locationCoordinates.get(3).get(1), 800);
        // Malaysia
        setupBounceAnimation(imgMalaysiaLocation, locationCoordinates.get(4)
                .get(0), locationCoordinates.get(4).get(1), 800);
        // Myanmar
        setupBounceAnimation(imgMyanmarLocation, locationCoordinates.get(5)
                .get(0), locationCoordinates.get(5).get(1), 1200);
        // Philippines
        setupBounceAnimation(imgPhilippinesLocation, locationCoordinates.get(6)

                .get(0), locationCoordinates.get(6).get(1), 1200);
        // Singapore
        setupBounceAnimation(imgSingaporeLocation, locationCoordinates.get(7)
                .get(0), locationCoordinates.get(7).get(1), 1800);
        // Thailand
        setupBounceAnimation(imgThailandLocation, locationCoordinates.get(8)
                .get(0), locationCoordinates.get(8).get(1), 1800);
        // Vietnam
        setupBounceAnimation(imgVietnamLocation, locationCoordinates.get(9)
                .get(0), locationCoordinates.get(9).get(1), 1800);
    }

    /**
     * Setup Bounce animation for locations.
     *
     * @param view  - locations symbol to countries.
     * @param x     - X translated coordination.
     * @param y     - Y translated coordination
     * @param delay - start delay time in milliseconds.
     */
    private void setupBounceAnimation(final View view, final float x,
                                      final float y, long delay) {
        long animationDuration = MainActivity.BASE_DURATION * 2;

        final PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(
                View.TRANSLATION_X, x, x);
        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y, -150, y);
        final ObjectAnimator bounceAnimation = ObjectAnimator
                .ofPropertyValuesHolder(view, pvhX, pvhY);
        bounceAnimation.setStartDelay(delay);
        bounceAnimation.setDuration(animationDuration * 3);
        bounceAnimation.setInterpolator(MainActivity.BOUNCE_INTERPOLATOR);
        bounceAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }
        });
        bounceAnimation.start();
    }

    /**
     * Setup animation and click event on locations.
     *
     * @param view
     */
    private void onClickLocationAnimation(View view) {

        final Animation anim = AnimationUtils.loadAnimation(
                P2AContext.getContext(), R.anim.fadein);

        final QuickAction quickAction = new QuickAction(
                getApplicationContext(), QuickAction.VERTICAL);
        // Button Pressed then apply animation
        view.startAnimation(anim);
        // Setup name of country pressed
        setmSelectedCountry(selectCountryDAO.findCountryById(view
                .getContentDescription().toString()));
        final ActionItem infoItem = new ActionItem(ID_INFO, getmSelectedCountry().get_country_name(),
                "                                  ",
                getResources().getDrawable(R.drawable.ic_play));
        infoItem.setSticky(true);
        quickAction.addActionItem(infoItem);

        // Get 5 highest score of this country
        final SessionDAO sessionDAO = SessionDAO.getInstance();
        List<Session> highestSession = sessionDAO.find5HighestScoreSessionOfCountry(mSelectedCountry.get_country_id());
        try {
            // If the finished sessions are existed.
            if (highestSession != null) {
                setupRealHighestSessionScoreOfCountry(quickAction, highestSession);
                quickAction.setAnimStyle(QuickAction.ANIM_REFLECT);
                quickAction
                        .setOnActionItemClickListener(new OnActionItemClickListener() {
                            @Override
                            public void onItemClick(QuickAction source, int pos,
                                                    int actionId) {
                                if (pos == ID_INFO) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            quickAction.dismiss();
                                            playGame();
                                        }
                                    });
                                }
                            }
                        });
            } else {
//              setupFakeHighestSessionScore(quickAction, 4);
                quickAction.setAnimStyle(QuickAction.ANIM_AUTO);
                quickAction
                        .setOnActionItemClickListener(new OnActionItemClickListener() {

                            @Override
                            public void onItemClick(QuickAction source, int pos,
                                                    int actionId) {
                                if (pos == ID_INFO) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            quickAction.dismiss();
                                            playGame();
                                        }
                                    });

                                }
                            }
                        });
            }
        } catch (Exception e) {
            quickAction.setAnimStyle(QuickAction.ANIM_AUTO);
            quickAction
                    .setOnActionItemClickListener(new OnActionItemClickListener() {

                        @Override
                        public void onItemClick(QuickAction source, int pos,
                                                int actionId) {
                            if (pos == ID_INFO) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        quickAction.dismiss();
                                        playGame();
                                    }
                                });

                            }
                        }
                    });
        }

        quickAction.show(view);
    }

    /**
     * @param quickAction - the row that present a session's score
     * @param size        - size of faked records.
     */
//    private void setupFakeHighestSessionScore(QuickAction quickAction, int size) {
//        for (int i = 1; i <= size; i++) {
//            final ActionItem item = new ActionItem(i, "anonymous " + i, "1260", getResources().getDrawable(R.drawable.player25x25));
//            quickAction.addActionItem(item);
//        }
//    }

    /**
     * @param qAction  the row that present the session's score
     * @param sessions list of highest session's score of the country
     */
    private void setupRealHighestSessionScoreOfCountry(QuickAction qAction, List<Session> sessions) {
        UserDAO userDAO = UserDAO.getInstance();
        try {
            for (int i = 0; i < sessions.size(); i++) {
                User user = userDAO.findP2AUser(sessions.get(i).get_session_user_id());
                final ActionItem item = new ActionItem(i + 1, user.get_user_name(),
                        " " + sessions.get(i).get_session_total_score(),
                        getResources().getDrawable(R.drawable.player25x25));
                qAction.addActionItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Forward to Play Game activity
     */
    private void playGame() {
        try {

            final Intent playGameIntent = new Intent(this,
                    PlayGameActivity.class);

            final Bundle translateBundle = ActivityOptions.makeCustomAnimation(
                    SelectCountryActivity.this, R.anim.slide_in_left,
                    R.anim.slide_out_left).toBundle();

            playGameIntent.putExtra(PlayGameActivity.ID_COUNTRY,
                    getmSelectedCountry().get_country_id());

            // Find session of current user if NULL, create new one.
            final P2AContext appContext = (P2AContext) getApplicationContext();
            appContext.setSelectedCountry(getmSelectedCountry());

            final Session currentSession = findSessionOfUser(appContext.getcUser().get_user_id(), getmSelectedCountry().get_country_id());

            final int userFlagValue = getIntent().getIntExtra(USER_FLAG, 1);

            if (userFlagValue == 1) {// Anonymous here
                if (currentSession != null) { // Check  non-finish session
                    // Existed session
                    appContext.setCurrentSession(currentSession);
                    mSession = appContext.getCurrentSession();
                    playGameIntent.putExtra(PlayGameActivity.ID_SESSION, mSession.get_session_id());

                } else {
                    // New session
                    mSession = null;
                }

            } else if (userFlagValue == 0) { // User logged in
                if (currentSession != null) { // Check  non-finish session
                    // Existed session
                    appContext.setCurrentSession(currentSession);
                    mSession = appContext.getCurrentSession();
                    playGameIntent.putExtra(PlayGameActivity.ID_SESSION, mSession.get_session_id());

                } else {
                    // New session
                    mSession = null;
                }
            }
            startActivity(playGameIntent, translateBundle);
            // Release memory of UI hidden
            onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Hide locations.
        imgBruneiLocation.setVisibility(View.INVISIBLE);
        imgCambodiaLocation.setVisibility(View.INVISIBLE);
        imgIndonesiaLocation.setVisibility(View.INVISIBLE);
        imgLaoLocation.setVisibility(View.INVISIBLE);
        imgMalaysiaLocation.setVisibility(View.INVISIBLE);
        imgMyanmarLocation.setVisibility(View.INVISIBLE);
        imgPhilippinesLocation.setVisibility(View.INVISIBLE);
        imgSingaporeLocation.setVisibility(View.INVISIBLE);
        imgThailandLocation.setVisibility(View.INVISIBLE);
        imgVietnamLocation.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * @param userId
     * @param countryId
     * @return non-finish session of the user
     */
    private Session findSessionOfUser(int userId, int countryId) {
        final SessionDAO sessionDAO = SessionDAO.getInstance();
        final Session session = sessionDAO.findSessionByUserId(userId, countryId);
        return session;
    }

    /**
     * Setup coordinations of locations up to the current screen size
     *
     * @param screenWidth
     * @param screenHeight
     */
    private void getCoordinatesOfLocations(int screenWidth, int screenHeight) {
        float x, y;
        ArrayList<Float> coordinates = new ArrayList<Float>();
        // Brunei's location
        x = screenWidth * 48 / 100;
        y = screenHeight * 52 / 100;
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Cambodia's location
        x = screenWidth * 33 / 100;
        y = screenHeight * 38 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Indonesia's location
        x = screenWidth * 47 / 100;
        y = screenHeight * 62 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Lao's location
        x = screenWidth * (float) 35.5 / 100;
        y = screenHeight * 32 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Malaysia's location
        x = screenWidth * 29 / 100;
        y = screenHeight * 53 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Myanma's location
        x = screenWidth * 20 / 100;
        y = screenHeight * 23 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Philippines's location
        x = screenWidth * 60 / 100;
        y = screenHeight * 40 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Singapore's location
        x = screenWidth * 32 / 100;
        y = screenHeight * 57 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Thailand's location
        x = screenWidth * 28 / 100;
        y = screenHeight * 34 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);

        // Vietnam's location
        x = screenWidth * (float) 34.2 / 100;
        y = screenHeight * 23 / 100;
        coordinates = new ArrayList<Float>();
        coordinates.add(x);
        coordinates.add(y);
        locationCoordinates.add(coordinates);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == imgBruneiLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgCambodiaLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgIndonesiaLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgLaoLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgMalaysiaLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgMyanmarLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgPhilippinesLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgSingaporeLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgThailandLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == imgVietnamLocation.getId()) {
            onClickLocationAnimation(v);
        } else if (v.getId() == btnSyncQuestion.getId()) {
            // Setup sync animation
            loadButtonAnimation(btnSyncQuestion, R.anim.fadein);
            // Start sync question action
            startSyncNewQuestions();
        } else if (v.getId() == btnHelp.getId()) {
            // Setup help animation
            loadButtonAnimation(btnHelp, R.anim.fadein);
            // Start help action
            switchFragments();
        } else if (v.getId() == mSelectCountryView.getId()) {
            if (mDidSlideOut) {
                mDidSlideOut = false;
                getFragmentManager().popBackStack();
                mDarkHoverView.animate().alpha(.0f).setDuration(300);
            }
        } else if (v.getId() == btnLeaderBoard.getId()) {
            onClickLeaderBoard(btnLeaderBoard);
        }
    }

    /**
     * Setup animation and click event on Leader Board.
     *
     * @param view
     */
    private void onClickLeaderBoard(ImageView view) {
//        Toast.makeText(this,"Click Leader Board",Toast.LENGTH_SHORT).show();
        final QuickAction quickAction = new QuickAction(
                getApplicationContext(), QuickAction.VERTICAL);
        final ActionItem infoItem = new ActionItem(ID_INFO, "Top players          ",
                getResources().getDrawable(R.drawable.ic_leader_scroll));
        infoItem.setSticky(true);
        quickAction.addActionItem(infoItem);
        quickAction.show(view);
        // Check internet connection first
        if (GeneralHelper.hasConnection()) {
            // Invoke top score all country service.
            mGetTopAllScoreTask = new GetTopAllScoreTask(quickAction, SelectCountryActivity.this);
            mGetTopAllScoreTask.execute();
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).create();
            alertDialog.setMessage("No Internet connection!");
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Invokes inserting bundled data into the database.
                    isFirstTimeBundledData();
                    alertDialog.cancel();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        // super.onBackPressed();
        // Applied window animation for back action
//        overridePendingTransition(R.anim.slide_in_right,
//                R.anim.slide_out_right);
//        if (mDidSlideOut) {
//            mDidSlideOut = false;
//            getFragmentManager().popBackStack();
//            mDarkHoverView.animate().alpha(.0f).setDuration(300);
//
//        }

    }

    synchronized Country getmSelectedCountry() {
        return mSelectedCountry;
    }

    synchronized void setmSelectedCountry(Country mSelectedCountry) {
        this.mSelectedCountry = mSelectedCountry;
    }

    private List<Integer> nonFinishCountriesOfUser(int userId) {
        List<Integer> countriesID;
        final SessionDAO sessionDAO = SessionDAO.getInstance();
        countriesID = sessionDAO.findCountriesNonFinish(userId);
        return countriesID;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void loadButtonAnimation(View view, final int animationResource) {
        Animation anim = AnimationUtils.loadAnimation(
                P2AContext.getContext(), animationResource);
        view.startAnimation(anim);
    }

    private void requestUserLogin() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).create();
        // Setting Dialog Title
        alertDialog.setTitle("ASEAN Game");
        alertDialog.setMessage("Required login!");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Invokes inserting bundled data into the database.
//                Toast.makeText(MainActivity.this,"Loading data from bundled data!",Toast.LENGTH_LONG).show();
                alertDialog.cancel();
                onBackPressed();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void startSyncQuestion() {
        if (mSyncQuestionTask != null) {
            return;
        }
        // It's not required login.
        showSyncProgress(true);
        mSyncQuestionTask = new SyncQuestionTask(SelectCountryActivity.this);
        mSyncQuestionTask.execute();
//        final P2AContext appContext = (P2AContext) getApplicationContext();
//        mUser = appContext.getcUser();
//        if (mUser.get_user_id() > 1 && !mUser.get_user_token().isEmpty()
//                && !P2ASharedSystemPreferences.getCurrentPassword(P2AContext.getContext()).isEmpty()) {
//            showSyncProgress(true);
//            mSyncQuestionTask = new SyncQuestionTask();
//            mSyncQuestionTask.execute((Void) null);
//        } else {
//            requestUserLogin();
//        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showSyncProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            // Call garbage collect
            System.gc();
            mSyncStatusView.setBackground(new BitmapDrawable(getResources(),
                    ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.map_jpg_final, screenWidth, screenHeight)));

            mSyncStatusView.setVisibility(View.VISIBLE);
            mSyncStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSyncStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
//                            if(mSyncStatusView.getVisibility() == View.GONE){
//                                mSyncStatusView.clearAnimation();
//                            }
                        }
                    });

            mSelectCountryView.setVisibility(View.VISIBLE);
            mSelectCountryView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mSelectCountryView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSyncStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSelectCountryView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void isFirstTimeBundledData() {
        boolean isFirstTimeInstall = P2ASharedSystemPreferences.getFirstTimeInstallationFlag(SelectCountryActivity.this);
        /*
            checking this is the first time installation
         */
        if (isFirstTimeInstall) {
            // It's the first time then we set the first time flag to FALSE,
            // and insert bundled data into the database
            loadBundledData();
            P2ASharedSystemPreferences.setFirstTimeInstallationFlag(SelectCountryActivity.this, false);
        } else {
            // it's not the first time then we've nothing to do.
//            Toast.makeText(this, "Data is loaded!", Toast.LENGTH_LONG).show();
        }
    }

    private void InvokeDataServices() {
        boolean isFirstTimeInstall = P2ASharedSystemPreferences.getFirstTimeInstallationFlag(SelectCountryActivity.this);
        /*
            checking this is the first time installation
         */
        if (isFirstTimeInstall) {
            // It's the first time then we set the first time flag to FALSE,
            // and insert data from web services into the database
            insertDataFromServices();
            P2ASharedSystemPreferences.setFirstTimeInstallationFlag(SelectCountryActivity.this, false);
        } else {
            // it's not the first time then we've nothing to do.
//          Toast.makeText(getContext(),"It's not first time!",Toast.LENGTH_LONG).show();
            insertContinueDataFromServices();
        }
    }

    private void insertDataFromServices() {
        startSyncQuestion();
    }

    private void insertContinueDataFromServices() {
        startSyncQuestion();
    }

    private void loadBundledData() {
        final P2AGameDbHelper p2aDb = P2AGameDbHelper.getInstance(P2AContext.getContext());
        if (p2aDb.getDatabaseName() != null) {
            return;
        }
    }

    void startSyncNewQuestions() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).create();
        // Setting Dialog Title
        alertDialog.setTitle("ASEAN Game");

        if (GeneralHelper.hasConnection()) {
            alertDialog.setMessage("Do you want to load new questions from server?");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Invokes web services then load data into the database.
                    InvokeDataServices();
                    alertDialog.cancel();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Invokes inserting bundled data into the database.
                    isFirstTimeBundledData();
                    alertDialog.cancel();
                }
            });
        } else {
            alertDialog.setMessage("No Internet connection!");
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Invokes inserting bundled data into the database.
                    isFirstTimeBundledData();
                    alertDialog.cancel();
                }
            });
        }
        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * This method is used to toggle between the two fragment states by
     * calling the appropriate animations between them. The entry and exit
     * animations of the text fragment are specified in R.animator resource
     * files. The entry and exit animations of the image fragment are
     * specified in the slideBack and slideForward methods below. The reason
     * for separating the animation logic in this way is because the translucent
     * dark hover view must fade in at the same time as the image fragment
     * animates into the background, which would be difficult to time
     * properly given that the setCustomAnimations method can only modify the
     * two fragments in the transaction.
     */
    private void switchFragments() {
        if (mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        if (mDidSlideOut) {
            mDidSlideOut = false;
            getFragmentManager().popBackStack();
        } else {
            mDidSlideOut = true;

            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator arg0) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_fragment_in, 0, 0,
                            R.anim.slide_fragment_out);
                    transaction.add(R.id.select_country_container, mTextFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            };
            slideBack(listener);
        }
    }

    /**
     * This method animates the image fragment into the background by both
     * scaling and rotating the fragment's view, as well as adding a
     * translucent dark hover view to inform the user that it is inactive.
     */
    public void slideBack(Animator.AnimatorListener listener) {
        View movingFragmentView = mImageFragment.getView();

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.8f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.8f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.0f, 0.5f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(getResources().
                getInteger(R.integer.half_slide_up_down_duration));

        final AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, darkHoverViewAnimator, movingFragmentRotator);
        s.addListener(listener);
        s.start();
    }

    /**
     * This method animates the image fragment into the foreground by both
     * scaling and rotating the fragment's view, while also removing the
     * previously added translucent dark hover view. Upon the completion of
     * this animation, the image fragment regains focus since this method is
     * called from the onBackStackChanged method.
     */
    public void slideForward(Animator.AnimatorListener listener) {
        View movingFragmentView = mImageFragment.getView();
        ;

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.5f, 0.0f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(
                getResources().getInteger(R.integer.half_slide_up_down_duration));

        final AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, movingFragmentRotator, darkHoverViewAnimator);
        s.setStartDelay(getResources().getInteger(R.integer.slide_up_down_duration));
        s.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });
        s.start();
    }

    /**
     * Process request loading new questions from API server.
     *
     * @author Hanh.D.TRAN
     * @updated: We want to sync all questions.
     */
    private class SyncQuestionTask extends AsyncTask<Void, Integer, Boolean> {
        // DAO instances
        final QuestionDAO questionDAO = QuestionDAO.getInstance();
        final AnswerDAO answerDAO = AnswerDAO.getInstance();
        private ProgressDialog pd = null;
        private Context ctx;

        public SyncQuestionTask(Context context) {
            ctx = context;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ctx);
            pd.setTitle(getResources().getString(R.string.title_request_question));
            pd.setMessage(getResources().getString(R.string.message_request_question));
            pd.setCancelable(false);
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            final Integer counter = values[0];
            pd.setProgress(counter);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Request GET questions service here.
            return invokeQuestionsRequest();
        }

        private boolean invokeQuestionsRequest(){
            final P2AClientServiceProvider p2aClientServiceProvider =
                    P2AClientServiceProvider.getInstance(SelectCountryActivity.this);

            // Volley request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    p2aClientServiceProvider.getOriginalServicePath() + "questionsHandler.ashx", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    // Parser Questions JSON
                    final List<Question> questions = Question.parserQuestionsJson(jsonObject.toString());
                    if (!questions.isEmpty()) {
                        Toast.makeText(SelectCountryActivity.this, "Get new questions successful!", Toast.LENGTH_LONG).show();
                        // Update dialog's message when is insert data into database.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setMessage(getResources().getString(R.string.message_request_question));
                                pd.setMax(questions.size());
                            }
                        });
                        int counter = 1;
                        // Drop the table then create it again.
                        P2AGameDbHelper.getInstance(P2AContext.getContext()).dropTablesToGetNewQuestions();
                        // Insert questions into the database
                        Question question;
                        Iterator<Question> itQuestion = questions.iterator();
                        while (itQuestion.hasNext()) {
                            question = itQuestion.next();
                            //questionDAO.insertMultipleAnswerOfQuestion(question);
                            questionDAO.insertQuestion(question);
                            insertAnswerOfQuestion(question);
                            publishProgress(counter);
                            ++counter;
                        }
                    } else {
                        Toast.makeText(SelectCountryActivity.this, "No new questions loaded!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(P2AHttpHeaderConstants.QUESTION_ID, "0");
                    return params;
                }
            };

            P2AContext.getInstance().addToRequestQueue(request);

            /** OLD CODE - FOR GOOGLE DEFAULT HTTP CLIENT */
//            final DefaultHttpClient defaultClient = p2aClientServiceProvider.getDefaultClient();
//            final HttpPost httpPost = new HttpPost(p2aClientServiceProvider.getOriginalServicePath() + "questionsHandler.ashx");
//            httpPost.setHeader(P2AHttpHeaderConstants.QUESTION_ID, "0");
//            try {
//                final HttpResponse response = defaultClient.execute(httpPost);
//                P2AClientServiceProvider.setResponsedCode(response.getStatusLine().getStatusCode());
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
//                    if (!json.isEmpty()) {
//                        // Parser Questions JSON
//                        final List<Question> questions = Question.parserQuestionsJson(json);
//                        if (questions.isEmpty()) {
//                            return false;
//                        } else {
//                            // Update dialog's message when is insert data into database.
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.setMessage("In progress...");
//                                    pd.setMax(questions.size());
//                                }
//                            });
//                            int counter = 1;
//                            // Drop the table then create it again.
//                            P2AGameDbHelper.getInstance(P2AContext.getContext()).dropTablesToGetNewQuestions();
//                            // Insert questions into the database
//                            Question question;
//                            Iterator<Question> itQuestion = questions.iterator();
//                            while (itQuestion.hasNext()) {
//                                question = itQuestion.next();
//                                //questionDAO.insertMultipleAnswerOfQuestion(question);
//                                questionDAO.insertQuestion(question);
//                                insertAnswerOfQuestion(question);
//                                publishProgress(counter);
//                                ++counter;
//                            }
//                        }
//                        return true;
//                    } else {
//                        return false;
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//                return false;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
            return true;
        }

        private void insertAnswerOfQuestion(Question question) {
            for (Answer answer : question.getAnswers()) {
                answerDAO.insertAnswer(answer);
            }
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
//            if (aBoolean) {
//                Toast.makeText(SelectCountryActivity.this, "Get new questions successful!", Toast.LENGTH_LONG).show();
//
//            } else {
//                Toast.makeText(SelectCountryActivity.this, "No new questions loaded!", Toast.LENGTH_LONG).show();
//            }
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mSyncQuestionTask = null;
            pd.cancel();
            showSyncProgress(false);
            SelectCountryActivity.this.recreate();
        }

    }

    private class GetTopAllScoreTask extends AsyncTask<Void, Integer, Boolean> {
        ProgressDialog pd = null;
        private Context ctx;
        private List<LeaderBoard> leaders;
        private QuickAction mQuickAction;

        public GetTopAllScoreTask(QuickAction quickAction, Context context) {
            mQuickAction = quickAction;
            ctx = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ctx);
            pd.setTitle("High scores");
            pd.setMessage("Loading in progress...");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMax(10);
            //pd.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            final Integer counter = values[0];
            pd.setProgress(counter);
            final ActionItem item = new ActionItem(counter + 1, leaders.get(counter).getLoggedName()
                    + "\n" + (leaders.get(counter).getNameOfInstitute().length() > 35 ?
                    leaders.get(counter).getNameOfInstitute().substring(0, 33) :
                    leaders.get(counter).getNameOfInstitute()),
                    leaders.get(counter).getTotalScore() + "  ",
                    getResources().getDrawable(R.drawable.ic_medal));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mQuickAction.addActionItem(item);
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Request GET questions service here.
            final P2AClientServiceProvider p2aClientServiceProvider =
                    P2AClientServiceProvider.getInstance(SelectCountryActivity.this);
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return null;
            }
            /* Build Volley request */
            final String url = p2aClientServiceProvider.getOriginalServicePath() + "topScoreAllHandler.ashx";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            // Parser Questions JSON
                            leaders = LeaderBoard.parserLeaderBoardJson(jsonObject.toString());
                            if (!leaders.isEmpty()) {
                                for (int counter = 0; counter < leaders.size(); counter++) {
                                    GeneralHelper.sleepForInSecs(200);
                                    publishProgress(counter);
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    });
            P2AContext.getInstance().addToRequestQueue(request);
            return true;

            /** OLD CODE - FOR GOOGLE DEFAULT HTTP CLIENT */
//            final DefaultHttpClient defaultClient = p2aClientServiceProvider.getDefaultClient();
//            final HttpPost httpPost = new HttpPost(p2aClientServiceProvider.getOriginalServicePath() + "topScoreAllHandler.ashx");
//
//            try {
//                // Simulate network access.
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                return null;
//            }
//            try {
//                final HttpResponse response = defaultClient.execute(httpPost);
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
//                        // Parser Questions JSON
//                        leaders = LeaderBoard.parserLeaderBoardJson(json);
//                        if (leaders.isEmpty()) {
//                            return false;
//                        } else {
//                            for (int counter = 0; counter < leaders.size(); counter++) {
//                                GeneralHelper.sleepForInSecs(200);
//                                publishProgress(counter);
//                            }
//                            return true;
//                        }
//                    } else {
//                        return false;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                }
//
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//                return false;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pd.cancel();
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            onCancelled();
            if (aBoolean) {
                Toast.makeText(SelectCountryActivity.this, "Loading top players successful!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectCountryActivity.this, "Loading top players fail - connection time out!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class CheckNewQuestion extends AsyncTask<Void, Integer, Boolean> {
        // DAO instances
        final QuestionDAO questionDAO = QuestionDAO.getInstance();
        final AnswerDAO answerDAO = AnswerDAO.getInstance();
        int questionInDB;
        int questionOnService;
        private Context ctx;

        public CheckNewQuestion(Context context) {
            ctx = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //check if we have new question
            if (GeneralHelper.hasConnection()) {
                questionInDB = questionDAO.countQuestions();
                questionOnService = 0;

                // Request GET questions service here.
                final P2AClientServiceProvider p2aClientServiceProvider =
                        P2AClientServiceProvider.getInstance(SelectCountryActivity.this);

                final String url = p2aClientServiceProvider.getOriginalServicePath() + "questionsCountHandler.ashx";

                // Building Volley request
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            final JSONArray questionJsonArray = jsonObject.getJSONArray("QuestionsCount");
                            for (int i = 0; i < questionJsonArray.length(); i++) {
                                final JSONObject questionObject = questionJsonArray.getJSONObject(i);
                                questionOnService = questionObject.getInt("question_count");

                                if (questionOnService > questionInDB) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                                    builder1.setMessage("There are new " + (questionOnService - questionInDB) + " question.\n Do you want to update?");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    mSyncQuestionTask = new SyncQuestionTask(SelectCountryActivity.this);
                                                    mSyncQuestionTask.execute();
                                                    dialog.cancel();
                                                }
                                            });
                                    builder1.setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });
            } else {
                return false;
            }

            return true;

            /** OLD APPROACH - GOOGLE DEFAULT HTTP CLIENT */
//                final DefaultHttpClient defaultClient = p2aClientServiceProvider.getDefaultClient();
//                final HttpPost httpPost = new HttpPost(p2aClientServiceProvider.getOriginalServicePath() + "questionsCountHandler.ashx");
//
//                try {
//                    final HttpResponse response = defaultClient.execute(httpPost);
//
//                    P2AClientServiceProvider.setResponsedCode(response.getStatusLine().getStatusCode());
//                    final HttpEntity httpEntity = response.getEntity();
//                    InputStream is;
//                    is = httpEntity.getContent();
//
//                    try {
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(is, "utf-8"), 8);
//                        StringBuilder sb = new StringBuilder();
//                        String line;
//                        String json;
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//                        is.close();
//                        json = sb.toString();
//                        final JSONObject jsonObject = new JSONObject(json);
//                        final JSONArray questionJsonArray = jsonObject.getJSONArray("QuestionsCount");
//                        for (int i = 0; i < questionJsonArray.length(); i++) {
//                            final JSONObject questionObject = questionJsonArray.getJSONObject(i);
//                            questionOnService = questionObject.getInt("question_count");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                    return false;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                }
//
//                return true;
//            } else
//                return false;
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
//            if (aBoolean) {
//                if (questionOnService > questionInDB) {
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
//                    builder1.setMessage("There are new " + (questionOnService - questionInDB) + " question.\n Do you want to update?");
//                    builder1.setCancelable(true);
//                    builder1.setPositiveButton("Yes",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    mSyncQuestionTask = new SyncQuestionTask(SelectCountryActivity.this);
//                                    mSyncQuestionTask.execute();
//                                    dialog.cancel();
//                                }
//                            });
//                    builder1.setNegativeButton("No",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//                    AlertDialog alert11 = builder1.create();
//                    alert11.show();
//                }
//            }
            onCancelled();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            checkNewQuestionTask = null;
        }
    }
}
