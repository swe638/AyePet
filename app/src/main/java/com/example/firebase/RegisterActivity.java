package com.example.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etFullname;
    private String username;
    private String password;
    private String confirmPassword;
    private String fullName;
    private ProgressDialog pDialog;
    private String register_url = "http://192.168.43.227:80/member/db/register.php";
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullname = findViewById(R.id.etFullName);

        Button login = findViewById(R.id.btnRegisterLogin);
        Button register = findViewById(R.id.btnRegister);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString().toLowerCase().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();
                fullName = etFullname.getText().toString().trim();

                if (validateInputs()) {
                    registerUser();
                }
            }
        });
    }

        private void displayLoader () {
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Signing Up... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        private void loadDashboard () {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }

        private void registerUser() {
            displayLoader();
            JSONObject request = new JSONObject();
            try {
                request.put(KEY_USERNAME, username);
                request.put(KEY_PASSWORD, password);
                request.put(KEY_FULL_NAME, fullName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                    (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            try {
                                if (response.getInt(KEY_STATUS) == 0) {
                                    session.loginUser(username, fullName);
                                    loadDashboard();
                                } else if (response.getInt(KEY_STATUS) == 1) {
                                    etUsername.setError("Username already taken!");
                                    etUsername.requestFocus();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        }

        private boolean validateInputs(){
            if (KEY_EMPTY.equals(fullName)){
                etFullname.setError("Full Name Cannot be empty");
                etFullname.requestFocus();
                return false;
            }
            if (KEY_EMPTY.equals(username)){
                etUsername.setError("Username cannot be empty");
                return false;
            }
            if (KEY_EMPTY.equals(password)){
                etPassword.setError("Password cannot be empty");
            }
            if (KEY_EMPTY.equals(confirmPassword)){
                etConfirmPassword.setError("Confirm Password cannot be empty");
                etConfirmPassword.requestFocus();
                return false;
            }
            if (!password.equals(confirmPassword)){
                etConfirmPassword.setError("Password and Confirm Password does not match");
                etConfirmPassword.requestFocus();
                return false;
            }
            return true;
        }
}



