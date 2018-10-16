package com.example.owner.mapDemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.owner.mapDemo.MapsActivity.DEFAULT;
import static java.lang.String.format;


public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private static final int DRIVER_RESULT_INT = 1;
    private static final double DEFAULT_DOUBLE = 0.0;

    //booking values to get from db
    String bookingId, destinationLocation, pickUpPoint, fromLat, fromLng,
            toLat, toLng, timestamp, userFName, userLName,
            userPhone;
    double ridePrice, rideDistance;

    private RequestQueue mQueue;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    Location mLastLocation;

    RelativeLayout relativeLayout;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<Polyline> polyLines;

    Marker markerDestination, markerPickUpPoint;

    private Button completeButton, cancelButton;
    String driverLat, driverLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        setUserData();

        polyLines = new ArrayList<>();
        //tracks the drivers current location in intervals
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        completeButton = findViewById(R.id.completeRoute);
        cancelButton = findViewById(R.id.cancelRoute);

        completeButton.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mQueue = Volley.newRequestQueue(this);

        relativeLayout = findViewById(R.id.driver_relative);


        Switch goOnline = findViewById(R.id.goOnlineButton);
        goOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    connectDriver();
                    getBookingRequestInfo();

                } else {
                    disconnectDriver();
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookingInfo(bookingId);
                Toast.makeText(DriverActivity.this, "You have successfully " +
                                "cancelled the booking, the user shall be notified",
                        Toast.LENGTH_LONG).show();
                clearBookingVariables();
            }
        });

    }

    private void clearBookingVariables() {
        removePolyLinesRoute();
        markerPickUpPoint.remove();
        markerDestination.remove();
        cancelButton.setVisibility(View.INVISIBLE);
        completeButton.setVisibility(View.INVISIBLE);
    }

    private void setUserData() {

        //sets user phone number in the navigation view
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        String name1 = sharedPreferences.getString("fName", DEFAULT);
        String name2 = sharedPreferences.getString("lName", DEFAULT);
        String phone = sharedPreferences.getString("phone", DEFAULT);
        String fullName = name1 + " " + name2;

        // menuName.setText(fullName);
        //  menuPhone.setText(phone);
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
            Intent intent = new Intent(DriverActivity.this, UserLoginActivity.class);
            startActivity(intent);
            removeUserData();
            finish();
        } else if (id == R.id.mnuSettings) {
            Toast.makeText(this, "settings selected", Toast.LENGTH_SHORT).show();
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


    private void connectDriver() {
        checkLocationPermission();
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        mMap.setMyLocationEnabled(true);

    }


    private void disconnectDriver() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }

    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                mLastLocation = location;
                driverLat = String.valueOf(mLastLocation.getLatitude());
                driverLng = String.valueOf(mLastLocation.getLongitude());

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
                                ActivityCompat.requestPermissions(DriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(DriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    private void getRouteToMarker(LatLng pickupLatLng, LatLng destinationLatLng) {


        //drawing a route to booking pick up location
        LatLng driverLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(driverLatLng, destinationLatLng, pickupLatLng)
                .build();
        routing.execute();


    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if (polyLines.size() > 0) {
            for (Polyline poly : polyLines) {
                poly.remove();
            }
        }

        polyLines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polyLines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " +
                            route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void removePolyLinesRoute() {
        for (Polyline line : polyLines) {
            line.remove();
        }
        polyLines.clear();
    }


    public void getBookingRequestInfo() {
        String url = "http://178.128.166.68/getBookingInfoDriver.php";

        //sets user phone number in the navigation view
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        final String driverId = sharedPreferences.getString("id", DEFAULT);


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
                params.put("id", driverId);

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
            destinationLocation = object.getString("fromLocality");
            pickUpPoint = object.getString("toLocality");
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
            Intent intent = new Intent(DriverActivity.this, BookingProcess.class);
            intent.putExtra("driverSent", "driverSent");
            intent.putExtra("destinationLocation", destinationLocation);
            intent.putExtra("pickUpPoint", pickUpPoint);
            intent.putExtra("rideDistance", rideDistance);
            intent.putExtra("ridePrice", ridePrice);
            intent.putExtra("userFName", userFName);
            intent.putExtra("userLName", userLName);
            intent.putExtra("userPhone", userPhone);
            intent.putExtra("bookingId", bookingId);
            startActivityForResult(intent, DRIVER_RESULT_INT);
        } else {
            getBookingRequestInfo();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (DRIVER_RESULT_INT): {
                if (resultCode == Activity.RESULT_OK) {
                    String startRoute = data.getStringExtra("startRoute");
                    String endRoute = data.getStringExtra("endRoute");
                    double fromLat = data.getDoubleExtra("fromLatBooking", DEFAULT_DOUBLE);
                    double fromLng = data.getDoubleExtra("fromLngBooking", DEFAULT_DOUBLE);
                    double toLat = data.getDoubleExtra("toLatBooking", DEFAULT_DOUBLE);
                    double toLng = data.getDoubleExtra("toLngBooking", DEFAULT_DOUBLE);


                    setMarkerDestination(endRoute, fromLat, fromLng);
                    setMarkerPickUpPoint(startRoute, toLat, toLng);
                    getRouteToMarker(getLocationLatLng(startRoute), getLocationLatLng(endRoute));
                    sendDriverLocationToUser(bookingId, driverLat, driverLng);
                    completeButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);


                }
                break;
            }
        }
    }


    private LatLng getLocationLatLng(String location) {
        Geocoder geo = new Geocoder(DriverActivity.this);
        List<Address> pickUpPointList = null;
        try {
            pickUpPointList = geo.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address fromAddress = pickUpPointList.get(0);

        LatLng ll = (new LatLng(fromAddress.getLatitude(), fromAddress.getLongitude()));

        return ll;

    }

    //marker settings for user defined destination
    private void setMarkerDestination(String locality, double lat, double lng) {
        //if a previous marker exists this function removes it
        if (markerDestination != null) {
            markerDestination.remove();
        }

        //sets marker on locality
        MarkerOptions option = new MarkerOptions()
                .title(locality)
                .draggable(true)
                ///icon(BitmapDescriptorFactory.fromResource(R.minimap.ic_launcher))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(lat, lng))
                .snippet("is this what you were looking for?");
        markerDestination = mMap.addMarker(option);
    }

    //sets marker for user pick up point
    private void setMarkerPickUpPoint(String locality, double lat, double lng) {
        //if a previous marker exists this function removes it
        if (markerPickUpPoint != null) {
            markerPickUpPoint.remove();
        }

        //sets marker on locality
        MarkerOptions option = new MarkerOptions()
                .title(locality)
                .draggable(true)
                ///icon(BitmapDescriptorFactory.fromResource(R.minimap.ic_launcher))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(lat, lng));
        markerPickUpPoint = mMap.addMarker(option);

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


    }

    private void sendDriverLocationToUser(final String bookingId, final String driverLat,
                                          final String driverLng) {
        String url = "http://178.128.166.68/driverLocation.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverActivity.this, "location sent",
                                Toast.LENGTH_LONG).show();
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
                params.put("driverLat", driverLat);
                params.put("driverLng", driverLng);

                return params;
            }
        };
        mQueue.add(postRequest);
    }


}

