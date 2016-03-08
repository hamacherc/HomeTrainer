package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.List;


// Jetzt führen wir das Training durch.
public class TrainingActivity extends Activity implements View.OnClickListener {

    private final static String TAG = TrainingActivity.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_SELECTED_PLAN = "SELECTED_PLAN";
    String mDeviceName, mDeviceAddress, selectedPlan;
    BluetoothLeService mBluetoothLeService;
    boolean mDeviceConnected;
    int minRekomHR, minGA1HR, minGA2HR, minEBHR, minSBHR, actualHR, minThreshold, maxThreshold,
        totalTime, currentStep, workoutMode;
    double maxHeartRate;
    boolean userDataAvailable,isManagedByHR, cycling;
    final private int WORKOUT_GA1 = 1;
    final private int WORKOUT_GA2 = 2;
    final private int WORKOUT_TABLE = 3;
    final private int WORKOUT_VIDEO = 4;
    TextView mDataField, txtDeviceName, countdownTimerView, txvMessage, txvUpperCadence,
            txvLowerCadence, txvSlope, txvFrontGear, txvBackGear, txvStepDetails;
    Button butStartTraining;
    Chronometer stepWatch, stopWatch;

    // Unseren Datenbankhelfer aktivieren.
    WorkoutsDBHelper workoutPlan = new WorkoutsDBHelper(this);

    private void initializeViews() {
        // Unsere Views referenzieren.
        txtDeviceName = (TextView) findViewById(R.id.used_belt_name);
        mDataField = (TextView) findViewById(R.id.txtPulse_view);
        stopWatch = (Chronometer) findViewById(R.id.chronometer);
        countdownTimerView = (TextView) findViewById(R.id.countdown_timer_view);
        butStartTraining = (Button) findViewById(R.id.but_start);
        txvMessage = (TextView) findViewById(R.id.text_Message);
        txvBackGear = (TextView) findViewById(R.id.txt_gear_back);
        txvFrontGear = (TextView) findViewById(R.id.txt_gear_front);
        txvSlope = (TextView) findViewById(R.id.txt_slope);
        txvLowerCadence = (TextView) findViewById(R.id.txt_rpm_lower);
        txvUpperCadence = (TextView) findViewById(R.id.txt_rpm_upper);
        txvStepDetails = (TextView) findViewById(R.id.txv_workout_step_details);
        stepWatch = (Chronometer) findViewById(R.id.stepometer);
        // Zeichensatz für unsere Stoppuhr festlegen (7-Digit-Anzeige)
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/lcdmn.ttf");
        stopWatch.setTypeface(type);
        stepWatch.setTypeface(type);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        // Während des Trainings wollen wir nicht, dass der Bildschirm ausgeht.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Views in Variabeln referenzieren.
        initializeViews();

        // Brustgurtnamen und zu trainierenden Trainingsplan auswerten.
        Intent previousActivity = getIntent();
        mDeviceName = previousActivity.getExtras().getString(EXTRAS_DEVICE_NAME);
        mDeviceAddress = previousActivity.getExtras().getString(EXTRAS_DEVICE_ADDRESS);
        selectedPlan = previousActivity.getExtras().getString(EXTRAS_SELECTED_PLAN);
        Log.d(TAG, "Es wird " + selectedPlan + " trainiert.");

        // Herfrequenz berechnen, wenn Userdaten verfügbar sind.
        SharedPreferences pref = this.getSharedPreferences("UserSettings", MODE_PRIVATE);
        if (pref.contains("name")) {
            userDataAvailable = PersonalSettingsActivity.loadSettings(pref);
            if (userDataAvailable) {
                calculateHR();
            }
        }

        // Art des Trainingsplans auslesen.
        workoutMode = workoutPlan.getWorkoutType(selectedPlan);

        // Name des verwendeten Pulsgurtes einblenden.
        txtDeviceName.setText(mDeviceName);

        // Den Bluetooth-Service für den Pulsgurt einbinden
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Listener für unseren Start-Button
        butStartTraining.setOnClickListener(this);

        isManagedByHR = false;
    }



    private void displayData(String data) {
        if (data != null) {
            actualHR = Integer.parseInt(data); // Pulswert zum Berechnen auslesen.
            mDataField.setText(data); // Pulswert auf die Anzeige schicken.
            // Je nach Belastung (individuellen Traininsbereiche) die Pulsanzeige einfärben.
            if(actualHR >= minRekomHR && actualHR < minGA1HR) {
                mDataField.setTextColor(Color.parseColor("#0c6c1b"));
            } else if (actualHR >= minGA1HR && actualHR < minGA2HR) {
                mDataField.setTextColor(Color.parseColor("#49c65c"));
            } else if (actualHR >= minGA2HR && actualHR < minEBHR) {
                mDataField.setTextColor(Color.YELLOW);
            } else if (actualHR >= minEBHR && actualHR < minSBHR) {
                mDataField.setTextColor(Color.parseColor("#ff6a0e"));
            } else if (actualHR >= minSBHR) {
                mDataField.setTextColor(Color.RED);
            } else mDataField.setTextColor(Color.WHITE);
            if (actualHR < minThreshold && isManagedByHR) {
                txvMessage.setText("Du musst mehr Gas geben!!!\nPuls muss mindestens " + minThreshold + " sein!!");
            } else if (actualHR > maxThreshold && isManagedByHR)  {
                txvMessage.setText(R.string.text_go_more_relaxed);
            } else if (isManagedByHR) {
                txvMessage.setText("Super! Du bist in Deinem Trainingsbereich!");
            }
        }
    }


    @Override
    public void onClick(View PushedButton) {
        switch(PushedButton.getId()) {
            case R.id.but_start:
                PushedButton.setVisibility(View.GONE);
                CountDownTimer countdown = new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countdownTimerView.setText("" + (millisUntilFinished / 1000));
                    }
                    @Override
                    public void onFinish() {
                        countdownTimerView.setText(R.string.readygo_text);
                        Handler handler = new Handler();
                        handler.postDelayed(nextMove, 1500);
                        startBiking();
                    }
                };
                countdown.start();
                break;
        }
    }

    Runnable nextMove = new Runnable() {
        public void run() {
            countdownTimerView.setVisibility(View.GONE);
        }
    };


    public void calculateHR() {
        // individuellen Trainingsbereiche festlegen.
        PersonalSettingsActivity.calculateHR();
        maxHeartRate = PersonalSettingsActivity.maxHeartRate;
        minRekomHR = (int) Math.round(maxHeartRate / 2);
        minGA1HR = (int) Math.round(maxHeartRate * 0.6);
        minGA2HR = (int) Math.round(maxHeartRate * 0.7);
        minEBHR = (int) Math.round(maxHeartRate * 0.8);
        minSBHR = (int) Math.round(maxHeartRate * 0.9);
    }

    protected void startBiking() {
        totalTime = workoutPlan.getWorkoutTotalTime(selectedPlan);
        workoutPlan.loadCurrentWorkoutPlan(selectedPlan);
        currentStep = 0;

        Log.d(TAG, "Gesamttrainigsdauer: " + totalTime + " Minuten");
        Log.d(TAG, "Anzahl der Trainingsschritte: " + workoutPlan.totalSteps);

        stopWatch.setBase(SystemClock.elapsedRealtime());
        stepWatch.setBase(SystemClock.elapsedRealtime());
        stopWatch.start();
        stepWatch.start();
        cycling = true;
         switch (workoutMode) {
            case WORKOUT_GA2:
                txvMessage.setTextColor(Color.GREEN);
                isManagedByHR = true;
                minThreshold = minGA1HR;
                maxThreshold = minEBHR;
                manageByHR();
                break;
            case WORKOUT_TABLE:
                txvMessage.setTextColor(Color.GREEN);
                isManagedByHR = true;
                minThreshold = minGA1HR;
                maxThreshold = minEBHR;
                manageByHR();
                break;
            case WORKOUT_VIDEO:
                manageByTerrain();
                break;
        }
    }

    protected void manageByHR() {

        stepWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            double stepTimeMS;

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (currentStep < workoutPlan.totalSteps) {
                    stepTimeMS = (workoutPlan.stepTime[currentStep] * 1000 * 60);
                    showWorkoutData();
                    if (SystemClock.elapsedRealtime() - chronometer.getBase() >= stepTimeMS) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        currentStep++;
                    }
                } else {
                    cycling = false;
                    stepWatch.stop();
                    stopWatch.stop();
                    stepWatch.setVisibility(View.GONE);
                    txvMessage.setText("Training beendet!");
                }
            }
    });
}
    protected void manageByTime() {

    }

    protected void manageByTerrain() {

    }

    protected void showWorkoutData() {
        if (cycling) {
            txvStepDetails.setText("Schritt " + (currentStep + 1) + ": " +
                    workoutPlan.stepTime[currentStep] + " Min.\n" +
                    workoutPlan.stepDetails[currentStep]);
            txvLowerCadence.setText(workoutPlan.lowerCadence[currentStep]);
            txvUpperCadence.setText(workoutPlan.upperCadence[currentStep]);
            txvBackGear.setText(Integer.toString(workoutPlan.gearBack[currentStep]));
            txvFrontGear.setText(Integer.toString(workoutPlan.gearFront[currentStep]));
            txvSlope.setText(Integer.toString(workoutPlan.slope[currentStep]));
        }
    }

    // Bluetooth Geraffel
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();

            if (uuid.equals("0000180d-0000-1000-8000-00805f9b34fb")) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }

    };

    // Hier werden die Aktionen festgelegt, die ausgeführt werden, wenn es Neuigkeiten
    // vom Brustgurt gibt.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            TextView txtDeviceName = (TextView) findViewById(R.id.used_belt_name);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mDeviceConnected = true;
                txtDeviceName.setTextColor(Color.GREEN);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mDeviceConnected = false;
                txtDeviceName.setTextColor(Color.RED);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    // Activity Lifecycle stuff
    @Override
    protected void onResume() {
        super.onResume();
        // Wenn Activity wieder aufwacht, Verbindung zum Brustgurt wiederherstellen.
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity pausiert --> Brustgurt pausiert.
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Jemand Activity kaputt gemacht? Brustgurt abklemmen.
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


}
