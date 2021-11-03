package com.noamkeisy.taskno2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
import java.util.ArrayList;

public class WeatherData extends AsyncTask<Void, Void, Void> {
    final String WHEATHER_SERVICE_LINK  = "https://api.openweathermap.org/data/2.5/weather?id=2172797&APPID=5bda833ea98063658162aac5ac577075&units=metric&";

    private boolean loading = true;
    private Double latitude;
    private Double longitude;
    private WeatherStruct weatherStruct = new WeatherStruct();


    public WeatherData() {
    }

    public WeatherData(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String data ="";
        try {
            URL url = new URL(WHEATHER_SERVICE_LINK + "lat=" + latitude + "&lon=" + longitude );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONObject rootObject = new JSONObject(data);
            JSONArray weatherArr  = rootObject.getJSONArray("weather");
            if(weatherArr.length()>0) {
                JSONObject bestResult = weatherArr.getJSONObject(0);
                weatherStruct.setDescription(bestResult.getString("description"));
                weatherStruct.setDayNightIcon(bestResult.getString("icon"));
            }
            JSONObject mainObject = rootObject.getJSONObject("main");
            weatherStruct.setTemp(mainObject.getInt("temp"));
            weatherStruct.setTempMan(mainObject.getInt("temp_max"));
            weatherStruct.setTempMin(mainObject.getInt("temp_min"));
            weatherStruct.setCity(rootObject.getString("name"));

            loading = false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //MainActivity.coordinateTv.setText(this.dataParsed);
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public WeatherStruct getWeatherStruct() {
        return weatherStruct;
    }

    public boolean isLoading() {
        return this.loading;
    }



}
