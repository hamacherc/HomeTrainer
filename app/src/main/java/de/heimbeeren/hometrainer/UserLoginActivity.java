package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserLoginActivity extends Activity {

    protected EditText edtUsername;
    private EditText edtPassword;
    protected String enteredUsername;
    protected Button butLogin;
    protected Button butRegister;

    //private final String serverUrl = "http://httpbin.org/post";

    private final String serverUrl = "http://hometrainer.heimbeeren.de/android_user_api";
    private final static String TAG = UserLoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredUsername = edtUsername.getText().toString();
                String enteredPassword = edtPassword.getText().toString();
                if (enteredUsername.equals("") || enteredPassword.equals("")) {
                    Toast.makeText(UserLoginActivity.this, R.string.hint_user_pass_filled,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enteredUsername.length() < 1 || enteredPassword.length() < 1) {
                    Toast.makeText(UserLoginActivity.this, getString(R.string.hint_user_pass_min_length),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        butLogin = (Button) findViewById(R.id.but_login);
        butRegister = (Button) findViewById(R.id.but_register);
    }
}
