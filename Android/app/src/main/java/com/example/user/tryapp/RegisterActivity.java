package com.example.user.tryapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextName,editTextUsername,editTextPassword,editTextEmail;
    Button registerButton;

    DataBaseHelper db_helper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = (EditText) findViewById(R.id.etName);
        editTextUsername = (EditText) findViewById(R.id.etUsername);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        editTextEmail = (EditText) findViewById(R.id.etEmail);
        final EditText editTextRepeatPassword = (EditText) findViewById(R.id.etPassowrdRepeat);

        registerButton = (Button) findViewById(R.id.bRegister);
        final TextView backLink = (TextView) findViewById(R.id.tvBackLink);

        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LogInActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
            }
        });

        final TextView textViewError = (TextView) findViewById(R.id.tvError);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Password = s.toString();
                String pass = editTextRepeatPassword.getText().toString();
                if (!Password.equals(pass)) {
                    textViewError.setText("Passwords must match!");
                } else {
                    textViewError.setText("");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextRepeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Password = s.toString();
                String pass = editTextPassword.getText().toString();
                if (!Password.equals(pass)) {
                    textViewError.setText("Passwords must match!");
                } else {
                    textViewError.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private boolean isNetworkAvailable() {
        //checking for internet
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void db_register(){

        //db_helper.delete_all_from_table();
        Contact c = new Contact();

        c.setName(editTextName.getText().toString());
        c.setUsername(editTextUsername.getText().toString());
        c.setEmail(editTextEmail.getText().toString());
        c.setPassword(editTextPassword.getText().toString());

        String username_status= db_helper.searchUsername(editTextUsername.getText().toString());

        if (username_status.equals("not found")) {
            db_helper.insertContact(c);
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Register Failed! Username is already taken, please try another one.")
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();

        }

    }


    public void OnRegister(View view){
        //taking info from imput filds
        String name = editTextName.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        final String email =  editTextEmail.getText().toString();
        //do something with that info
        Boolean check_network_availability_status = isNetworkAvailable();
        //System.out.println("INTERNET status -> " + check_network_availability_status);
        if (check_network_availability_status == false){
            db_register();
            Intent intent = new Intent(getBaseContext(), LoadingScreenActivity.class);
            startActivity(intent);
            //System.out.println("Dupa inregistrare apel!!!");
        }else {
            db_register();//register si in baza de date locala(SQLite)
            String REGISTER_URL = "http://10.10.100.22:80/mobile/Register.php";
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", name);
            params.put("username", username);
            params.put("email", email);
            params.put("password", password);

            CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, REGISTER_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    Intent intent = new Intent(getBaseContext(), LoadingScreenActivity.class);
                                    startActivity(intent);
                                    String email_server = response.getString("email");
                                    System.out.println(email_server);
                                    //send_email(email_server);
                                } else {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("Register Failed. Please try another username.")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(e.toString())
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage(error.toString())
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        }
    }
}
