package de.heimbeeren.hometrainer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class ChooseTraining extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DATA_PRESENT = "DATA_PRESENT";
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean boolPersonalDataPresent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_training);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button butStartTraining = (Button) findViewById(R.id.but_start_training);

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

        butStartTraining.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_training, menu);
        return true;
    }


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
            startActivityForResult(getHeartRateBeltIntent, REQUEST_A_BELT);
            return true;
        } else if (id == R.id.action_personal_settings) {
            Intent getPersonalSettingsIntent = new Intent(this, PersonalSettingsActivity.class);
            final int REQUEST_PERSONAL_SETTINGS = 2;
            startActivityForResult(getPersonalSettingsIntent, REQUEST_PERSONAL_SETTINGS);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            mDeviceName = data.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            TextView nameOfBelt = (TextView) findViewById(R.id.view_device_name);
            TextView addressOfBelt = (TextView) findViewById(R.id.view_device_address);

            nameOfBelt.setText(mDeviceName);
            addressOfBelt.setText(mDeviceAddress);
        }
        else if (requestCode == 2) {
            boolPersonalDataPresent = data.getBooleanExtra(EXTRAS_DATA_PRESENT, false);
        }

    }

    @Override
    public void onClick(View PushedButton) {
        switch(PushedButton.getId()) {
            case R.id.but_start_training:
                Intent nowStartTraining = new Intent(this, TrainingActivity.class);
                nowStartTraining.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                nowStartTraining.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(nowStartTraining);

               break;
        }
    }
}
