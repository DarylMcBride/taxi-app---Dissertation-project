package com.example.owner.mapDemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.owner.mapDemo.MapsActivity.DEFAULT;

public class BookingProcess extends AppCompatActivity {

    private static final int COMPANY_RESULT_INT = 12;
    private static final int COMPANY_SELECT_DRIVER_INT = 2;
    private static final int BOOKING_TRACK_INT = 123;

    public String destinationLocation;
    public String pickUpPoint;
    String driverNameSend;
    public double ridePrice;
    public double rideDistance;
    Button confirmButton, declineButton;
    TextView fromBookingInfo, toBookingInfo, rideDistanceTV, ridePriceTV, selectDriverTV;
    Spinner selectDriverSpinner;
    ArrayList<String> driverName, driverIdArray;

    RequestQueue mQueue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));


        setContentView(R.layout.booking_info);
        final Bundle bundle = getIntent().getExtras();
        final Intent intent = getIntent();

        selectDriverTV = findViewById(R.id.selectdriverTV);
        selectDriverSpinner = findViewById(R.id.selectDriverSpinner);
        selectDriverTV.setVisibility(View.INVISIBLE);
        selectDriverSpinner.setVisibility(View.INVISIBLE);

        destinationLocation = bundle.getString("destinationLocation");
        pickUpPoint = bundle.getString("pickUpPoint");
        rideDistance = bundle.getDouble("rideDistance");
        ridePrice = bundle.getDouble("ridePrice");

        rideDistanceTV = findViewById(R.id.distanceTVBookingInfo);
        ridePriceTV = findViewById(R.id.priceTVBookingInfo);
        fromBookingInfo = findViewById(R.id.fromBookingInfo);
        toBookingInfo = findViewById(R.id.toBookingInfo);

        fromBookingInfo.setText(destinationLocation);
        toBookingInfo.setText(pickUpPoint);
        ridePriceTV.setText("£" + String.valueOf(ridePrice));
        rideDistanceTV.setText(String.valueOf(rideDistance) + "  Km");

        declineButton = findViewById(R.id.declineBookingButton);
        declineButton.setVisibility(View.INVISIBLE);
        confirmButton = findViewById(R.id.confirmYourBooking);


        mQueue = Volley.newRequestQueue(this);
        if (intent.hasExtra("userSent")) {

            confirmButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String type = "sendBooking";
                    long date = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    String dateString = sdf.format(date);

    /*
    getting latitude and lognitude data for the booking (entered by user)
    data converted to string for database entry
     */
                    double fromLatDouble = getLocationLat(pickUpPoint);
                    String fromLat = Double.valueOf(fromLatDouble).toString();
                    double fromLngDouble = getLocationLng(pickUpPoint);
                    String fromLng = Double.valueOf(fromLngDouble).toString();
                    double toLatDouble = getLocationLat(destinationLocation);
                    String toLat = Double.valueOf(toLatDouble).toString();
                    double toLngDouble = getLocationLng(destinationLocation);
                    String toLng = Double.valueOf(toLngDouble).toString();

                    SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
                    String userid = sharedPreferences.getString("id", DEFAULT);

                    String rideDistance = String.valueOf(bundle.getDouble("rideDistance"));
                    String ridePrice = String.valueOf(bundle.getDouble("ridePrice"));

                    BackgroundWorker backgroundWorker = new BackgroundWorker(BookingProcess.this);
                    try {
                        backgroundWorker.execute(type, pickUpPoint, destinationLocation, fromLat,
                                fromLng, toLat, toLng, dateString, ridePrice,
                                rideDistance, userid).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //returns int to alert maps activity to start tracking the booking
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("pickUpPoint", pickUpPoint);
                    resultIntent.putExtra("destinationLocation", destinationLocation);
                    setResult(BOOKING_TRACK_INT, resultIntent);
                    finish();


                }
            });

        } else if (intent.hasExtra("companySent")) {
            //customises pop up for company view
            String buttonEdit = "Confirm";

            loadDriverList();
            selectDriverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   driverNameSend = selectDriverSpinner.getItemAtPosition(selectDriverSpinner.
                            getSelectedItemPosition()).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            selectDriverSpinner.setVisibility(View.VISIBLE);
            selectDriverTV.setVisibility(View.VISIBLE);

            driverName = new ArrayList<>();
            final String bookingId = bundle.getString("bookingId", DEFAULT);

            declineButton.setVisibility(View.VISIBLE);

            confirmButton.setText(buttonEdit);

            declineButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateBookingInfo(bookingId);
                    finish();
                }
            });

            confirmButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("bookingId", bookingId);
                    resultIntent.putExtra("driverName", driverNameSend);
                    setResult(COMPANY_SELECT_DRIVER_INT, resultIntent);
                    finish();

                }

            });
        } else if (intent.hasExtra("driverSent")) {
            String buttonEdit = "Confirm";

            confirmButton.setText(buttonEdit);


            confirmButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    double fromLatDouble = getLocationLat(pickUpPoint);
                    double fromLngDouble = getLocationLng(pickUpPoint);
                    double toLngDouble = getLocationLng(destinationLocation);
                    double toLatDouble = getLocationLat(destinationLocation);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("startRoute", destinationLocation);
                    resultIntent.putExtra("endRoute", pickUpPoint);
                    resultIntent.putExtra("fromLatBooking", fromLatDouble);
                    resultIntent.putExtra("fromLngBooking", fromLngDouble);
                    resultIntent.putExtra("toLatBooking", toLatDouble);
                    resultIntent.putExtra("toLngBooking", toLngDouble);
                    resultIntent.putExtra("driverName", driverName);

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();

                }

            });
        }
    }

    private double getLocationLat(String location) {
        Geocoder geo = new Geocoder(BookingProcess.this);
        List<Address> pickUpPointList = null;
        try {
            pickUpPointList = geo.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address fromAddress = pickUpPointList.get(0);
        fromAddress.getLatitude();

        return fromAddress.getLatitude();
    }

    private double getLocationLng(String location) {

        Geocoder geo = new Geocoder(BookingProcess.this);
        List<Address> pickUpPointList = null;
        try {
            pickUpPointList = geo.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address fromAddress = pickUpPointList.get(0);
        fromAddress.getLongitude();

        return fromAddress.getLongitude();
    }

    private void updateBookingInfo(final String bookingId) {
        String url = "http://178.128.166.68/updateBookingInfo.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bookingId", bookingId);


                return params;
            }
        };
        mQueue.add(postRequest);
        Intent resultIntent = new Intent();
        setResult(COMPANY_RESULT_INT, resultIntent);
    }

    private void loadDriverList() {
        String url = "http://178.128.166.68/getDriverForBooking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("driverInfo");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String fName = jsonObject1.getString("fName");
                        driverName.add(fName);


                    }
                    selectDriverSpinner.setAdapter(new ArrayAdapter<String>(
                            BookingProcess.this,
                            android.R.layout.simple_spinner_dropdown_item, driverName));
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

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mQueue.add(stringRequest);
    }

}


