package com.example.owner.mapDemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class CompanyActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String DEFAULT = "N/A";


    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    //return values from previous booking process class
    private static final int COMPANY_DECLINE_INT = 1;
    private static final int COMPANY_SELECT_DRIVER_INT = 2;



    String bookingId, pickUpPoint, destinationLocation, fromLat, fromLng,
            toLat, toLng, timestamp, userFName, userLName,
            userPhone;
    double ridePrice, rideDistance;

    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    Location mLastLocation;


    private RequestQueue mQueue;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Switch goOnline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_map);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        goOnline = findViewById(R.id.goOnlineButton);
        goOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    connectCompany();


                } else {
                    disconnectCompany();
                }

            }
        });


    }


    private void connectCompany() {
        checkLocationPermission();
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);


        getBookingRequestInfo();
    }

    private void disconnectCompany() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commonmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnuLogout) {
            Intent intent = new Intent(CompanyActivity.this, UserLoginActivity.class);
            startActivity(intent);
            removeUserData();
            finish();
        } else if (id == R.id.mnuSettings) {
            Toast.makeText(this, "logout selected", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    private void removeUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }


    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        }
    };

    //checks for location permission based on android version
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CompanyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(CompanyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide permission", Toast.LENGTH_LONG).show();
                }
                break;

            }
        }
    }


    public void getBookingRequestInfo() {
        String url = "http://178.128.166.68/getBookingInfo.php";

        //sets user phone number in the navigation view
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        final String companyId = sharedPreferences.getString("id", DEFAULT);


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    parseBookingData(response);
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
                params.put("id", companyId);

                return params;
            }
        };
        mQueue.add(request);
    }

    public void parseBookingData(String response) throws JSONException {


        JSONObject userData = new JSONObject(response);
        JSONArray ud = userData.getJSONArray("bookingRequests");

        for (int i = 0; i < ud.length(); i++) {
            JSONObject object = ud.getJSONObject(i);
            bookingId = object.getString("id");
            pickUpPoint = object.getString("fromLocality");
            destinationLocation = object.getString("toLocality");
            fromLat = object.getString("fromLat");
            fromLng = object.getString("fromLng");
            toLat = object.getString("toLat");
            toLng = object.getString("toLng");
            timestamp = object.getString("timestamp");
            userFName = object.getString("fName");
            userLName = object.getString("lName");
            userPhone = object.getString("phone");
            ridePrice = object.getDouble("price");
            rideDistance = object.getDouble("distance");
        }

        if (destinationLocation != null) {
            Intent intent = new Intent(CompanyActivity.this, BookingProcess.class);
            intent.putExtra("companySent", "companySent");
            intent.putExtra("destinationLocation", destinationLocation);
            intent.putExtra("pickUpPoint", pickUpPoint);
            intent.putExtra("rideDistance", rideDistance);
            intent.putExtra("ridePrice", ridePrice);
            intent.putExtra("userFName", userFName);
            intent.putExtra("userLName", userLName);
            intent.putExtra("userPhone", userPhone);
            intent.putExtra("bookingId", bookingId);
            startActivityForResult(intent, COMPANY_DECLINE_INT);
        } else {
            getBookingRequestInfo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (COMPANY_DECLINE_INT): {
                if (resultCode == COMPANY_DECLINE_INT) {
                    Toast.makeText(CompanyActivity.this, "You have successfully " +
                                    "declined the booking, the user shall be notified",
                            Toast.LENGTH_LONG).show();

                } else if (resultCode == COMPANY_SELECT_DRIVER_INT) {
                    String acceptedBookingID = data.getStringExtra("bookingId");
                    String driverName = data.getStringExtra("driverName");
                    acceptBookingProcess(acceptedBookingID, driverName);
                }

                break;
            }

        }
    }

    private void acceptBookingProcess(final String acceptedBookingID, final String driverName) {

        //updates the status in booking info from pending to accepted, th makes it viewable by the
        //assigned driver
        String url = "http://178.128.166.68/acceptBooking.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CompanyActivity.this, response, Toast.LENGTH_LONG).show();
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
                params.put("bookingId", acceptedBookingID);
                params.put("driverName", driverName);


                return params;
            }
        };
        mQueue.add(postRequest);
    }





}

