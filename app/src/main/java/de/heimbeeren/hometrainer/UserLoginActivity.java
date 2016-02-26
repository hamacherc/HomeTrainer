package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class UserLoginActivity extends Activity implements View.OnClickListener {

    protected EditText edtUsername;
    private EditText edtPassword;
    protected String enteredUsername;
    protected Button butLogin;
    protected Button butRegister;

    //private final String serverUrl = "http://httpbin.org/post";

    private static final String LOGIN_URL="http://hometrainer.heimbeeren.de/login.php";
    private final static String TAG = UserLoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
    }

    private void initializeViews() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        butLogin = (Button) findViewById(R.id.but_login);
        butRegister = (Button) findViewById(R.id.but_register);
    }

    private void registerUser() {
        String username = edtUsername.getText().toString().trim().toLowerCase();
        String password = edtPassword.getText().toString().trim().toLowerCase();

        login(username, password);
    }

    private void login(String username, String password) {
        class LoginUser extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            RegisterUserClass ruc = new RegisterUserClass();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UserLoginActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String, String>();
                data.put("name", params[0]);
                data.put("username", params[1]);
                data.put("password", params[2]);
                data.put("email", params[3]);

                String result = ruc.sendPostRequest(LOGIN_URL, data);

                return result;
            }
        }

        LoginUser ru = new LoginUser();
        ru.execute(username,password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_register:
                Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.but_login:
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
                break;
        }
    }
}
