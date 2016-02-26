package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText edtNewName, edtTextUsername, edtTextPassword, edtTextEmail;
    private Button butRegister;

    private static final String REGISTER_URL="http://hometrainer.heimbeeren.de/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtNewName = (EditText) findViewById(R.id.edt_new_fullname);
        edtTextUsername = (EditText) findViewById(R.id.edt_new_username);
        edtTextPassword = (EditText) findViewById(R.id.edt_new_password);
        edtTextEmail = (EditText) findViewById(R.id.edt_new_email);
        butRegister = (Button) findViewById(R.id.but_new_register);
        butRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == butRegister) {
            registerUser();
        }
    }

    private void registerUser() {
        String name = edtNewName.getText().toString().trim().toLowerCase();
        String username = edtTextUsername.getText().toString().trim().toLowerCase();
        String password = edtTextPassword.getText().toString().trim().toLowerCase();
        String email = edtTextEmail.getText().toString().trim().toLowerCase();

        register(name,username,password,email);
    }

    private void register(String name, String username, String password, String email) {
        class RegisterUser extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            RegisterUserClass ruc = new RegisterUserClass();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this, "Please Wait", null, true, true);
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

                String result = ruc.sendPostRequest(REGISTER_URL, data);

                return result;
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(name,username,password,email);

    }

}
