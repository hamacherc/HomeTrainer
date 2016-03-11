package de.heimbeeren.hometrainer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;


public class ChooseTraining extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private final static String TAG = ChooseTraining.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String SELECTED_PLAN = "SELECTED_PLAN";
    private Spinner planSpinner;
    private String mDeviceName,selectedPlan;
    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_training);
        initializeBluetooth();
        firstWorkoutPlan();
        initializeViews();
    }

    // Standardcode für das Menü.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_training, menu);
        return true;
    }


    // Hier wird entschieden, was passiert, wenn ein Menüpunkt aufgerufen wird.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent getHeartRateBeltIntent = new Intent(this, DeviceScanActivity.class);
            final int REQUEST_A_BELT = 1;
            // Auswahl-Activity für den Brustgurt starten. Als Rückgabe wird der Name
            // und die Seriennummer des Brustgurtes als String erwartet.
            startActivityForResult(getHeartRateBeltIntent, REQUEST_A_BELT);
            return true;
        } else if (id == R.id.action_personal_settings) {
            Intent getPersonalSettingsIntent = new Intent(this, PersonalSettingsActivity.class);
            final int REQUEST_PERSONAL_SETTINGS = 2;
            // Hier wird die Activity für die persönlichen Einstellungen gestartet.
            // Als Ergebnis wird ein Boolscher Wert erwartet, der True ist, sobald gültige
            // Einstellungen vorliegen.
            startActivityForResult(getPersonalSettingsIntent, REQUEST_PERSONAL_SETTINGS);
        } else if (id == R.id.action_user_login) {
            Intent UserLoginIntent = new Intent(this, UserLoginActivity.class);
            final int USER_LOGIN = 3;
            // Es wird eine Activity aufgerufen, in der versucht wird,
            // einen Userlogin per MySQL-DB zu verwirklichen
            startActivityForResult(UserLoginIntent, USER_LOGIN);
        }

        return super.onOptionsItemSelected(item);
    }

    // Von den Activities zurückgegebene Ergebnisse auswerten.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // Brustgurt-Auswahl-Activity? Dann
            // Rückgabewerte auffangen.
            mDeviceName = data.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            // Textviews zum Anzeigen des ausgewählten Brustgurtes.
            TextView nameOfBelt = (TextView) findViewById(R.id.view_device_name);
            TextView addressOfBelt = (TextView) findViewById(R.id.view_device_address);
            nameOfBelt.setText(mDeviceName);
            addressOfBelt.setText(mDeviceAddress);
        }
        else if (requestCode == 2) {
        }

    }

    @Override
    public void onClick(View PushedButton) {
        switch(PushedButton.getId()) {
            // Hat etwa jemand den Startknopf gedrückt? Dann Trainings-Activity starten.
            case R.id.but_start_training:
                Intent nowStartTraining = new Intent(this, TrainingActivity.class);
                nowStartTraining.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                nowStartTraining.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                nowStartTraining.putExtra(SELECTED_PLAN, selectedPlan);
                startActivity(nowStartTraining);
               break;
        }
    }

    private void firstWorkoutPlan() {

        // Load the Workout Database

        String workoutPlanName;

        int[] slope;
        String[] stepTime;
        int[] gearFront;
        int[] gearBack;
        String[] lowerCadence;
        String[] upperCadence;
        String[] stepDetails;

        WorkoutsDBHelper dbHelper = new WorkoutsDBHelper(this);
        dbHelper.deleteWorkoutPlanList();

        workoutPlanName = "Ausdauertraining GA2";
        slope = new int[] {0};
        stepTime = new String[] {"120"};
        gearFront = new int[] {2};
        gearBack = new int[] {6};
        lowerCadence = new String[] {"90"};
        upperCadence = new String[] {"100"};
        stepDetails = new String[] {"120 Minuten Ausdauertraining nach Puls"};
        for (int i= 0; i < slope.length; i++) {
            dbHelper.insertWorkoutStep(workoutPlanName, 2, slope[i], stepTime[i], gearFront[i], gearBack[i],
                    lowerCadence[i], upperCadence[i], stepDetails[i]);
        }


        slope = new int[] {-3,0,2,-2,3,-3,1,-3,-4,0,-1,0,1,0,-4};
        stepTime = new String[] {"4","3","1","1","1","1","1","2","3","2","2","3","2","2","4"};
        gearFront = new int[] {2,3,2,2,2,2,3,2,2,3,2,2,2,3,2};
        gearBack = new int[] {6,7,7,7,6,8,8,8,8,8,8,8,8,8,6};
        lowerCadence = new String[] {"80","<","","90","","90","","90",">","","","80","","80","100"};
        upperCadence = new String[] {"100","80","","100","","110","","110","80","","110","100","110","100","110"};
        stepDetails = new String[] {"Aufwärmen!", "3 * 30 Sek. Gas geben und 30 sek. ruhen", "30 - 35 km/h",
                "Locker Pedalieren","30 - 35 km/h", "Locker Pedalieren", "30 - 35 km/h", "Locker Pedalieren",
                "3 * 30 Sek. Gas geben und 30 sek. ruhen", "35 - 40 km/h", "Locker Pedalieren",
                "3 * 30 sek. nur linkes Bein und 30 Sek. nur rechtes Bein",
                "", "Vollgas!", "Abkühlphase"};


        workoutPlanName = "Intervalltraining BRA1";
        for (int i= 0; i < slope.length; i++) {
            dbHelper.insertWorkoutStep(workoutPlanName, 3, slope[i], stepTime[i], gearFront[i], gearBack[i],
                    lowerCadence[i], upperCadence[i], stepDetails[i]);
        }

        slope = new int[] {-4,0,-1,0,-2,-1,-2,0,-1,1,-1,-2,-1,-2,-4};
        stepTime = new String[] {"8","4","2","2","2","3","4","3","2","2","1","2","2","1","5"};
        gearFront = new int[] {2,3,2,3,2,2,2,2,2,2,2,2,2,3,2};
        gearBack = new int[] {2,3,5,3,7,2,7,3,7,3,7,6,3,3,2};
        lowerCadence = new String[] {"95","80","90","90","80","110","80","110","80","110","90","","80","80","90"};
        upperCadence = new String[] {"+","+","+","+","+","","90","","90","","90","","","100","100"};
        stepDetails = new String[] {"Aufwärmen!", "2 * 1 Min. 40 - 45 km/h und LOCKER!", "30 - 35 km/h",
                "2 * 1 Min. 40 - 45 km/h und LOCKER!","30 - 33 km/h", "Hohe Geschwindigkeit", "Locker pedalieren", "Hohe Geschwindigkeit",
                "Locker pedalieren", "Hohe Geschwindigkeit", "Locker pedalieren", "3 * 30 Sek. Gas geben und 30 sek. ruhen",
                "3 * 30 sek. nur linkes Bein und 30 Sek. nur rechtes Bein", "Locker pedalieren", "Abkühlphase"};

        workoutPlanName = "Intervalltraining BRA3";
        for (int i= 0; i < slope.length; i++) {
            dbHelper.insertWorkoutStep(workoutPlanName, 3, slope[i], stepTime[i], gearFront[i], gearBack[i],
                    lowerCadence[i], upperCadence[i], stepDetails[i]);
        }

        slope = new int[] {-3,1,0};
        stepTime = new String[] {"1","1","1"};
        gearFront = new int[] {2,3,2};
        gearBack = new int[] {6,7,7};
        lowerCadence = new String[] {"<","80","100"};
        upperCadence = new String[] {"100","80","110"};
        stepDetails = new String[] {"Aufwärmen","Trainieren","Cool Down"};

        workoutPlanName = "Testtraining";
        for (int i= 0; i < slope.length; i++) {
            dbHelper.insertWorkoutStep(workoutPlanName, 3, slope[i], stepTime[i], gearFront[i], gearBack[i],
                    lowerCadence[i], upperCadence[i], stepDetails[i]);
        }
    }

    private void loadSpinnerData() {
        // Datenbank-Helfer
        WorkoutsDBHelper db = new WorkoutsDBHelper(getApplicationContext());

        // Alle Plannamen laden
        List<String> plans = db.getPlanList();

        // Adapter für den Spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, plans);

        // Adapter Layout setzen
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Adapter an den Spinner binden
        planSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPlan = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "Selected Plan: " + selectedPlan);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        planSpinner = (Spinner) findViewById(R.id.spinner_select_mode);
        planSpinner.setOnItemSelectedListener(this);
        loadSpinnerData();
        selectedPlan = planSpinner.getSelectedItem().toString();
        Log.d(TAG, "Selected Plan: " + selectedPlan);
        Button butStartTraining = (Button) findViewById(R.id.but_start_training);
        butStartTraining.setOnClickListener(this);
    }

    private void initializeBluetooth() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

}
