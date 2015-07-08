package com.cse.p2a.aseangame.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.p2a.aseangame.P2AContext;
import com.cse.p2a.aseangame.R;
import com.cse.p2a.aseangame.data.adapter.P2AGameDbAdapter;
import com.cse.p2a.aseangame.data.dao.AnswerDAO;
import com.cse.p2a.aseangame.data.dao.SessionDAO;
import com.cse.p2a.aseangame.data.dao.SessionDetailDAO;
import com.cse.p2a.aseangame.data.model.Answer;
import com.cse.p2a.aseangame.data.model.Question;
import com.cse.p2a.aseangame.data.model.Session;
import com.cse.p2a.aseangame.data.model.SessionDetail;
import com.cse.p2a.aseangame.utils.GeneralHelper;
import com.cse.p2a.aseangame.utils.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class PlayGameActivity extends Activity implements OnClickListener {
    public static final String ID_COUNTRY = "ID_COUNTRY";
    // Put session id here if user chose resume game
    public static final String ID_SESSION = "ID_SESSION";
    // image naming and path  to include sd card  appending name you choose for file
    // Some constants
    private final static String SCREENSHOTS_LOCATIONS = Environment.getExternalStorageDirectory().toString() + "/Pictures/";
    private static final String LOG_TAG = PlayGameActivity.class.getCanonicalName();
    private final TextView[] txtAnswer = new TextView[4];
    private final int animationTime = 250;
    private final SessionDAO sessionDAO;
    private final SessionDetailDAO sessionDetailDAO;
    //	public static String SHARED_PREF_NAME = "AppSetting";
    private ArrayList<Question> questions = new ArrayList<Question>();
    private Session session;
    private ArrayList<SessionDetail> sessiondetails;
    private int currentQuestion = 0;
    private int clicked = 0;
    private int currentScore = 0;
    private TextView txtQuestion;
    private TextView txtScore;
    private TextView txtTimer;
    private LinearLayout lnnQuestionContent;
    private Button btnHome;
    private P2AContext appP2AContext;
    private long timer = 0;
    private Intent intent;
    private TextView txtBonusScore;
    private ImageView imgShareOnFacebook;
    private String mQuestionScreenShotName;
    private int bonusCount = 0;
    private long beginQuestion = 0;
    private Thread timerThead;
    private View playGameView;
    private int screenWidth, screenHeight;

    private View.OnTouchListener motionButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int actionMasked = event.getActionMasked();

            switch (actionMasked) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    v.animate().scaleY(.95f).setInterpolator(MainActivity.sAccelerator);
                    v.setPressed(true);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP: {
                    if (v.isPressed()) {
                        v.performClick();
                        v.animate().scaleX(1).scaleY(1).setInterpolator(MainActivity.sAccelerator);
                        v.animate().alpha(1).setInterpolator(MainActivity.sDecelerator);
                        v.setPressed(false);

                    } else {
                        v.animate().scaleX(1).scaleY(1).setInterpolator(MainActivity.sAccelerator);
                        v.animate().alpha(1).setInterpolator(MainActivity.sDecelerator);
                    }
                    break;
                }
            }
            v.invalidate();
            return true;
        }
    };

    /**
     * Constructor
     */
    public PlayGameActivity() {
        sessionDAO = SessionDAO.getInstance();
        sessionDetailDAO = SessionDetailDAO.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        try {
            setContentView(R.layout.activity_play_game);

            playGameView = findViewById(R.id.play_game_frame);

            if (playGameView.getBackground() == null) {
                playGameView.setBackground(new BitmapDrawable(getResources(),
                        ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.play_game_background_final,
                                screenWidth, screenHeight)
                ));
            }
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            // Try to garbage collection first.
            System.gc();
            try {
                setContentView(R.layout.activity_play_game);

                playGameView = findViewById(R.id.play_game_frame);

                if (playGameView.getBackground() == null) {
                    playGameView.setBackground(new BitmapDrawable(getResources(),
                            ImageUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.play_game_background_final,
                                    screenWidth, screenHeight)
                    ));
                }
            } catch (OutOfMemoryError oome2) {
                oome2.printStackTrace();
                // TODO Give me a solution!
            }
        }
        intent = getIntent();
        appP2AContext = (P2AContext) getApplicationContext();

        loadInterfaceObject();

        if (intent.getIntExtra(ID_SESSION, 0) > 0) {

            loadOldSession();
        } else {
            loadRandomQuestions(intent.getIntExtra(ID_COUNTRY, 1));
            newSession();
        }

        setContent();

        setTimerThead(new Thread() {

            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.currentThread().sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer = timer + 1;
                                updateTimerText();
                                if (timer - beginQuestion == 30) {
                                    //                                    suggestShareQuestion();
                                }
                            }
                        });
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        getTimerThead().start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadImageButtonAnimations(imgShareOnFacebook, R.animator.activity_finish_fadein);
    }

    private void playWrongSound() {
        P2AContext appContext = (P2AContext) getApplicationContext();
        if (appContext.isSoundOn()) {
            SoundPool soundPool = appContext.getSoundPool();
            int wrongAudio = appContext.getWrongAudio();
            this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.play(wrongAudio, volume, volume, 1, 0, 1f);
        }
    }

    private void playRightSound() {
        P2AContext appContext = (P2AContext) getApplicationContext();
        if (appContext.isSoundOn()) {
            SoundPool soundPool = appContext.getSoundPool();
            int rightAudio = appContext.getRightAudio();
            this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.play(rightAudio, volume, volume, 1, 0, 1f);
        }
    }

    /**
     * Load new session.
     */
    private void newSession() {
        // Get current user id - Anonymous is default 1.
        final P2AContext appContext = (P2AContext) getApplicationContext();
        Calendar dateTimeCreated = new GregorianCalendar();
        dateTimeCreated.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        session = new Session();
        session.set_session_country_id(intent.getIntExtra(ID_COUNTRY, 1));
        session.set_session_begin_date(dateTimeCreated.getTimeInMillis());
        session.set_session_finish(0);
        if (appContext.getcUser() != null) {
            session.set_session_user_id(appContext.getcUser().get_user_id());
            session.set_session_token(appContext.getcUser().get_user_token());
        } else {
            session.set_session_user_id(1); // assign to Anonymous
        }

        long session_id = sessionDAO.insertSession(session);
        session.set_session_id((int) session_id);
        sessiondetails = new ArrayList<SessionDetail>();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            List<Answer> answers = AnswerDAO.getInstance().getAnswersOfQuestion(question.get_question_id());

            if (answers.size() > 0) {
                questions.get(i).setAnswers(answers);
            } else {
                // Check if answers is null or empty for this question

            }
            SessionDetail sessiondetail = new SessionDetail();
            sessiondetail.set_session_detail_create_date(dateTimeCreated.getTimeInMillis());
            sessiondetail.set_session_detail_session_id((int) session_id);
            sessiondetail.set_session_detail_question_id(question.get_question_id());
            sessiondetail.set_session_detail_order_number(i);
            long sessiondetail_id = sessionDetailDAO.insertSessionDetail(sessiondetail);
            sessiondetail.set_session_detail_id((int) sessiondetail_id);
            sessiondetails.add(sessiondetail);
        }
    }

    /**
     * Load old session that played in previous turn.
     */
    private void loadOldSession() {
        int tmp = 0;
        // First of all, we'll find the first session that is uncompleted.
        session = P2AGameDbAdapter.getSession(intent.getIntExtra(ID_SESSION, 0));
        final P2AContext appContext = (P2AContext) getApplicationContext();
        if (appContext.getcUser() != null) {
            session.set_session_token(appContext.getcUser().get_user_token());
        }
        currentScore = (int) session.get_session_total_score();
        txtScore.setText(currentScore + "");
        timer = session.get_session_spend_time();
        updateTimerText();
        sessiondetails = P2AGameDbAdapter.getSessionDetails(session.get_session_id());
        for (SessionDetail sessionDetail : sessiondetails) {
            Question q;
            q = P2AGameDbAdapter.getQuestion(sessionDetail.get_session_detail_question_id());
            List<Answer> answers = AnswerDAO.getInstance().getAnswersOfQuestion(q.get_question_id());
            q.setAnswers(answers);
            questions.add(q);
            if (sessionDetail.get_session_detail_answer_id_1() == 0
                    && sessionDetail.get_session_detail_answer_id_2() == 0
                    && sessionDetail.get_session_detail_answer_id_3() == 0
                    && sessionDetail.get_session_detail_answer_id_4() == 0
                    && tmp == 0) {
                currentQuestion = (sessionDetail.get_session_detail_order_number() - 1) < 0 ?
                        0 : sessionDetail.get_session_detail_order_number();
                ++tmp;
            }
        }
        if (session.get_session_num_lastest_correct() > 0) {
            bonusCount = session.get_session_num_lastest_correct();
        } else {
            bonusCount = 0;
        }
    }

    /**
     *
     */
    private void loadInterfaceObject() {

        btnHome = (Button) findViewById(R.id.play_game_home_btn);
        btnHome.setOnClickListener(this);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        txtQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        txtScore = (TextView) findViewById(R.id.txtScore);

        txtTimer = (TextView) findViewById(R.id.txtTimer);
        txtAnswer[0] = (TextView) findViewById(R.id.txtAnswerA);
        txtAnswer[0].setEllipsize(TextUtils.TruncateAt.START);
        txtAnswer[0].setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        txtAnswer[0].setOnTouchListener(motionButtonListener);
        txtAnswer[0].animate().setDuration(100);

        txtAnswer[1] = (TextView) findViewById(R.id.txtAnswerB);
        txtAnswer[1].setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        txtAnswer[1].setEllipsize(TextUtils.TruncateAt.START);
        txtAnswer[1].setOnTouchListener(motionButtonListener);
        txtAnswer[1].animate().setDuration(100);

        txtAnswer[2] = (TextView) findViewById(R.id.txtAnswerC);
        txtAnswer[2].setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        txtAnswer[2].setEllipsize(TextUtils.TruncateAt.START);
        txtAnswer[2].setOnTouchListener(motionButtonListener);
        txtAnswer[2].animate().setDuration(100);

        txtAnswer[3] = (TextView) findViewById(R.id.txtAnswerD);
        txtAnswer[3].setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        txtAnswer[3].setEllipsize(TextUtils.TruncateAt.START);
        txtAnswer[3].setOnTouchListener(motionButtonListener);
        txtAnswer[3].animate().setDuration(100);

        lnnQuestionContent = (LinearLayout) findViewById(R.id.lnnQuestionContent);

        TextView txtCountryName = (TextView) findViewById(R.id.play_game_country_name_txt);
        txtCountryName.setText(appP2AContext.getSelectedCountry().get_country_name());

        txtBonusScore = (TextView) findViewById(R.id.play_bonus_score_txt);
        // Load the timer ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView imgTimer = (ImageView) findViewById(R.id.play_timer_image);
        imgTimer.setBackgroundResource(R.drawable.spin_animation);
        // Get the background, which has been compiled to an AnimationDrawable object.
        final AnimationDrawable frameAnimation = (AnimationDrawable) imgTimer.getBackground();
        // Start the animation (looped playback by default).
        frameAnimation.start();

        imgShareOnFacebook = (ImageView) findViewById(R.id.play_game_share_on_facebook_btn);
        imgShareOnFacebook.setOnClickListener(this);

    }

    private void loadRandomQuestions(int idCountry) {
        // Log.d(SelectCountryActivity.class.getCanonicalName(),"Country id loaded: " + idCountry);
        questions = new ArrayList<Question>();
        questions = P2AGameDbAdapter.getRandomQuestion(idCountry);
        // Log.d(SelectCountryActivity.class.getCanonicalName(),"Length of Question loaded: " + questions.size());
        // To shuffle question list
//        Collections.shuffle(questions);
    }

    private void setContent() {
        beginQuestion = timer;
        clicked = 0;
        resetText();
        final Activity curActivity = this;
        Question question = questions.get(currentQuestion);
        txtScore.setText(currentScore + "");
        txtQuestion.setText((currentQuestion + 1) + ". " + question.get_question_content());
        final ArrayList<Answer> answers = P2AGameDbAdapter.getAnswer(question.get_question_id());
        // Shuffle answer list
//        Collections.shuffle(answers);
        final int[] points = P2AGameDbAdapter.getPoint(question.get_level_id());
        // Checking previous turn chose correct answer or not
        if ((sessiondetails.get(currentQuestion).get_session_detail_answer_id_1() == 1 && answers.get(0).get_is_true())
                || (sessiondetails.get(currentQuestion).get_session_detail_answer_id_2() == 1 && answers.get(1).get_is_true())
                || (sessiondetails.get(currentQuestion).get_session_detail_answer_id_3() == 1 && answers.get(2).get_is_true())
                || (sessiondetails.get(currentQuestion).get_session_detail_answer_id_4() == 1 && answers.get(3).get_is_true())) {
            // The previous turn is chosen the correct answer.
            currentQuestion++;
            if (currentQuestion < 30) {
                try {
                    setContent();
                } catch (Exception e) {
                    e.printStackTrace();
                    sessionDetailDAO.deleteSessionDetail(session.get_session_id());
                    Calendar dateTimeCreated = new GregorianCalendar();
                    dateTimeCreated.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                    session.set_session_end_date(dateTimeCreated.getTimeInMillis());
                    session.set_session_finish(1);
                    saveCurrentSession(session);
                }

            } else {
                sessionDetailDAO.deleteSessionDetail(session.get_session_id());
                Calendar dateTimeCreated = new GregorianCalendar();
                dateTimeCreated.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                session.set_session_end_date(dateTimeCreated.getTimeInMillis());
                session.set_session_finish(1);
                saveCurrentSession(session);
                // chuyen qua intent finish
                finishGame(session.get_session_id());
                curActivity.finish();
            }
        } else {
            // Resume with the previous turn haven't chosen the correct answer.
            // Re-draw sentences chose at previous play turn
            if (sessiondetails.get(currentQuestion).get_session_detail_answer_id_1() == 1) {
                txtAnswer[0].setEnabled(false);
                txtAnswer[0].setBackground(getResources().getDrawable(R.drawable.spantext_berry));
                clicked++;
            }
            if (sessiondetails.get(currentQuestion).get_session_detail_answer_id_2() == 1) {
                txtAnswer[1].setEnabled(false);
                txtAnswer[1].setBackground(getResources().getDrawable(R.drawable.spantext_berry));
                clicked++;
            }
            if (sessiondetails.get(currentQuestion).get_session_detail_answer_id_3() == 1) {
                txtAnswer[2].setEnabled(false);
                txtAnswer[2].setBackground(getResources().getDrawable(R.drawable.spantext_berry));
                clicked++;
            }
            if (sessiondetails.get(currentQuestion).get_session_detail_answer_id_4() == 1) {
                txtAnswer[3].setEnabled(false);
                txtAnswer[3].setBackground(getResources().getDrawable(R.drawable.spantext_berry));
                clicked++;
            }

        }

        for (int i = 0; i < 4; i++) {
            txtAnswer[i].setText((i == 0 ? "A. " : i == 1 ? "B. " : i == 2 ? "C. " : "D. ") + answers.get(i).get_answer_content());
            final int currentAnswer = i;

            if (answers.get(i).get_is_true()) { // On the corrected answer
                txtAnswer[i].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        for (int j = 0; j < 4; j++) {
                            txtAnswer[j].setEnabled(false);
                        }
                        // Refine background for the correct answer
                        TextView tv = (TextView) arg0;
                        tv.setEnabled(false);
                        tv.setBackground(getResources().getDrawable(R.drawable.spantext_sky));

                        ++bonusCount;
                        int bonusScore = points[clicked];
                        if (bonusCount == 4 && clicked == 0) {
                            bonusScore = points[clicked] * 2;
                            txtBonusScore.setText("+" + points[clicked] + " x 2");
                            bonusCount = 1;
                        } else if (clicked != 0) {
                            txtBonusScore.setText("+" + points[clicked]);
                            bonusCount = 0;
                        } else {
                            txtBonusScore.setText("+" + points[clicked]);
                        }
                        clicked++;
                        final Animation bounce = AnimationUtils.loadAnimation(PlayGameActivity.this, R.animator.bounce);
                        txtBonusScore.startAnimation(bounce);
                        currentScore = currentScore + bonusScore;
                        // Set question point flag.
                        sessiondetails.get(currentQuestion).set_session_detail_score(bonusScore);

                        // Flag sentences answered.
                        switch (currentAnswer) {
                            case 0:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_1(1);
                                break;
                            case 1:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_2(1);
                                break;
                            case 2:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_3(1);
                                break;
                            case 3:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_4(1);
                                break;
                        }

                        sessionDetailDAO.updateSessionDetail(sessiondetails.get(currentQuestion));
                        // Play right audio when chose correct answer
                        playRightSound();

                        currentQuestion++;
                        final DisplayMetrics metrics = new DisplayMetrics();
                        Animation animation2;
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        SharedPreferences sharedPref = curActivity.getPreferences(Context.MODE_PRIVATE);
                        int continiousRightAnswer = sharedPref.getInt(getString(R.string.continious_Right_Answer), 0);
                        continiousRightAnswer++;
                        if (continiousRightAnswer == 100) {
                            continiousRightAnswer = 0;
                            //chuyen qua intent 100 cau dung;
                        }
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.continious_Right_Answer), continiousRightAnswer);
                        editor.commit();
                        // Set 30 questions for finishing session now.
                        if (currentQuestion < 30) {
                            animation2 = new TranslateAnimation(0, -metrics.widthPixels, 0, 0);
                            animation2.setDuration(animationTime);
                            lnnQuestionContent.startAnimation(animation2);
                            animation2 = null;
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    setContent();
                                }
                            }, animationTime);
                        } else {
                            sessionDetailDAO.deleteSessionDetail(session.get_session_id());
                            final Calendar dateTimeCreated = new GregorianCalendar();
                            dateTimeCreated.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                            session.set_session_end_date(dateTimeCreated.getTimeInMillis());
                            session.set_session_finish(1);
                            saveCurrentSession(session);
                            // chuyen qua intent finish
                            finishGame(session.get_session_id());
                        }


                    }
                });

            } else {
                // Clicked on wrong answer
                txtAnswer[i].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // Refine background for the correct answer
                        TextView tv = (TextView) arg0;
                        tv.setEnabled(false);
                        tv.setBackground(getResources().getDrawable(R.drawable.spantext_berry));
                        // Play wrong audio
                        playWrongSound();

                        // Flag sentences answered
                        bonusCount = 0;
                        clicked++;
                        switch (currentAnswer) {
                            case 0:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_1(1);
                                break;
                            case 1:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_2(1);
                                break;
                            case 2:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_3(1);
                                break;
                            case 3:
                                sessiondetails.get(currentQuestion).set_session_detail_answer_id_4(1);
                                break;
                        }
                        sessiondetails.get(currentQuestion).set_session_detail_score(points[clicked]);
                        sessionDetailDAO.updateSessionDetail(sessiondetails.get(currentQuestion));
                        SharedPreferences sharedPref = curActivity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.continious_Right_Answer), 0);
                        editor.commit();

                        // Forward to new question when gave three wrong answers continuously
                        if (clicked == 3) {
                            // Locking right answer first
                            for (TextView view : txtAnswer) {
                                if (view.isEnabled())
                                    view.setEnabled(false);
                            }
                            // Go to next question
                            currentQuestion++;
                            final DisplayMetrics metrics = new DisplayMetrics();
                            final Animation animation2;
                            // Set 30 questions for finishing session now.
                            if (currentQuestion < 30) {
                                animation2 = new TranslateAnimation(0, -metrics.widthPixels, 0, 0);
                                animation2.setDuration(animationTime);
                                lnnQuestionContent.startAnimation(animation2);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        setContent();
                                    }
                                }, animationTime);
                            } else {
                                sessionDetailDAO.deleteSessionDetail(session.get_session_id());
                                final Calendar dateTimeCreated = new GregorianCalendar();
                                dateTimeCreated.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                                session.set_session_end_date(dateTimeCreated.getTimeInMillis());
                                session.set_session_finish(1);
                                saveCurrentSession(session);
                                // chuyen qua intent finish
                                finishGame(session.get_session_id());
                            }
                        }

                    }
                });
            }
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Animation animation;
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        animation = new TranslateAnimation(metrics.widthPixels, 0, 0, 0);
        animation.setDuration(animationTime);
        lnnQuestionContent.setClickable(true);
        lnnQuestionContent.startAnimation(animation);
        animation = null;
    }

    /**
     * Forward to the finish game screen
     *
     * @param sessionId ID of current session
     */
    private void finishGame(int sessionId) {
        try {
            final Intent finishGameIntent = new Intent(this, FinishGameActivity.class);
            final Bundle translateBundle = ActivityOptions.makeCustomAnimation(
                    PlayGameActivity.this, R.animator.slide_in_left,
                    R.animator.slide_out_left).toBundle();
            finishGameIntent.putExtra(FinishGameActivity.FINISHED_SESSION_ID_FLAG, sessionId);
            getTimerThead().currentThread().interrupt();
            startActivity(finishGameIntent, translateBundle);
            finish();
            // Release memory for UI hidden
            onTrimMemory(TRIM_MEMORY_COMPLETE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetText() {
        for (int i = 0; i < 4; i++) {
            txtAnswer[i].setTextColor(Color.WHITE);
            txtAnswer[i].setBackground(getResources().getDrawable(R.drawable.spantext_purple));
            txtAnswer[i].setEnabled(false);
        }
        // Sleep 3 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 4; i++) {
                    txtAnswer[i].setEnabled(true);
                }
            }
        }, animationTime * 2);
    }

//    private void suggestShareQuestion() {
//        final AlertDialog suggestAlert = new AlertDialog.Builder(PlayGameActivity.this).create();
//        suggestAlert.setIcon(android.R.drawable.ic_dialog_info);
//        suggestAlert.setTitle("Share this question");
//        suggestAlert.setMessage("Do you want to tell someone help answer this question?");
//        suggestAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                suggestAlert.dismiss();
//            }
//        });
//        suggestAlert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                invokeSharingIntentOnFacebook();
//            }
//        });
//
//        suggestAlert.show();
//    }

    private void updateTimerText() {
        String text = "";
        long h, m, s;
        h = timer / 3600;
        m = timer / 60 - h * 60;
        s = timer - h * 3600 - m * 60;
        text = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
        txtTimer.setText(text);

    }

    private void saveCurrentSession(Session saveSession) {
        saveSession.set_session_spend_time(timer);
        saveSession.set_session_total_score(currentScore);
        saveSession.set_session_num_lastest_correct(bonusCount);
        sessionDAO.updateSession(saveSession);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == btnHome.getId()) {
            // Pretended that Back pressed!
            onBackPressed();
        } else if (v.getId() == imgShareOnFacebook.getId()) {
            v.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    // Share on Facebook button is pressed
                    // Setup pressed animation
                    loadImageButtonAnimations(v, R.animator.fadein);
                    // Check internet connection
                    if (GeneralHelper.hasConnection()) {
                        // Sharing the question on social networks.
                        shareQuestion(getResources().getString(R.string.action_share_on_facebook));
                    } else {
                        Toast.makeText(PlayGameActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void shareQuestion(String typeOfShare) {
        final String fbType = getResources().getString(R.string.action_share_on_facebook);
        if (typeOfShare.equals(fbType)) {
            // Share total score on Facebook social network
            invokeSharingIntentOnFacebook();
        }
    }

    /**
     * Share total score on Facebook social network
     */
    private void invokeSharingIntentOnFacebook() {
        // Take a screen shot
        takeScreenShot(imgShareOnFacebook);
        final File screenShotfile = new File(SCREENSHOTS_LOCATIONS, mQuestionScreenShotName);
        final Uri screenShotUri = Uri.fromFile(screenShotfile);
//        final Uri screenShotUri = Uri.parse(SCREENSHOTS_LOCATIONS + mQuestionScreenShotName);
        final String shareSubject = "I'm playing P2A ASEAN Game! http://www.p2a.asia";
        String shareBody = "http://www.p2a.asia";
        if (txtQuestion.getText() != null) {
            shareBody = "http://www.p2a.asia/Help me answer the question: " + txtQuestion.getText().toString();
        }
        final Intent sharingQuestionIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingQuestionIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        sharingQuestionIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingQuestionIntent.setType("image/*");
        sharingQuestionIntent.putExtra(Intent.EXTRA_STREAM, screenShotUri);
        sharingQuestionIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingQuestionIntent, "Share your question via"));
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            // Release memory when user interface becomes hidden
            // Log.w(LOG_TAG, "Should release some resource is unnecessary for resuming.");
            getTimerThead().interrupt();
        }
    }

    /**
     * @param view
     * @param animationResource
     */
    private void loadImageButtonAnimations(final View view, final int animationResource) {
        final Animation anim = AnimationUtils.loadAnimation(
                P2AContext.getContext(), animationResource);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        final LayoutInflater li = LayoutInflater.from(PlayGameActivity.this);
        final View promptsView = li.inflate(R.layout.popup_exit_confirm, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayGameActivity.this);
        alertDialogBuilder.setView(promptsView);
        final Button btnYes = (Button) promptsView.findViewById(R.id.btnYes);
        final Button btnNo = (Button) promptsView.findViewById(R.id.btnNo);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        btnYes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getTimerThead().currentThread().interrupt(); // TODO catch exception
                alertDialog.dismiss();
                saveCurrentSession(session);
                PlayGameActivity.this.finish();
                onTrimMemory(TRIM_MEMORY_COMPLETE);
                // Applied window animation for back action
                overridePendingTransition(R.animator.slide_in_right,
                        R.animator.slide_out_right);
            }
        });
        btnNo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * Capturing the recent question screen
     */
    private void takeScreenShot(final View v) {
        // Get device dimensions
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

        // Save the screen shot to the file system
        FileOutputStream fos;
        try {

            mQuestionScreenShotName = "question_capture"
                    + System.currentTimeMillis() + ".jpg";

            String mQuestionImagePath = SCREENSHOTS_LOCATIONS + mQuestionScreenShotName;

            fos = new FileOutputStream(mQuestionImagePath);

            if (fos != null) {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)) {
                    return;
                }
                fos.flush();
                fos.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized Thread getTimerThead() {
        return timerThead;
    }

    public synchronized void setTimerThead(Thread timerThead) {
        this.timerThead = timerThead;
    }
}
