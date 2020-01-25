package com.tru.brainly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class GameActivity extends AppCompatActivity {
    Button optionOne, optionTwo, optionThree, optionFour;
    TextView timerTextView, scoreTextView, questionTextView;
    ConstraintLayout overlayScreen, mainScreen;
    Game game;

    public void startGame(View v) {
        overlayScreen.setVisibility(View.INVISIBLE);
        game.startGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlayScreen = findViewById(R.id.overlayScreen);
        mainScreen = findViewById(R.id.mainScreen);

        optionOne = findViewById(R.id.optionOneButton);
        optionTwo = findViewById(R.id.optionTwoButton);
        optionThree = findViewById(R.id.optionThreeButton);
        optionFour = findViewById(R.id.optionFourButton);
        Button[] optionButtons = {optionOne, optionTwo, optionThree, optionFour};

        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        questionTextView = findViewById(R.id.questionTextView);
        TextView[] textViews = {timerTextView, scoreTextView, questionTextView};

        ConstraintLayout[] gameScreens = {overlayScreen, mainScreen};

        game = new Game(optionButtons, textViews, gameScreens);
    }
}

class Game {
    int minNumber = 1;
    int maxNumber = 31;
    int currentScore = 0;
    int numberOfQuestionsAsked = 0;
    boolean gameRunning = false;
    long gameMaxTime = 5000L;

    Button optionOne, optionTwo, optionThree, optionFour;
    Button[] optionButtons;
    TextView timerTextView, scoreTextView, questionTextView;
    ConstraintLayout overlayScreen, mainScreen;

    @SuppressLint("ClickableViewAccessibility")
    Game(Button[] optionButtons, TextView[] textViews, ConstraintLayout[] gameScreens) {
        optionOne = optionButtons[0];
        optionTwo = optionButtons[1];
        optionThree = optionButtons[2];
        optionFour = optionButtons[3];
        this.optionButtons = optionButtons;

        for(Button btn : optionButtons) {
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN)
                        chooseOption(v);
                    return false;
                }
            });
        }

        timerTextView = textViews[0];
        scoreTextView = textViews[1];
        questionTextView = textViews[2];

        this.overlayScreen = gameScreens[0];
        this.mainScreen = gameScreens[1];
    }

    public void startGame() {
        Log.i("XD", "GAME_STARTED");
        gameRunning = true;
        CountDownTimer countDownTimer = startCountDown();
        nextQuestion();
    }

    public void endGame() {
        Log.i("XD", "GAME_ENDED");
        currentScore = 0;
        numberOfQuestionsAsked = 0;
        gameRunning = false;
        overlayScreen.setVisibility(View.VISIBLE);
    }

    public void nextQuestion() {
        Log.i("XD", "NEXT_QUESTION");
        Question question = Question.generateChallenge(minNumber, maxNumber);
        questionTextView.setText(String.format(Locale.ENGLISH, "%s = ?", question.equation));
        ArrayList<Integer> options = question.options;
        for(int i = 0; i < options.size(); i++) {
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
            currentScore += 1;
        }
        numberOfQuestionsAsked += 1;
        scoreTextView.setText(String.format(Locale.ENGLISH, "%d / %d", currentScore, numberOfQuestionsAsked));
        nextQuestion();
    }

    public CountDownTimer startCountDown() {
        final CountDownTimer countDownTimer = new CountDownTimer(gameMaxTime + 100, 1000) {
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
        // bound set to 3 , disabled division for now due to floats
        int symbol = random.nextInt(3);
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
                equation = String.format("%d ÷ %d", firstNumber, secondNumber);
                result = firstNumber / secondNumber;
                break;
        }
        return new Question(equation, result, generateOptions(result + 20, 3, result));
    }

    private static ArrayList<Integer> generateOptions(int maxNumber, int numOfOptions, int correctOption) {
        maxNumber = Math.abs(maxNumber);
        ArrayList<Integer> options = new ArrayList<>();
        if (maxNumber <= 3)
            maxNumber = 10;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<maxNumber; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        for (int i=0; i < numOfOptions; i++) {
            options.add(list.get(i));
        }
        options.add(correctOption);
        Collections.shuffle(options);
        return options;
    }
}
