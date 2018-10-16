package com.example.owner.mapDemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.owner.mapDemo.BackgroundWorker.httpURLConnection;

public class ProfileFragment extends Fragment {


    private static final String DEFAULT = "N/A";
    private TextView fName, joinedDate, noOfTrips, lName, phoneTV;
    private RequestQueue mQueue;
    String fNameGet, lNameGet, joinedDateGet, noOfTripsGet, phoneGet;
    ImageButton backButton;
    View view;
    String activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //sets view to profile fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //volley library request queue, parsing getActivity because 'this' value does not work within
        //fragments
        mQueue = Volley.newRequestQueue(getActivity());

        //setting the views i wish to edit within profile
        fName = view.findViewById(R.id.userProfileFNameTV);
        joinedDate = view.findViewById(R.id.userAccountJoinedTV);
        noOfTrips = view.findViewById(R.id.userAccountNumOfTripsTV);
        phoneTV = view.findViewById(R.id.userAccountPhoneTV);
        setUserData();
        //get profile data from parse method
        //jsonParse();
        //implementing button to return to maps activity
        backButton = view.findViewById(R.id.backToMapImgButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), MapsActivity.class);
                startActivity(in);
            }
        });

        return view;
    }



    private void setUserData() {
        String fullName;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myData",
                Context.MODE_PRIVATE);
        //getting the shared pref user details
        fNameGet = sharedPreferences.getString("fName", DEFAULT);
        lNameGet = sharedPreferences.getString("lName", DEFAULT);
        joinedDateGet = sharedPreferences.getString("joined", DEFAULT);
        phoneGet = sharedPreferences.getString("phone", DEFAULT);
        noOfTripsGet = sharedPreferences.getString("noOfTrips", DEFAULT);

        fullName = fNameGet+ "  " + lNameGet;
        //setting the user detaisl in a text view
        fName.setText(fullName);
        joinedDate.setText(joinedDateGet);
        phoneTV.setText(phoneGet);
        noOfTrips.setText(noOfTripsGet);
    }
}