package com.tru.brainly;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int minNumber = 1;
    int maxNumber = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                equation = String.format("%d + %d", firstNumber + secondNumber);
                result = firstNumber + secondNumber;
                break;
            case 1:
                equation = String.format("%d - %d", firstNumber - secondNumber);
                result = firstNumber - secondNumber;
                break;
            case 2:
                equation = String.format("%d × %d", firstNumber * secondNumber);
                result = firstNumber * secondNumber;
                break;
            case 3:
                equation = String.format("%d ÷ %d", firstNumber / secondNumber);
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
