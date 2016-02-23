package de.heimbeeren.hometrainer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.params.HttpParams;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


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
                AsyncDataClass asyncRequestObject = new AsyncDataClass();
                asyncRequestObject.execute(serverUrl, enteredUsername, enteredPassword);
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

    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            butLogin.setClickable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> loginCredentials = new HashMap<String, String>();
            loginCredentials.put("username",params[1]);
            loginCredentials.put("password",params[2]);
            String postParameters = createQueryStringForParameters(loginCredentials);

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.setReadTimeout(10000 /*milliseconds*/ );
                //connection.setConnectTimeout( 15000 /* milliseconds */ );
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                Log.d(TAG, postParameters);
                connection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(postParameters);
                //out.flush();
                out.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "POST Response Code :: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d(TAG, response.toString());

                } else {
                    Log.e(TAG, "POST Request didn't work!");
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            butLogin.setClickable(true);
        }
    }

    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';
    public static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(
                                parameters.get(parameterName)));
                firstParameter = false;
            }
        }

        return parametersAsQueryString.toString();
    }

    private void initializeViews() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        butLogin = (Button) findViewById(R.id.but_login);
        butRegister = (Button) findViewById(R.id.but_register);
    }
}
