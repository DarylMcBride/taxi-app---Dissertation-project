package com.example.owner.mapDemo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataRetriever extends AsyncTask<String, Void, JSONArray> {

    private String fromLocality, toLocality, fromLat, fromLng, toLat, toLng, fName, lName, phone;
    private JSONArray jA = null;
    private String result;
    String jsonURL;
    @Override
    protected void onPreExecute() {
       jsonURL = "http://178.128.166.68/getBookingInfo.php";
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        String type = params[0];
        if (type.equals("getBookingInfo")) {
            try {
                String getBookingInfoURL = "http://178.128.166.68/getBookingInfo.php";
                URL url = new URL(getBookingInfoURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result = line;
                }
                try {
                    jA = new JSONArray(result);
                } catch (JSONException e) {
                    Log.e("JSON - 3 -", e.toString());
                    return null;
                }

            } catch (Exception e) {
                return null;
            }

        }
        return jA;
    }


    @Override
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);


        for (int i = 0; i < result.length(); i++) {
            try {
                JSONObject JO = (JSONObject) result.get(i);

                    fromLocality = JO.getString("fromLocality");
                    toLocality = JO.getString("fromLocality");
                    fromLat = JO.getString("fromLat");
                    fromLng = JO.getString("fromLng");
                    toLat = JO.getString("toLat");
                    toLng = JO.getString("toLng");
                    fName = JO.getString("fName");
                    lName = JO.getString("lName");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



}




