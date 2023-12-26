package com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers;

import android.os.AsyncTask;
import android.util.Log;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class MapsHelper {
    public static void getRoute(LatLng origin, LatLng destination, Function<String, Void> callback) {
        String originString = origin.latitude + "," + origin.longitude;
        String destinationString = destination.latitude + "," + destination.longitude;

        HttpURLConnection connection = null;
        String url = "https://maps.googleapis.com/maps/api/directions/json?mode=walking&origin=" + originString + "&destination=" + destinationString + "&key=AIzaSyANKhHM9Oj8249bwzxrK7FQcxSDRaRfPSI";
        System.out.println(url);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonString = new JsonTask().execute(url).get();
                    JSONObject json = null;
                    try {
                        json = new JSONObject(jsonString);
                        String polyline = json.getJSONArray("routes")
                                .getJSONObject(0)
                                .getJSONObject("overview_polyline")
                                .getString("points");
                        callback.apply(polyline);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private static class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
