package com.sagra.stylemaker_v1.weather;

import android.net.Uri;
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

public class YahooWeatherService {
    private WeatherServiceListener listener;
    private Exception error;
    private String temperatureUnit = "C";

    public YahooWeatherService(WeatherServiceListener listener) {
        this.listener = listener;
    }

    private String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public void refreshWeather(String location) {

        new AsyncTask<String, Void, Channel>() {
            @Override
            protected Channel doInBackground(String[] locations) {

                String location = locations[0];

                Channel channel = new Channel();

                String unit = getTemperatureUnit().equalsIgnoreCase("f") ? "f" : "c";

                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='" + unit + "'", location);

                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));

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

                    JSONObject data = new JSONObject(result.toString());
                    Log.d("saea", result.toString());
                    JSONObject queryResults = data.optJSONObject("query");

                    int count = queryResults.optInt("count");

                    if (count == 0) {
                        error = new LocationWeatherException("No weather information found for " + location);
                        return null;
                    }

                    JSONObject channelJSON = queryResults.optJSONObject("results").optJSONObject("channel");
                    channel.populate(channelJSON);

                    return channel;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Channel channel) {

                if (channel == null && error != null) {
                    listener.serviceFailure(error);
                } else {
                    listener.serviceSuccess(channel);
                }

            }

        }.execute(location);
    }

    private class LocationWeatherException extends Exception {
        LocationWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }

    public interface WeatherServiceListener {
        void serviceSuccess(Channel channel);

        void serviceFailure(Exception exception);
    }


    // Channel
    public class Channel implements GoogleMapsGeocodingService.JSONPopulator {
        private Units units;
        private Units.Item item;
        private String location;

        public Units getUnits() {
            return units;
        }

        public Units.Item getItem() {
            return item;
        }

        public String getLocation() {
            return location;
        }

        @Override
        public void populate(JSONObject data) {

            units = new Units();
            units.populate(data.optJSONObject("units"));

            item = new Units.Item();
            item.populate(data.optJSONObject("item"));

            JSONObject locationData = data.optJSONObject("location");

            String region = locationData.optString("region");
            String country = locationData.optString("country");

          //  location = String.format("%s, %s", locationData.optString("city"), (region.length() != 0 ? region : country));
            location = String.format("%s", locationData.optString("city"));
        }

        @Override
        public JSONObject toJSON() {

            JSONObject data = new JSONObject();

            try {
                data.put("units", units.toJSON());
                data.put("item", item.toJSON());
                data.put("requestLocation", location);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }

    }

    public static class Units implements GoogleMapsGeocodingService.JSONPopulator {
        private String temperature;

        public String getTemperature() {
            return temperature;
        }

        @Override
        public void populate(JSONObject data) {
            temperature = data.optString("temperature");
        }

        @Override
        public JSONObject toJSON() {
            JSONObject data = new JSONObject();

            try {
                data.put("temperature", temperature);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }


        // Item
        public static class Item implements GoogleMapsGeocodingService.JSONPopulator {
            private Condition condition;
            private Condition[] forecast;

            public Condition getCondition() {
                return condition;
            }

            public Condition[] getForecast() {
                return forecast;
            }

            @Override
            public void populate(JSONObject data) {
                condition = new Condition();
                condition.populate(data.optJSONObject("condition"));

                JSONArray forecastData = data.optJSONArray("forecast");

                forecast = new Condition[forecastData.length()];

                for (int i = 0; i < forecastData.length(); i++) {
                    forecast[i] = new Condition();
                    try {
                        forecast[i].populate(forecastData.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public JSONObject toJSON() {
                JSONObject data = new JSONObject();
                try {
                    data.put("condition", condition.toJSON());
                    data.put("forecast", new JSONArray(forecast));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }
        }

    }

    // Condition
    public static class Condition implements GoogleMapsGeocodingService.JSONPopulator {
        private int code;
        private int temperature;
        private int highTemperature;
        private int lowTemperature;
        private String description;
        private String day;

        public int getCode() {
            return code;
        }

        public int getTemperature() {
            return temperature;
        }

        public int getHighTemperature() {
            return highTemperature;
        }

        public int getLowTemperature() {
            return lowTemperature;
        }

        public String getDescription() {
            return description;
        }

        public String getDay() {
            return day;
        }

        @Override
        public void populate(JSONObject data) {
            code = data.optInt("code");
            temperature = data.optInt("temp");
            highTemperature = data.optInt("high");
            lowTemperature = data.optInt("low");
            description = data.optString("text");
            day = data.optString("day");
        }

        @Override
        public JSONObject toJSON() {
            JSONObject data = new JSONObject();

            try {
                data.put("code", code);
                data.put("temp", temperature);
                data.put("high", highTemperature);
                data.put("low", lowTemperature);
                data.put("text", description);
                data.put("day", day);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }
    }






}
