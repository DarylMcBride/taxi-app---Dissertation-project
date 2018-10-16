package com.example.owner.mapDemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
    AlertDialog alertDialog;

    BackgroundWorker(Context ctx) {

        context = ctx;
    }

    public static HttpURLConnection httpURLConnection = null;


    @Override
    protected String doInBackground(String... params) {
        //setting type params for if statement, setting url variables for if statement
        String type = params[0];
        String loginUrl = "http://178.128.166.68/login.php";
        String regUrl = "http://178.128.166.68/register.php";
        String sendBookingUrl = "http://178.128.166.68/insertBooking.php";
        if (type.equals("login")) {
            try {
                //setting string values to the params passed from previous code
                String phone = params[1];
                String password = params[2];
                //setting the url
                URL url = new URL(loginUrl);
                //opening a connection and input and output data
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                //writing the post data and passing it to the server
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("phone") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                //reading the response from the server
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result = line;
                }
                //closing off the resources and disconnecting the http connection
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            all further code within the if statement more or less provides the same function as the beginning
            statement with slightly different parameters
             */

        } else if (type.equals("register")) {
            try {
                String fName = params[1];
                String lName = params[2];
                String phone = params[3];
                String password = params[4];
                String joined = params[5];


                URL url = new URL(regUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("fName") + "=" + URLEncoder.encode(fName, "UTF-8") + "&"
                        + URLEncoder.encode("lName", "UTF-8") + "=" + URLEncoder.encode(lName, "UTF-8") + "&"
                        + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                        + URLEncoder.encode("joined", "UTF-8") + "=" + URLEncoder.encode(joined, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result = line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("sendBooking")) {


            try {
                String fromLocality = params[1];
                String toLocality = params[2];
                String fromLat = params[3];
                String fromLng = params[4];
                String toLng = params[5];
                String toLat = params[6];
                String dateTime = params[7];
                double ridePrice = Double.parseDouble(params[8]);
                double rideDistance = Double.parseDouble(params[9]);
                String userid = params[10];



                URL url = new URL(sendBookingUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("fromLocality", "UTF-8") + "=" + URLEncoder.encode(fromLocality, "UTF-8") + "&"
                        + URLEncoder.encode("toLocality", "UTF-8") + "=" + URLEncoder.encode(toLocality, "UTF-8") + "&"
                        + URLEncoder.encode("fromLat", "UTF-8") + "=" + URLEncoder.encode(fromLat, "UTF-8") + "&"
                        + URLEncoder.encode("fromLng", "UTF-8") + "=" + URLEncoder.encode(fromLng, "UTF-8") + "&"
                        + URLEncoder.encode("toLng", "UTF-8") + "=" + URLEncoder.encode(toLng, "UTF-8") + "&"
                        + URLEncoder.encode("toLat", "UTF-8") + "=" + URLEncoder.encode(toLat, "UTF-8") + "&"
                        + URLEncoder.encode("dateTime", "UTF-8") + "=" + URLEncoder.encode(dateTime, "UTF-8") + "&"
                        + URLEncoder.encode("ridePrice", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(ridePrice), "UTF-8") + "&"
                        + URLEncoder.encode("rideDistance", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(rideDistance), "UTF-8") + "&"
                        + URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result = line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return null;

    }


    //setting up alert dialog for errors
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Status");
    }

    //giving the class a command based on the servers response
    @Override
    protected void onPostExecute(String result) {
        if (result.contentEquals("loginCom")) {
            Intent intent = new Intent(context, MapsActivity.class);
            context.startActivity(intent);
        } else if (result.contentEquals("loginDriverCom")) {
            Intent intent = new Intent(context, DriverActivity.class);
            context.startActivity(intent);

        } else if (result.contentEquals("loginCompanyCom")) {
            Intent intent = new Intent(context, CompanyActivity.class);
            context.startActivity(intent);
        } else if (result.contentEquals("register success")) {
            Intent intent = new Intent(context, UserLoginActivity.class);
            context.startActivity(intent);
        } else if (result.contentEquals("successfully booked")) {

        }


    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
