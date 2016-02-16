package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

/* Diese Activity fragt die persönlichen Werte ab und speichert sie im
Shared Preferences Store ab. Später sollen diese Werte mal in der Heimbeeren-Cloud gespeichert
werden.
 */

public class PersonalSettingsActivity extends Activity {
    public static int weight = 60;
    public static int height = 165;
    public static int age = 35;
    public static int restingHeartRate = 60;
    public static double maxHeartRate;
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
    private NumberPicker nbpAge;
    private NumberPicker nbpWeight;
    private NumberPicker nbpHeight;
    private NumberPicker nbpRestHR;
    private RadioGroup rgbGender;
    private RadioGroup rgbFitness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        // Erstmal unsere ganzen Views in der Activity in Variabeln referenzieren.
        initializeViews();
        SharedPreferences pref = this.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        if (pref.contains("name")) {
            loadSettings(pref); // Einstellungen laden, wenn vorhanden.
            displaySettings(); // und anzeigen.
        }
    }


    public static boolean loadSettings(SharedPreferences pref) {
        // Werte aus dem Shared Preferences Store auslesen und in den Variabeln ablegen.
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

    private void displaySettings() { // Einstellungen aus den Variabeln in die Views übertragen.
        //nbpWeight.setText(Integer.toString(weight));
        //nbpHeight.setText(Integer.toString(height));
        //nbpAge.setText(Integer.toString(age));
        edtName.setText(name);
        //nbpRestHR.setText(Integer.toString(restingHeartRate));
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

    private void saveSettings() { // Shared Preferences Store öffnen und Werte abspeichern.
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
        if(checkValues()) { // Werte auf Gültigkeit überprüfen.
            name = edtName.getText().toString();
            familyName = edtFamiliyName.getText().toString();
            // checkValues(); // Werte auf Gültigkeit überprüfen (redundanter Funktionsaufruf?)
            checkGender(); // Geschlecht in Variable übertragen
            checkFitnessLevel(); // Fitnesslevel-Konstante speichern.
            calculateHR(); // Maximalpuls ausrechnen.
            saveSettings(); // Alle Werte in den Shared Preferences Speicher schieben.
        } else {
            // Warnmeldung ausgeben und nicht die Activity beenden.
            Toast.makeText(this, R.string.toast_invalid_values_text, Toast.LENGTH_SHORT).show();
            return;
        }
        // Wenn alles gut ist, beenden wir jetzt die Activity, gehen wieder zurück zur aufrufenden
        // Activity (das Hauptmenü) und übergeben ihr die erfolgreiche Einstellungseingabe
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        finish();
    }

    public void checkFitnessLevel() {
        // Radiobutton Group für den Fitnesslevel auswerten und die passende Konstanten in einer
        // Variable speichern. Wird später möglicherweise beim Training gebraucht.
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
        // Um Logikfehler zu vermeiden werden alle eingegebenen Werte auf Brauchbarkeit geprüft.
        // Alle nicht brauchbaren Werte werden rot markiert, damit der User vernünftige Werte angibt.
        try {
            // Überprüfen der eingegebenen Körpergröße
            // height = Integer.parseInt(nbpHeight.getText().toString());
            nbpHeight.setBackgroundColor(defaultColor);
            if (height < 100 || height > 250) {
                nbpHeight.setBackgroundColor(Color.RED);
                heightValid = false;
            } else {
                heightValid = true;
            }
        } catch (Exception whatHasHappened) {
            nbpHeight.setBackgroundColor(Color.RED);
            heightValid = false;
        }
        try {
            // Überprüfen des eingegebenen Ruhpuls-Wertes.
            // restingHeartRate = Integer.parseInt(nbpRestHR.getText().toString());
            nbpRestHR.setBackgroundColor(defaultColor);
            if (restingHeartRate < 30 || restingHeartRate > 100) {
                nbpRestHR.setBackgroundColor(Color.RED);
                restingHeartRateValid = false;
            } else {
                restingHeartRateValid = true;
            }
        } catch (Exception whatHasHappened) {
            nbpRestHR.setBackgroundColor(Color.RED);
            restingHeartRateValid = false;
        }
        try {
            // Überprüfen des eingegebenen Gewichtes
            //weight = Integer.parseInt(nbpWeight.getText().toString());
            nbpWeight.setBackgroundColor(defaultColor);
            if (weight < 20 || weight > 150) {
                nbpWeight.setBackgroundColor(Color.RED);
                weightValid = false;
            } else {
                weightValid = true;
            }
        } catch (Exception ThatHasHappenend) {
            nbpWeight.setBackgroundColor(Color.RED);
            weightValid = false;
        }
        try {
            // Überprüfen des eingegebenen Alters.
            //age = Integer.parseInt(nbpAge.getText().toString());
            nbpAge.setBackgroundColor(defaultColor);
            if (age < 5 || age > 110) {
                nbpAge.setBackgroundColor(Color.RED);
                ageValid = false;
            } else {
                ageValid = true;
            }
        } catch (Exception ThatHasHappenend) {
            nbpAge.setBackgroundColor(Color.RED);
            ageValid = false;
        }
        // Alle Ergebnisse überprüfen und daraufhin den Rückgabewert festlegen.
        if (ageValid && heightValid && weightValid & restingHeartRateValid) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPause() {
        // Activity wurde pausiert. Auch hier: Rückgabewerte setzen.
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Unsere Activity ist irgendwie zerstört worden?
        // Noch schnell den korrekten Rückgabewert speichern. (Funktion ist eigentlich redundant)
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        super.onDestroy();
    }

    public void checkGender() {
        // Radiobuttons für das Geschlecht auswerten und in Variable speichern.
        switch(rgbGender.getCheckedRadioButtonId()) {
            case R.id.rb_male:
                gender = "male";
                break;
            case R.id.rb_female:
                gender = "female";
                break;
        }
    }
    @Override
    public void onBackPressed() {
        // Hat einer auf Zurück gedrückt?
        // Dafür sorgen, dass die Rückgabewerte dann auch generiert werden.
        Intent backToMain = new Intent(this, ChooseTraining.class);
        backToMain.putExtra("DATA_PRESENT", true);
        setResult(RESULT_OK, backToMain);
        super.onBackPressed();
    }

    private void initializeViews() {
        // Erstmal unsere ganzen Views in der Activity in Variabeln referenzieren.
        edtName = (EditText) findViewById(R.id.prename_edit_text);
        edtFamiliyName = (EditText) findViewById(R.id.surname_edit_text);
        nbpAge = (NumberPicker) findViewById(R.id.age_number_picker);
        nbpWeight = (NumberPicker) findViewById(R.id.weight_number_picker);
        nbpHeight = (NumberPicker) findViewById(R.id.height_number_picker);
        nbpRestHR = (NumberPicker) findViewById(R.id.restHR_number_picker);
        rgbGender = (RadioGroup) findViewById(R.id.rg_gender);
        defaultColor = nbpWeight.getDrawingCacheBackgroundColor();
        rgbFitness = (RadioGroup) findViewById(R.id.rgb_fitnesslevel);

    }

    public static void calculateHR() {
        // Je nach Geschlecht die Maximal-Herzfrequenz ausrechnen. Diese wird benötigt, um alle
        // Trainingsbereiche zu berechnen.
        if (gender.equals("male")) {
            maxHeartRate = 214 - 0.5 * age - 0.11 * weight;
        } else {
            maxHeartRate = 210 - 0.5 * age - 0.11 * weight;
        }
    }
}
