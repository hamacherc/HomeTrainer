package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PersonalSettingsActivity extends Activity {
    public static int weight = 60;
    public static int height = 165;
    public static int age = 35;
    public static int restingHeartRate = 60;
    public static String name;
    public static String familyName;
    public static String gender = "male";
    public static double COUCH_POTATO = 0.5;
    public static double MEDIUM_FIT = 0.6;
    public static double TOPFIT = 0.7;
    public static double fitnesslevel = COUCH_POTATO;
    private int defaultColor;
    private boolean ageValid = false;
    private boolean weightValid = false;
    private boolean heightValid = false;
    private boolean restingHeartRateValid = false;
    private EditText edtName;
    private EditText edtFamiliyName;
    private EditText edtAge;
    private EditText edtWeight;
    private EditText edtHeight;
    private EditText edtRestHR;
    private RadioGroup rgbGender;
    private RadioGroup rgbFitness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initializeViews();
        SharedPreferences pref = this.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        if (pref.contains("name")) {
            loadSettings(pref);
            displaySettings();
        }
    }


    public static boolean loadSettings(SharedPreferences pref) {
        try {
            weight = pref.getInt("weight", 60);
            height = pref.getInt("height", 165);
            age = pref.getInt("age", 35);
            name = pref.getString("name", "");
            restingHeartRate = pref.getInt("restingHeartRate", 60);
            familyName = pref.getString("familyName", "");
            gender = pref.getString("gender", "male");
            fitnesslevel = Double.longBitsToDouble(pref.getLong("fitnesslevel", 0));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private void displaySettings() {
        edtWeight.setText(Integer.toString(weight));
        edtHeight.setText(Integer.toString(height));
        edtAge.setText(Integer.toString(age));
        edtName.setText(name);
        edtRestHR.setText(Integer.toString(restingHeartRate));
        edtFamiliyName.setText(familyName);
        switch(gender) {
            case "male":
                rgbGender.check(R.id.rb_male);
                break;
            case "female":
                rgbGender.check(R.id.rb_female);
                break;
        }
        if(fitnesslevel == 0.7) {
            rgbFitness.check(R.id.rb_topfit);
        } else if (fitnesslevel == 0.6) {
            rgbFitness.check(R.id.rb_mediumfit);
        } else {
            rgbFitness.check(R.id.rb_couchpotatoe);
        }
    }

    private void saveSettings() {
        SharedPreferences pref = getSharedPreferences("UserSettings", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("weight",weight);
        editor.putInt("height",height);
        editor.putInt("age", age);
        editor.putInt("restingHeartRate", restingHeartRate);
        editor.putString("name", name);
        editor.putString("familyName", familyName);
        editor.putString("gender", gender);
        editor.putLong("fitnesslevel", Double.doubleToRawLongBits(fitnesslevel));
        editor.apply();
    }
    public void onClickReady(View view) {
        if(checkValues()) {
            name = edtName.getText().toString();
            familyName = edtFamiliyName.getText().toString();
            checkValues();
            checkFitnessLevel();
            saveSettings();
        } else {
            Toast.makeText(this, R.string.toast_invalid_values_text, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        finish();
    }

    public void checkFitnessLevel() {
        switch(rgbFitness.getCheckedRadioButtonId()) {
            case R.id.rb_couchpotatoe:
                fitnesslevel = COUCH_POTATO;
                break;
            case R.id.rb_mediumfit:
                fitnesslevel = MEDIUM_FIT;
                break;
            case R.id.rb_topfit:
                fitnesslevel = TOPFIT;
                break;
        }
    }

    public boolean checkValues() {
        try {
            height = Integer.parseInt(edtHeight.getText().toString());
            edtHeight.setBackgroundColor(defaultColor);
            if (height < 100 || height > 250) {
                edtHeight.setBackgroundColor(Color.RED);
                heightValid = false;
            } else {
                heightValid = true;
            }
        } catch (Exception whatHasHappened) {
            edtHeight.setBackgroundColor(Color.RED);
            heightValid = false;
        }
        try {
            restingHeartRate = Integer.parseInt(edtRestHR.getText().toString());
            edtRestHR.setBackgroundColor(defaultColor);
            if (restingHeartRate < 30 || restingHeartRate > 100) {
                edtRestHR.setBackgroundColor(Color.RED);
                restingHeartRateValid = false;
            } else {
                restingHeartRateValid = true;
            }
        } catch (Exception whatHasHappened) {
            edtRestHR.setBackgroundColor(Color.RED);
            restingHeartRateValid = false;
        }
        try {
            weight = Integer.parseInt(edtWeight.getText().toString());
            edtWeight.setBackgroundColor(defaultColor);
            if (weight < 20 || weight > 150) {
                edtWeight.setBackgroundColor(Color.RED);
                weightValid = false;
            } else {
                weightValid = true;
            }
        } catch (Exception ThatHasHappenend) {
            edtWeight.setBackgroundColor(Color.RED);
            weightValid = false;
        }
        try {
            age = Integer.parseInt(edtAge.getText().toString());
            edtAge.setBackgroundColor(defaultColor);
            if (age < 5 || age > 110) {
                edtAge.setBackgroundColor(Color.RED);
                ageValid = false;
            } else {
                ageValid = true;
            }
        } catch (Exception ThatHasHappenend) {
            edtAge.setBackgroundColor(Color.RED);
            ageValid = false;
        }
        if (ageValid && heightValid && weightValid & restingHeartRateValid) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPause() {
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        super.onBackPressed();
    }

    private void initializeViews() {
        edtName = (EditText) findViewById(R.id.prename_edit_text);
        edtFamiliyName = (EditText) findViewById(R.id.surname_edit_text);
        edtAge = (EditText) findViewById(R.id.age_edit_text);
        edtWeight = (EditText) findViewById(R.id.weight_edit_text);
        edtHeight = (EditText) findViewById(R.id.height_edit_text);
        edtRestHR = (EditText) findViewById(R.id.restHR_edit_text);
        rgbGender = (RadioGroup) findViewById(R.id.rg_gender);
        defaultColor = edtWeight.getDrawingCacheBackgroundColor();
        rgbFitness = (RadioGroup) findViewById(R.id.rgb_fitnesslevel);
    }
}
