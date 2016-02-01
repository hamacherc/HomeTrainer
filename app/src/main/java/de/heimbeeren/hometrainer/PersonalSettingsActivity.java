package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by chris on 30.01.2016.
 */
public class PersonalSettingsActivity extends Activity {
    private int weight = 60;
    private int height = 165;
    private int age = 35;
    private double maxHeartRate;
    private int restingHeartRate = 60;
    private String name;
    private String familyName;
    private String gender = "male";
    final private double COUCH_POTATO = 0.5;
    final private double MEDIUM_FIT = 0.6;
    final private double TOPFIT = 0.7;
    private double fitnesslevel = COUCH_POTATO;
    private int defaultColor;
    private boolean ageValid = false;
    private boolean weightValid = false;

    private EditText edtName;
    private EditText edtFamiliyName;
    private EditText edtAge;
    private EditText edtWeight;
    private EditText edtHeight;
    private EditText edtRestHR;
    private TextView txvMaxHR;
    private RadioGroup rgbGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        edtName = (EditText) findViewById(R.id.prename_edit_text);
        edtFamiliyName = (EditText) findViewById(R.id.surname_edit_text);
        edtAge = (EditText) findViewById(R.id.age_edit_text);
        edtWeight = (EditText) findViewById(R.id.weight_edit_text);
        edtHeight = (EditText) findViewById(R.id.height_edit_text);
        edtRestHR = (EditText) findViewById(R.id.restHR_edit_text);
        rgbGender = (RadioGroup) findViewById(R.id.rg_gender);
        txvMaxHR = (TextView) findViewById(R.id.max_pulse_text);
        defaultColor = edtWeight.getDrawingCacheBackgroundColor();
        rgbGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (ageValid && weightValid) calculateMaxHR();
            }
        });
        edtWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    weight = Integer.parseInt(edtWeight.getText().toString());
                    edtWeight.setBackgroundColor(defaultColor);
                }
                catch (Exception ThatHasHappenend) {
                    edtWeight.setBackgroundColor(Color.RED);
                    weightValid = false;
                    txvMaxHR.setText("---");
                    return;
                }
                if (weight < 20 || weight > 150) {
                    edtWeight.setBackgroundColor(Color.RED);
                    weightValid = false;
                    txvMaxHR.setText("---");
                    return;
                } else {
                    weightValid = true;
                    if (ageValid) calculateMaxHR();
                    return;
                }

            }
        });
        edtAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    age = Integer.parseInt(edtAge.getText().toString());
                    edtAge.setBackgroundColor(defaultColor);
                }
                catch (Exception ThatHasHappenend) {
                    edtAge.setBackgroundColor(Color.RED);
                    ageValid = false;
                    txvMaxHR.setText("---");
                    return;
                }
                if (age < 5 || age > 110) {
                    edtAge.setBackgroundColor(Color.RED);
                    ageValid = false;
                    txvMaxHR.setText("---");
                    return;
                } else {
                    ageValid = true;
                    if (weightValid) calculateMaxHR();
                    return;
                }
            }
        });
    }

    public void onClickReady(View view) {
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        finish();
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

    public void calculateMaxHR() {
        if (rgbGender.getCheckedRadioButtonId() == R.id.rb_male){
            gender = "male";
            maxHeartRate = 214 - 0.5 * age - 0.11 * weight;
        } else {
            gender = "female";
            maxHeartRate = 210 - 0.5 * age - 0.11 * weight;
        }
        txvMaxHR.setText(String.valueOf(maxHeartRate));
    }


}
