package com.tru.brainly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {
    RadioGroup difficultyRadioGroup, timeRadioGroup;
    ToggleButton toggleEasy, toggleMedium, toggleHard, toggle30, toggle45, toggle60;

    int minNumber = 1;
    int maxNumber = 20;
    long gameMaxTime = 30000L;
    int currentPreset = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toggleEasy = findViewById(R.id.toggleEasy);
        toggleMedium = findViewById(R.id.toggleMedium);
        toggleHard = findViewById(R.id.toggleHard);
        toggle30 = findViewById(R.id.toggle30);
        toggle45 = findViewById(R.id.toggle45);
        toggle60 = findViewById(R.id.toggle60);

        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup);
        timeRadioGroup = findViewById(R.id.timeRadioGroup);

        RadioGroup.OnCheckedChangeListener toggleListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i = 0; i < difficultyRadioGroup.getChildCount(); i++) {
                    final ToggleButton view = (ToggleButton) group.getChildAt(i);
                    view.setChecked(view.getId() == checkedId);
                }
            }
        };

        difficultyRadioGroup.setOnCheckedChangeListener(toggleListener);
        timeRadioGroup.setOnCheckedChangeListener(toggleListener);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        long savedMaxTime = sharedPreferences.getLong("gameMaxTime", 30000L);
        int savedPreset = sharedPreferences.getInt("currentPreset", 1);

        switch (savedPreset) {
            case 1:
                toggleEasy.setChecked(true);
                break;
            case 2:
                toggleMedium.setChecked(true);
                break;
            case 3:
                toggleHard.setChecked(true);
                break;
        }

        if(savedMaxTime == 30000L) toggle30.setChecked(true);
        else if(savedMaxTime == 45000L) toggle45.setChecked(true);
        else if(savedMaxTime == 60000L) toggle60.setChecked(true);


    }

    public void applySettings(View view) {
        Intent gameActivity = new Intent(this, GameActivity.class);
        gameActivity.putExtra("minNumber", minNumber);
        gameActivity.putExtra("maxNumber", maxNumber);
        gameActivity.putExtra("gameMaxTime", gameMaxTime);
        gameActivity.putExtra("currentPreset", currentPreset);
        setResult(1, gameActivity);
        finish();
    }

    public void openGameActivity(View view) {
        finish();
    }

    public void onToggle(View view) {
        ((RadioGroup)view.getParent()).check(view.getId());

        if(view.getId() == toggleEasy.getId()) {
            currentPreset = 1;
            maxNumber = 20;
        } else if(view.getId() == toggleMedium.getId()) {
            currentPreset = 2;
            maxNumber = 35;
        } else if(view.getId() == toggleHard.getId()) {
            currentPreset = 3;
            maxNumber = 50;
        }

        if(view.getId() == toggle30.getId()) gameMaxTime = 30000L;
        else if(view.getId() == toggle45.getId()) gameMaxTime = 45000L;
        else if(view.getId() == toggle60.getId()) gameMaxTime = 60000L;
    }
}