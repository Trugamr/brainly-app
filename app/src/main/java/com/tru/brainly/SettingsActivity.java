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

class Preset {
    int minNumber;
    int maxNumber;
    long gameMaxTime;
    int currentPreset;
    Preset(int minNumber, int maxNumber, long gameMaxTime, int currentPreset) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.gameMaxTime = gameMaxTime;
        this.currentPreset = currentPreset;
    }
}

public class SettingsActivity extends AppCompatActivity {
    // Default Presets
    Preset easyPreset = new Preset(1, 20, 30000L, 1);
    Preset mediumPreset = new Preset(1, 35, 45000L, 2);
    Preset hardPreset = new Preset(1, 50, 60000L, 3);

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

        if(savedPreset == easyPreset.currentPreset) toggleEasy.setChecked(true);
        else if(savedPreset == mediumPreset.currentPreset) toggleMedium.setChecked(true);
        else if(savedPreset == hardPreset.currentPreset) toggleHard.setChecked(true);

        if(savedMaxTime == easyPreset.gameMaxTime) toggle30.setChecked(true);
        else if(savedMaxTime == mediumPreset.gameMaxTime) toggle45.setChecked(true);
        else if(savedMaxTime == hardPreset.gameMaxTime) toggle60.setChecked(true);


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
        if(!((ToggleButton) view).isChecked()) ((ToggleButton) view).setChecked(true);
        else ((RadioGroup) view.getParent()).check(view.getId());

        if(view.getId() == toggleEasy.getId()) {
            currentPreset = easyPreset.currentPreset;
            maxNumber = easyPreset.maxNumber;
        } else if(view.getId() == toggleMedium.getId()) {
            currentPreset = mediumPreset.currentPreset;
            maxNumber = mediumPreset.maxNumber;
        } else if(view.getId() == toggleHard.getId()) {
            currentPreset = hardPreset.currentPreset;
            maxNumber = hardPreset.maxNumber;
        }

        if(view.getId() == toggle30.getId()) gameMaxTime = easyPreset.gameMaxTime;
        else if(view.getId() == toggle45.getId()) gameMaxTime = mediumPreset.gameMaxTime;
        else if(view.getId() == toggle60.getId()) gameMaxTime = hardPreset.gameMaxTime;
    }
}