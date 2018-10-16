package com.example.owner.mapDemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserLoginActivity extends AppCompatActivity {


    private EditText phoneEditText, passwordEditText;
    private RequestQueue mQueue;
    String phone, password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //initialising the
        phoneEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        OpenRegButton();
    }

    private void OpenRegButton() {
        Button regButton = findViewById(R.id.openRegisterButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, UserRegisterActivity.class));
            }
        });
    }

    public void onLogin(View view) {
        String phoneCheck, passCheck;
        phoneCheck = String.valueOf(phoneEditText.getText());
        passCheck = String.valueOf(passwordEditText.getText());
        if (passCheck.trim().equals("")|| phoneCheck.trim().equals("")) {

            Toast.makeText(UserLoginActivity.this, "Please Enter valid information", Toast.LENGTH_LONG).show();

            Intent intent = getIntent();
            finish();
            startActivity(intent);

        } else {
            phone = phoneEditText.getText().toString();
            password = passwordEditText.getText().toString();
            mQueue = Volley.newRequestQueue(this);

            String type = "login";
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);

            String result = null;
            try {
                result = backgroundWorker.execute(type, phone, password).get();

                switch (result) {
                    case "login details are incorrect":
                        phoneEditText.getText().clear();
                        passwordEditText.getText().clear();
                        break;
                    case "loginCom":
                        jsonGetUser();
                        break;
                    case "loginDriverCom":
                        jsonGetDriver();
                        break;
                    case "loginCompanyCom":
                        jsonGetCompany();
                        break;
                    default:

                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }





        }

    }

    private void jsonGetUser() {
        String url = "http://178.128.166.68/getUserInfo.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    assignData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("password", password);

                return params;
            }
        };
        mQueue.add(request);
    }

    private void jsonGetDriver() {
        String url = "http://178.128.166.68/getDriverInfo.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    assignData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("password", password);

                return params;
            }
        };
        mQueue.add(request);
    }

    private void jsonGetCompany() {
        String url = "http://178.128.166.68/getCompanyInfo.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    assignDataCompany(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("password", password);

                return params;
            }
        };
        mQueue.add(request);
    }


    private void assignData(String response) throws JSONException {
        String id = null, fNameGet = null, lNameGet = null, phoneGet = null, noOfTripsGet = null,
                joinedDateGet = null;
        JSONObject userData = new JSONObject(response);
        JSONArray ud = userData.getJSONArray("profileData");

        for (int i = 0; i < ud.length(); i++) {
            JSONObject object = ud.getJSONObject(i);
            id = object.getString("id");
            fNameGet = object.getString("fName");
            lNameGet = object.getString("lName");
            phoneGet = object.getString("phone");
            joinedDateGet = object.getString("joined");
            noOfTripsGet = object.getString("noOfTrips");
        }

        SharedPreferences settings = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id", id);
        editor.putString("phone", phoneGet);
        editor.putString("fName", fNameGet);
        editor.putString("lName", lNameGet);
        editor.putString("joined", joinedDateGet);
        editor.putString("noOfTrips", noOfTripsGet);

        editor.apply();
        finish();
    }


    private void assignDataCompany(String response) throws JSONException {
        String id = null, companyNameGet = null, phoneGet = null, joinedDateGet = null,
                locationNameGet = null;
        JSONObject userData = new JSONObject(response);
        JSONArray ud = userData.getJSONArray("companyData");

        for (int i = 0; i < ud.length(); i++) {
            JSONObject object = ud.getJSONObject(i);
            id = object.getString("id");
            companyNameGet = object.getString("companyName");
            phoneGet = object.getString("phone");
            joinedDateGet = object.getString("joined");
            locationNameGet = object.getString("locationName");
        }

        SharedPreferences settings = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("id", id);
        editor.putString("phone", phoneGet);
        editor.putString("companyName", companyNameGet);
        editor.putString("joined", joinedDateGet);
        editor.putString("locationName", locationNameGet);

        editor.apply();
        finish();
    }



}