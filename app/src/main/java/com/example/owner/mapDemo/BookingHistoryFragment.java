package com.example.owner.mapDemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BookingHistoryFragment extends Fragment {

    String parsed;
    private TextView bookingHistoryTV;
    private RequestQueue mQueue;
    ImageButton backButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_booking_history, container, false);
        bookingHistoryTV = view.findViewById(R.id.bookingHistoryList);

        mQueue = Volley.newRequestQueue(getActivity());
        jsonParse();


        backButton = view.findViewById(R.id.backToMapImgButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), MapsActivity.class);
                startActivity(in);
            }
        });
        return view;
    }

    private void jsonParse() {
        String url = "http://178.128.166.68/getBookingHistory.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("bookingHistory");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject bookingHistory = jsonArray.getJSONObject(i);

                                String fromLocality =
                                        bookingHistory.getString("fromLocality");
                                String toLocality = bookingHistory.getString("toLocality");
                                double cost = bookingHistory.getDouble("cost");
                                String date = bookingHistory.getString("date");

                                parsed = fromLocality + " | " + toLocality + " | " + String.valueOf(cost) + " | " + date + "\n\n";
                            }

                            bookingHistoryTV.append(parsed); //setText outside of For Loop

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}