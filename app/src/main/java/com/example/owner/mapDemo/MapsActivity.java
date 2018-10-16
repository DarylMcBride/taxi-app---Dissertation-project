package com.example.owner.mapDemo;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String DEFAULT = "N/A";
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private static final int BOOKING_TRACK_INT = 123;

    private float rideDistance;
    private double ridePrice;
    private String bookingId;
    private double driverLat;
    private double driverLng;

    private DrawerLayout drawer;
    private RequestQueue mQueue;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Geocoder geo;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    Marker markerDestination, markerPickUpPoint;
    private List<Polyline> polyLines;


    EditText destinationEditText, fromPositionEditText;
    TextView menuPhone, menuName;
    private Button searchBooking, sendBooking;
    Marker driverLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets view to user maps, then inflates toold bar and side navigation view
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        View headerView = navigationView.getHeaderView(0);


        //setting variables in the sidebar
        menuPhone = headerView.findViewById(R.id.nav_phone);
        menuName = headerView.findViewById(R.id.nav_name);

        setUserData();


        //setting up parameters to add routing lines to markers
        polyLines = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (googleServiceAvail()) {


            //initialise map if google service is available
            initialiseMap();
        } else {
            //no googlemap layout
        }

        mQueue = Volley.newRequestQueue(this);
        //gets the information typed in by user in the destination field
        destinationEditText = findViewById(R.id.editTextTo);
        //gets the information typed in by the user in the pick up point field
        fromPositionEditText = findViewById(R.id.editTextFrom);

        sendBooking = findViewById(R.id.sendLocationsButton);
        sendBooking.setVisibility(View.INVISIBLE);


        searchBooking = findViewById(R.id.sendButton);


        searchBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the information the user previously typed in
                String destinationLocation, pickUpPoint;
                destinationLocation = destinationEditText.getText().toString();
                pickUpPoint = fromPositionEditText.getText().toString();

                //using geo coder to store the address value in a list and then returns the latitude and longitude
                geo = new Geocoder(MapsActivity.this);
                List<Address> pickUpPointList = null;
                try {
                    pickUpPointList = geo.getFromLocationName(pickUpPoint, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (pickUpPointList != null) {
                    Address fromAddress = pickUpPointList.get(0);
                    String pickUpPointLocality = fromAddress.getLocality();
                    //gets locality, lat and long for the location
                    double fromLat = fromAddress.getLatitude();
                    double fromLng = fromAddress.getLongitude();
                    //sets marker
                    setMarkerPickUpPoint(pickUpPointLocality, fromLat, fromLng);
                } else {
                    Toast.makeText(MapsActivity.this, "Please enter a valid pickUpPoint", Toast.LENGTH_LONG).show();
                }

                List<Address> destinationList = null;
                try {
                    destinationList = geo.getFromLocationName(destinationLocation, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (destinationList != null) {
                    Address toAddress = destinationList.get(0);
                    String destinationLocality = toAddress.getLocality();
                    double toLat = toAddress.getLatitude();
                    double toLng = toAddress.getLongitude();
                    goToLocationZoom(toLat, toLng, 10);
                    setMarkerDestination(destinationLocality, toLat, toLng);

                } else {
                    Toast.makeText(MapsActivity.this, "Please enter a valid destination", Toast.LENGTH_LONG).show();
                }

                if (markerDestination != null && markerPickUpPoint != null) {
                    sendBooking.setVisibility(View.VISIBLE);
                    getRouteToMarker(getLocationLatLng(pickUpPoint), getLocationLatLng(destinationLocation));
                }
            }

        });


        sendBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String destinationLocation, pickUpPoint;
                destinationLocation = destinationEditText.getText().toString();
                pickUpPoint = fromPositionEditText.getText().toString();

                DecimalFormat df = new DecimalFormat("#.00");
                double rideDistanceFormatted = Double.valueOf(df.format(rideDistance));
                double ridePriceFormatted = Double.parseDouble(df.format(ridePrice));

                Intent intent = new Intent(MapsActivity.this, BookingProcess.class);
                intent.putExtra("userSent", "userSent");
                intent.putExtra("destinationLocation", destinationLocation);
                intent.putExtra("pickUpPoint", pickUpPoint);
                intent.putExtra("rideDistance", rideDistanceFormatted);
                intent.putExtra("ridePrice", ridePriceFormatted);
                startActivityForResult(intent, BOOKING_TRACK_INT);
            }
        });
    }


    private void setUserData() {

        //sets user phone number in the navigation view
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        String name1 = sharedPreferences.getString("fName", DEFAULT);
        String name2 = sharedPreferences.getString("lName", DEFAULT);
        String phone = sharedPreferences.getString("phone", DEFAULT);
        String fullName = name1 + " " + name2;

        menuName.setText(fullName);
        menuPhone.setText(phone);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_profile_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).addToBackStack("userMap").commit();
                break;
            case R.id.nav_payment_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PaymentFragment()).addToBackStack("userMap").commit();
                break;
            case R.id.nav_lift_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BookingHistoryFragment()).addToBackStack("userMap").commit();
                break;
            case R.id.nav_favourite_company:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FavouriteCompanyFragment()).addToBackStack("userMap").commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
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
            Intent intent = new Intent(MapsActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
            removeUserData();
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


    private LatLng getLocationLatLng(String location) {
        geo = new Geocoder(MapsActivity.this);
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

    //empties variables to prevent repeated bookings
    private void postBookingClearVariables() {
        markerPickUpPoint.remove();
        markerDestination.remove();
        destinationEditText.setText("");
        fromPositionEditText.setText("");
        sendBooking.setVisibility(View.INVISIBLE);
    }


    private void initialiseMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //method to check that google service is available
    public boolean googleServiceAvail() {
        GoogleApiAvailability gapi = GoogleApiAvailability.getInstance();
        int isAvailable = gapi.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (gapi.isUserResolvableError(isAvailable)) {
            Dialog dialog = gapi.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Error connection", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //instantiate map if previous checks are positive
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

        //invokes custom info window if user has searched for locations
        infoWindowSet();
    }


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
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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


    //moves camera to location with zoom
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);

    }


    private void getRouteToMarker(LatLng pickupLatLng, LatLng destinationLatLng) {
        if (markerDestination != null && markerPickUpPoint != null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(destinationLatLng, pickupLatLng)
                    .build();
            routing.execute();


        }
    }

    private void infoWindowSet() {
        if (mMap != null) {
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                //updating custom window when marker is dragged
                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(MapsActivity.this);
                    LatLng latLng = marker.getPosition();
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //changes original text in location info window
                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();
                }
            });

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                //setting variables in info window to the information added by the useron
                @Override
                public View getInfoContents(Marker marker) {


                    //adding location info window
                    View v = getLayoutInflater().inflate(R.layout.location_info_window, null);
                    //assigning variables
                    TextView tvLocality = v.findViewById(R.id.tv_locality);
                    TextView tvLat = v.findViewById(R.id.tv_lat);
                    TextView tvLng = v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = v.findViewById(R.id.tv_snippet);


                    //sets text view information to info typed in by user
                    LatLng latlng = marker.getPosition();
                    //title taken from locality
                    tvLocality.setText(marker.getTitle());
                    //latitude and longitude assigned
                    tvLat.setText("Latitude: " + latlng.latitude);
                    tvLng.setText("Longitude: " + latlng.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });

        }

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
                .position(new LatLng(lat, lng))
                .snippet("is this what you were looking for?");
        markerPickUpPoint = mMap.addMarker(option);

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

            //getting ride distance provided by polyines and converting it into kilometres

            rideDistance = route.get(i).getDistanceValue() / 1000;

            ridePrice = Double.valueOf(rideDistance) * 0.8;

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (BOOKING_TRACK_INT): {
                if (resultCode == BOOKING_TRACK_INT) {
                    String fromLocationTracked = data.getStringExtra("pickUpPoint");
                    String toLocationTracked = data.getStringExtra("destinationLocation");
                    trackBooking(fromLocationTracked, toLocationTracked);
                }
                break;
            }

        }
    }

    private void trackBooking(final String fromLocationTracked, final String toLocationTracked) {

        String url = "http://178.128.166.68/checkForBooking.php";
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        final String userIdTracked = sharedPreferences.getString("id", DEFAULT);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Success")) {
                            trackBooking(fromLocationTracked, toLocationTracked);
                        } else if (response.contains("accepted")) {
                            Toast.makeText(MapsActivity.this, "your booking has been" +
                                            "accepted by the company, your driver is on route.",
                                    Toast.LENGTH_LONG).show();
                                    getDriverLocation();
                        } else if (response.contains("gone")) {
                            Toast.makeText(MapsActivity.this, "your booking has been " +
                                            "declined by the company.",
                                    Toast.LENGTH_LONG).show();
                        }
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
                params.put("fromLocation", fromLocationTracked);
                params.put("toLocation", toLocationTracked);
                params.put("userId", userIdTracked);


                return params;
            }

        };

        mQueue.add(request);


    }

    private void getDriverLocation() {
        String url = "http://178.128.166.68/getDriverLocation.php";
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        final String userId = sharedPreferences.getString("id", DEFAULT);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //parse the json object response to assign variables
                            parseDriverData(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("userId", userId);


                return params;
            }
        };
        mQueue.add(postRequest);
    }


    public void parseDriverData(String response) throws JSONException {


        JSONObject userData = new JSONObject(response);
        JSONArray ud = userData.getJSONArray("driverLocation");

        for (int i = 0; i < ud.length(); i++) {
            JSONObject object = ud.getJSONObject(i);
            bookingId = object.getString("id");
            driverLat = object.getDouble("driverLat");
            driverLng = object.getDouble("driverLng");
        }

            //if driver lat/lng is 0.0 then the driver has not routed the booking yet.
            if (driverLat != 0.0 || driverLng != 0.0) {
                setDriverLocation(driverLat, driverLng);
            }
            //recalling the method to refresh the drivers location
            getDriverLocation();
        }

    private void setDriverLocation(Double driverLat, Double driverLng) {
        if (driverLocationMarker != null) {
            driverLocationMarker.remove();
        }


        //sets marker on locality
        MarkerOptions option = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(new LatLng(driverLat, driverLng));
        driverLocationMarker = mMap.addMarker(option);
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
}
