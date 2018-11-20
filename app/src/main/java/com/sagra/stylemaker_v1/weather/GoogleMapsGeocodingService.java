package com.sagra.stylemaker_v1.weather;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class GoogleMapsGeocodingService {


    private GeocodingServiceListener listener;
    private Exception error;

    public GoogleMapsGeocodingService(GeocodingServiceListener listener) {
        this.listener = listener;
    }

    public void refreshLocation(final double latitude, final double longitude) {

        new AsyncTask<Location, Void, LocationResult>() {
            @Override
            protected LocationResult doInBackground(Location... locations) {

                String endpoint = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s",latitude, longitude, API_KEY);

                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();
                    Log.d("saea", result.toString());
                    JSONObject data = new JSONObject(result.toString());

                    JSONArray results = data.optJSONArray("results");

                    if (results.length() == 0) {
                       // error = new ReverseGeolocationException("Could not reverse geocode " + location.getLatitude() + ", " + location.getLongitude());

                        return null;
                    }

                    LocationResult locationResult = new LocationResult();
                    locationResult.populate(results.optJSONObject(0));

                    return locationResult;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(LocationResult location) {

                if (location == null && error != null) {
                    listener.geocodeFailure(error);
                } else {
                    if(location != null){
                        listener.geocodeSuccess(location);
                    }

                }

            }

        }.execute();
    }

    // OPTIONAL: Your Google Maps API KEY
    private static final String API_KEY = "AIzaSyCuFdhyH1_62n4xx66ZGYqP-SZknAB1V44";

    private class ReverseGeolocationException extends Exception {
        public ReverseGeolocationException(String detailMessage) {
            super(detailMessage);
        }
    }



    public interface JSONPopulator {
        void populate(JSONObject data);

        JSONObject toJSON();
    }


    public class LocationResult implements JSONPopulator {

        private String address;

        public String getAddress() {
            return address;
        }

        @Override
        public void populate(JSONObject data) {
            address = data.optString("formatted_address");
        }

        @Override
        public JSONObject toJSON() {
            JSONObject data = new JSONObject();

            try {
                data.put("formatted_address", address);
            } catch (JSONException e) {}

            return data;
        }
    }


    public interface GeocodingServiceListener {
        void geocodeSuccess(LocationResult location);

        void geocodeFailure(Exception exception);
    }

}
