package com.tru.brainly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    Button optionOne, optionTwo, optionThree, optionFour, optionFive, optionSix;
    TextView timerTextView, scoreTextView, questionTextView, brainlyText, finalScoresString;
    ConstraintLayout overlayScreen, mainScreen;
    Button startButton;
    Game game;
    SharedPreferences sharedPreferences;
    ImageView settingsImageView;
    boolean animatedOnStart = false;
    AlertDialog alertDialog;

    public void startGame(View v) {
        //  overlayScreen.setVisibility(View.INVISIBLE);
        // animations, find a better place to place these
        optionOne.setAlpha(1f);
        optionTwo.setAlpha(1f);
        optionThree.setAlpha(1f);
        optionFour.setAlpha(1f);
        scoreTextView.animate().setDuration(500).alpha(1f);
        timerTextView.animate().setDuration(500).alpha(1f);
        questionTextView.animate().setDuration(500).alpha(1f);
        // button showing animations
        optionOne.setTranslationY(100f);
        optionTwo.setTranslationY(400f);
        optionThree.setTranslationY(600f);
        optionFour.setTranslationY(800f);
        Animations.slideUpAnim(optionOne, 50f, 0.8f);
        Animations.slideUpAnim(optionTwo, 100f, 0.8f);
        Animations.slideUpAnim(optionThree, 150f, 0.8f);
        Animations.slideUpAnim(optionFour, 200f, 0.8f);

        if(Game.numberOfOptions == 6) {
            optionFive.setVisibility(View.VISIBLE);
            optionSix.setVisibility(View.VISIBLE);
            optionFive.setTranslationY(600f);
            optionSix.setTranslationY(800f);
            optionFive.setAlpha(1f);
            optionSix.setAlpha(1f);
            Animations.slideUpAnim(optionFive, 250f, 0.8f);
            Animations.slideUpAnim(optionSix, 300f, 0.8f);
        } else {
            optionFive.setVisibility(View.GONE);
            optionSix.setVisibility(View.GONE);
        }

        Animations.slideDownAnim((View) overlayScreen);
//        overlayScreen.animate().setDuration(500).alpha(0f);
        game.startGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlayScreen = findViewById(R.id.overlayScreen);
        mainScreen = findViewById(R.id.mainScreen);

        LinearLayout logoGroup = findViewById(R.id.logoGroup);
        settingsImageView = findViewById(R.id.settingsIcon);

        optionOne = findViewById(R.id.optionOneButton);
        optionTwo = findViewById(R.id.optionTwoButton);
        optionThree = findViewById(R.id.optionThreeButton);
        optionFour = findViewById(R.id.optionFourButton);
        optionFive = findViewById(R.id.optionFiveButton);
        optionSix = findViewById(R.id.optionSixButton);
        Button[] optionButtons = {optionOne, optionTwo, optionThree, optionFour, optionFive, optionSix};
        startButton = findViewById(R.id.startButton);
        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        questionTextView = findViewById(R.id.questionTextView);
        brainlyText = findViewById(R.id.brainlyText);
        finalScoresString = findViewById(R.id.finalScoresString);
        TextView[] textViews = {timerTextView, scoreTextView, questionTextView, brainlyText, finalScoresString};

        ConstraintLayout[] gameScreens = {overlayScreen, mainScreen};

        // end game alert dialog
        TextView alertTitle = new TextView(this);
        alertTitle.setText("brainly");
        alertTitle.setTextSize(22f);
        alertTitle.setTextColor(Color.parseColor("#3e3e3e"));
        alertTitle.setPadding(60, 40, 60, 30);
        alertTitle.setBackgroundResource(R.drawable.alert_title_background);
        alertDialog = new AlertDialog.Builder(this, R.style.GameEndAlertDialog)
                .setCustomTitle(alertTitle)
                .setMessage("Do you want to end the game ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.endGame();
                    }
                })
                .setNegativeButton("No", null)
                .create();

        game = new Game(optionButtons, textViews, gameScreens, startButton, alertDialog);

        loadSettings();

        scoreTextView.setAlpha(0f);
        timerTextView.setAlpha(0f);
        questionTextView.setAlpha(0f);

        // animations
        if(!animatedOnStart) {
            logoGroup.setScaleX(0f);
            logoGroup.setScaleY(0f);
            Animations.scaleUp(logoGroup, 50f, 0.7f);
            startButton.setScaleX(0f);
            startButton.setScaleY(0f);
            startButton.setTranslationY(300f);
            Animations.slideUpAnim(startButton);
            Animations.scaleUp(startButton, 50f, 0.7f);
        }
        animatedOnStart = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handling android back button
        if(resultCode == Activity.RESULT_CANCELED) return;
        game.minNumber = data.getIntExtra("minNumber", game.minNumber);
        game.maxNumber = data.getIntExtra("maxNumber", game.maxNumber);
        game.gameMaxTime = data.getLongExtra("gameMaxTime", game.gameMaxTime);
        game.currentPreset = data.getIntExtra("currentPreset", game.currentPreset);
        saveSettings();
        refreshOptions();
    }

    public void saveSettings() {
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putInt("minNumber", game.minNumber)
                .putInt("maxNumber", game.maxNumber)
                .putLong("gameMaxTime", game.gameMaxTime)
                .putInt("currentPreset", game.currentPreset)
                .apply();
    }

    public void loadSettings() {
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        game.minNumber = sharedPreferences.getInt("minNumber", 1);
        game.maxNumber = sharedPreferences.getInt("maxNumber", 20);
        game.gameMaxTime = sharedPreferences.getLong("gameMaxTime", 30000L);
        game.currentPreset = sharedPreferences.getInt("currentPreset", 1);
        refreshOptions();
    }

    public void refreshOptions() {
        // number of options based on preset
        if(game.currentPreset == 3) Game.numberOfOptions = 6;
        else Game.numberOfOptions = 4;
    }

    public void openSettingsActivity(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingsIntent, 1);
    }

    public void showEndGameDialog() {
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && game.gameRunning) showEndGameDialog();
        return super.onKeyDown(keyCode, event);
    }
}

class Game {
    int minNumber = 1;
    int maxNumber = 20;
    int currentScore = 0;
    int numberOfQuestionsAsked = 0;
    boolean gameRunning = false;
    long gameMaxTime = 30000L;
    int currentPreset = 1;
    static int numberOfOptions = 4;

    Button optionOne, optionTwo, optionThree, optionFour, optionFive, optionSix;
    Button[] optionButtons;
    TextView timerTextView, scoreTextView, questionTextView, brainlyText, finalScoresString;
    ConstraintLayout overlayScreen, mainScreen;
    Button startButton;
    CountDownTimer gameCountDownTimer;
    AlertDialog gameEndDialog;

    @SuppressLint("ClickableViewAccessibility")
    Game(Button[] optionButtons, TextView[] textViews, ConstraintLayout[] gameScreens, Button startButton, AlertDialog alertDialog) {
        optionOne = optionButtons[0];
        optionTwo = optionButtons[1];
        optionThree = optionButtons[2];
        optionFour = optionButtons[3];
        optionFive = optionButtons[4];
        optionSix = optionButtons[5];
        this.optionButtons = optionButtons;
        this.startButton = startButton;
        this.gameEndDialog = alertDialog;

        for(Button btn : optionButtons) {
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        chooseOption(v);
                    }
                    return false;
                }
            });
        }

        timerTextView = textViews[0];
        scoreTextView = textViews[1];
        questionTextView = textViews[2];
        brainlyText = textViews[3];
        finalScoresString = textViews[4];

        this.overlayScreen = gameScreens[0];
        this.mainScreen = gameScreens[1];
    }

    public void startGame() {
        Log.i("XD", "GAME_STARTED");
        gameRunning = true;
        gameCountDownTimer = startCountDown();
        nextQuestion();
    }

    public void endGame() {
        Log.i("XD", "GAME_ENDED");
        finalScoresString.setVisibility(View.VISIBLE);
        finalScoresString.setText(getColoredString());
        currentScore = 0;
        numberOfQuestionsAsked = 0;
        gameRunning = false;
        // animations
        Animations.slideUpAnim(overlayScreen);
        // overlayScreen.animate().setDuration(500).alpha(1f);
        scoreTextView.animate().setDuration(300).alpha(0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                scoreTextView.setText(String.format(Locale.ENGLISH, "%d / %d", currentScore, numberOfQuestionsAsked));
            }
        });
        timerTextView.animate().setDuration(300).alpha(0f);
        questionTextView.animate().setDuration(300).alpha(0f);
        for(Button optionButton : optionButtons) {
            optionButton.animate().setDuration(300).alpha(0);
        }

        try {
            gameCountDownTimer.cancel();
            gameEndDialog.dismiss();
        } catch(Exception e) { e.printStackTrace(); }

        startButton.setText("Restart");
    }

    public void nextQuestion() {
        Log.i("XD", "NEXT_QUESTION");
        Question question = Question.generateChallenge(minNumber, maxNumber);
        questionTextView.setText(String.format(Locale.ENGLISH, "%s = ?", question.equation));
        ArrayList<Integer> options = question.options;
        for(int i = 0; i < numberOfOptions; i++) {
            optionButtons[i].setText(String.format(Locale.ENGLISH, "%d", options.get(i)));
            if(options.get(i) == question.answer)
                optionButtons[i].setTag(true);
            else
                optionButtons[i].setTag(false);
        }
    }

    public void chooseOption(View v) {
        if(!gameRunning) return;
        boolean wasCorrect = Boolean.parseBoolean(v.getTag().toString());
        Log.i("XD", "OPTION: " + wasCorrect);
        if(wasCorrect) {
            Animations.scaleUpDown(v);
            currentScore += 1;
        } else {
            Animations.scaleDownUp(v);
        }
        numberOfQuestionsAsked += 1;
        scoreTextView.setText(String.format(Locale.ENGLISH, "%d / %d", currentScore, numberOfQuestionsAsked));
        nextQuestion();
    }

    public CountDownTimer startCountDown() {
        final CountDownTimer countDownTimer = new CountDownTimer(gameMaxTime + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format("%02ds", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                endGame();
            }
        }.start();
        return countDownTimer;
    }

    public CharSequence getColoredString() {
        BackgroundColorSpan percentStyle  = new BackgroundColorSpan(Color.parseColor("#84fab0"));
        BackgroundColorSpan scoreStyle = new BackgroundColorSpan(Color.parseColor("#8fd3f4"));
        BackgroundColorSpan questionsStyle = new BackgroundColorSpan(Color.parseColor("#8fd3f4"));

        float percentage = (float) currentScore / (float) numberOfQuestionsAsked * 100f;
        if(currentScore == 0) percentage = 0;
        SpannableString spanStringPercentage = new SpannableString(String.format(Locale.ENGLISH, "\u00A0%.2f%%\u00A0", percentage));
        spanStringPercentage.setSpan(percentStyle, 0, spanStringPercentage.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString spanStringScore = new SpannableString(String.format(Locale.ENGLISH, "\u00A0%d\u00A0", currentScore));
        spanStringScore.setSpan(scoreStyle, 0, spanStringScore.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString spanStringQuestions = new SpannableString(String.format(Locale.ENGLISH, "\u00A0%d\u00A0", numberOfQuestionsAsked));
        spanStringQuestions.setSpan(questionsStyle, 0, spanStringQuestions.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return TextUtils.concat(spanStringPercentage, " of your answers were correct, you managed to answer ", spanStringScore, " out of ", spanStringQuestions, " questions correctly ^_^");
    }
}

class Question {
    String equation;
    int answer;
    ArrayList<Integer> options;

    Question(String equation, int answer, ArrayList<Integer> options) {
        this.equation = equation;
        this.answer = answer;
        this.options = options;
    }

    public static Question generateChallenge(int min, int max) {
        /// restructure return
        Random random = new Random();
        int firstNumber = random.nextInt(max - min + 1) + min;
        int secondNumber = random.nextInt(max - min + 1) + min;
        String equation = "1 + 1";
        int result = 2;
        int symbol = random.nextInt(4);
        switch(symbol) {
            case 0:
                equation = String.format("%d + %d", firstNumber, secondNumber);
                result = firstNumber + secondNumber;
                break;
            case 1:
                equation = String.format("%d - %d", firstNumber, secondNumber);
                result = firstNumber - secondNumber;
                break;
            case 2:
                equation = String.format("%d × %d", firstNumber, secondNumber);
                result = firstNumber * secondNumber;
                break;
            case 3:
                firstNumber = firstNumber - (firstNumber % secondNumber);
                if(firstNumber == 0) {
                    firstNumber = secondNumber * (random.nextInt(8) + 2);
                    Log.i("XD", "JEERO");
                }
                    equation = String.format("%d ÷ %d", firstNumber, secondNumber);
                result = firstNumber / secondNumber;
                break;
        }
        return new Question(equation, result, generateOptions(result + 20, Game.numberOfOptions, result));
    }

    private static ArrayList<Integer> generateOptions(int maxNumber, int numOfOptions, int correctOption) {
        maxNumber = Math.abs(maxNumber);
        ArrayList<Integer> options = new ArrayList<>();
        if (maxNumber <= 6)
            maxNumber = 10;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<maxNumber; i++) {
            // correct option won't be repeated
            if(i == correctOption) list.add(maxNumber);
            else list.add(i);
        }
        Collections.shuffle(list);
        for (int i=0; i < numOfOptions - 1; i++) {
            int option = list.get(i);;
            // probability of showing -ve options if answer is negative
            if((new Random().nextBoolean()) && correctOption < 0 && correctOption != (-1 * option)) option *= -1;
            options.add(option);
        }
        options.add(correctOption);
        Collections.shuffle(options);
        return options;
    }
}

class Animations {
    public static void slideDownAnim(View v) {
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        SpringAnimation slideDownY = new SpringAnimation(v, DynamicAnimation.TRANSLATION_Y, screenHeight);
        slideDownY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW).setDampingRatio(0.9f);
        slideDownY.start();
    }

    public static void slideUpAnim(View v) {
        slideUpAnim(v, 80f, 0.8f);
    }

    public static void slideUpAnim(View v, float stiffness, float bounciness) {
        SpringAnimation slideUpY = new SpringAnimation(v, DynamicAnimation.TRANSLATION_Y, 0f);
        slideUpY.getSpring().setStiffness(stiffness).setDampingRatio(bounciness);
        slideUpY.start();
    }

    public static void scaleDownUp(View v) {
        final SpringAnimation scaleDownX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 0.8f);
        SpringAnimation scaleDownY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 0.8f);
        scaleDownX.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH).setDampingRatio(0.9f);
        scaleDownY.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH).setDampingRatio(0.9f);

        scaleDownX.start();
        scaleDownY.start();

        final SpringAnimation scaleUpX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 1f);
        final SpringAnimation scaleUpY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 1f);
        scaleUpX.getSpring().setStiffness(600f).setDampingRatio(0.55f);
        scaleUpY.getSpring().setStiffness(600f).setDampingRatio(0.55f);

        scaleDownX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                Log.i("ANIM", animation.toString() + ", " + canceled + ", " + value + ", " + velocity);
                scaleUpX.start();
                scaleUpY.start();
            }
        });
    }

    public static void scaleUpDown(View v) {
        final SpringAnimation scaleUpX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 1.1f);
        final SpringAnimation scaleUpY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 1.1f);
        scaleUpX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW).setDampingRatio(0.9f);
        scaleUpY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW).setDampingRatio(0.9f);

        scaleUpX.start();
        scaleUpY.start();

        final SpringAnimation scaleDownX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 1f);
        final SpringAnimation scaleDownY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 1f);
        scaleUpX.getSpring().setStiffness(1300f).setDampingRatio(0.55f);
        scaleUpY.getSpring().setStiffness(1300f).setDampingRatio(0.55f);

        scaleUpX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                scaleDownX.start();
                scaleDownY.start();
            }
        });
    }

    public static void scaleUp(View v, float stiffness, float bounciness) {
        final SpringAnimation scaleUpX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 1f);
        final SpringAnimation scaleUpY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 1f);
        scaleUpX.getSpring().setStiffness(stiffness).setDampingRatio(bounciness);
        scaleUpY.getSpring().setStiffness(stiffness).setDampingRatio(bounciness);
        scaleUpX.start();
        scaleUpY.start();
    }
}
