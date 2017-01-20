package com.example.user.tryapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import java.util.logging.Handler;

public class LogInActivity extends AppCompatActivity {

    EditText editTextUsername,editTextPassword;

    DataBaseHelper db_helper = new DataBaseHelper(this);

    Button loginButton;

    //private static final int PROGRESS = 0x1;

    //private ProgressBar mProgress;
    //private int mProgressStatus = 0;

    //private Handler mHandler = new Handler();


    //private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextUsername = (EditText) findViewById(R.id.etUsernam);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegister);

        loginButton = (Button) findViewById(R.id.bLogin);


        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
                LogInActivity.this.startActivity(registerIntent);
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


    public void db_login(){
        //System.out.println("intru aici");
        //===============================DB->search====================
        String db_id = db_helper.searchPass(editTextUsername.getText().toString(), editTextPassword.getText().toString());
        if(!db_id.equals("not found"))
        {
            Intent intent = new Intent(getBaseContext(),UserAreaActivity.class);
            intent.putExtra("id",db_id);
            startActivity(intent);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
            builder.setMessage("Login Failed -> from local DB")
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        //=============================================================
        //System.out.println("Ajung la final login method-> AFTER REGISTRATION THE USERS ARE ");
        db_helper.selectAllUsers();
    }

    public Contact check_user_is_registered_locally(String username, String password)
    {
        ArrayList<Contact> users = db_helper.selectAllUsers();
        for(int i = 0; i<users.size();i++)
        {
            if(users.get(i).getUsername().equals(username) && users.get(i).getPassword().equals(password)){
                return users.get(i);
            }
        }
        return null;
    }

    public int register_user_local_to_serve(){

        if(check_user_is_registered_locally(editTextUsername.getText().toString(),editTextPassword.getText().toString()) != null) {
            Contact user = check_user_is_registered_locally(editTextUsername.getText().toString(),editTextPassword.getText().toString());
            String REGISTER_URL = "http://10.10.100.22:80/mobile/Register.php";
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", user.getName());
            params.put("username", user.getUsername());
            params.put("email", user.getEmail());
            params.put("password", user.getPassword());

            CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, REGISTER_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("success")) {
                                    Toast.makeText(LogInActivity.this, "The authentication had succeeded :)", Toast.LENGTH_SHORT).show();
                                } else {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LogInActivity.this);
                                    builder.setMessage("Register Failed. Please try another username.")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LogInActivity.this);
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LogInActivity.this);
                    builder.setMessage(error.toString())
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
            return 1;
        }else{
            //mesaj meh nu esti inregistrat
           /* android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LogInActivity.this);
            builder.setMessage("You are not registered! please do that first")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();*/
            return 0;
        }
    }

    public void OnLogin(View view){
        //getting the info from input form
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        String LOGIN_URL = "http://10.10.100.22:80/mobile/Login.php";//"http://localhost:8082/exemple/appAndroid/Login.php";//"http://192.168.0.102/exemple/appAndroid/Login.php";//"http://localhost:8082/exemple/appAndroid/Login.php";"http://10.0.2.2/exemple/appAndroid/Login.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);
        //do something with that info
        Boolean check_network_availability_status = isNetworkAvailable();
        //System.out.println("INTERNET status -> " + check_network_availability_status);
        if (check_network_availability_status == false){
            db_login();
        }else {
            CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, LOGIN_URL, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean status = response.getBoolean("success");
                                System.out.println("STATUS => " + status);
                                if (status == true) {
                                    //System.out.println("STATUS => " + response.getBoolean("success"));
                                    Intent intent = new Intent(getBaseContext(), UserAreaActivity.class);
                                    intent.putExtra("name", response.getString("name"));
                                    intent.putExtra("username", response.getString("username"));
                                    intent.putExtra("id", response.getString("id"));
                                    startActivity(intent);
                                }
                                if (status == false) {
                                    //System.out.println("STATUS => " + response.getBoolean("success"));
                                    int register_status = register_user_local_to_serve();
                                    if(register_status == 0) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                                        builder.setMessage("Login Failed! Please register.")
                                                .setNegativeButton("Close", null)
                                                .create()
                                                .show();
                                    }else{
                                        Intent intent2 = new Intent(getBaseContext(), UserAreaActivity.class);
                                        intent2.putExtra("name", response.getString("name"));
                                        intent2.putExtra("username", response.getString("username"));
                                        intent2.putExtra("id", response.getString("id"));
                                        startActivity(intent2);
                                    }
                                }
                            } catch (JSONException e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
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
