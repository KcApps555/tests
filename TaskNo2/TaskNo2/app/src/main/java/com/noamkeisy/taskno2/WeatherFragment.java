package com.noamkeisy.taskno2;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.victor.loading.rotate.RotateLoading;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    final int LOCATION_PERMISSION_REQUEST = 1;
    final String WEARHER_FRAGMENT_TAG = "weather_fragment";

    RelativeLayout rootRelativeLayout;

    TextView clock;
    TextView date;
    TextView refresh_time;
    ImageButton refresh;
    TextView temp;
    TextView description_tv;
    ImageView description_iv;
    TextView city;
    TextView temp_range;
    String dayNight;

    Calendar calendar = Calendar.getInstance();
    FusedLocationProviderClient client;
    private FusedLocationProviderClient fusedLocationClient;

    double latitude, longitude;
    boolean isFinishLocation = false;
    Handler handler = new Handler();

    LocationCallback callback;
    WeatherData process;
    RotateLoading rotateLoading, refresh_rotateLoading;
    ImageView weatherBehindIv;
    TextView note;
    Boolean isPermissionOk = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        //SETTING//
        clock = rootView.findViewById(R.id.time);
        date = rootView.findViewById(R.id.date);
        refresh_time = rootView.findViewById(R.id.refresh_time);
        refresh = rootView.findViewById(R.id.refresh_ib);
        temp = rootView.findViewById(R.id.temp);
        description_tv = rootView.findViewById(R.id.description_tv);
        description_iv = rootView.findViewById(R.id.description_iv);
        city = rootView.findViewById(R.id.city_location);
        temp_range = rootView.findViewById(R.id.temp_range);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        rootRelativeLayout = rootView.findViewById(R.id.root_relativelayout);
        rotateLoading = rootView.findViewById(R.id.rotateloading);
        weatherBehindIv = rootView.findViewById(R.id.weather_behind);
        refresh_rotateLoading = rootView.findViewById(R.id.refresh_rotateloading);
        //rotateLoading.start();
        //refresh_rotateLoading.stop();
        refresh.setVisibility(View.VISIBLE);
        note = rootView.findViewById(R.id.note);
        //////////

        //Permission//
        if(Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST);
            }
            else getLocation();
        }
        else getLocation();
        /////////////

        /*if(isLocationEnabled(getContext())) {
            //Toast.makeText(getContext(), "GPS ON", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(getContext(), "GPS OFF", Toast.LENGTH_SHORT).show();
            if(isPermissionOk) {
                rotateLoading.stop();
                note.setText("אנא הפעל חיישן מיקום");
                note.setVisibility(View.VISIBLE);
            }
        }*/

        ///////////////////////////////////////

        //JSON//
        //call in function "getLocation"
        ///////

        //Seting Btn XML//
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23) {
                    int hasLocationPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if(hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST);
                    }
                    else getLocation();
                }
                else getLocation();
            }
        });
        //////////////////

        return rootView;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //NOTE TextView//
                rotateLoading.stop();
                refresh_rotateLoading.stop();
                note.setText("נדרשת הרשאת מיקום על מנת לצפות במזג אוויר");
                note.setVisibility(View.VISIBLE);
                /////////////////
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View pauseDialog = getLayoutInflater().inflate(R.layout.dialog_summary, null);
                //AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.SummaryDialog);
                TextView title = pauseDialog.findViewById(R.id.title);
                TextView text = pauseDialog.findViewById(R.id.text);
                Button settingBtn = pauseDialog.findViewById(R.id.setting_btn);
                settingBtn.setVisibility(View.VISIBLE);
                settingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        startActivity(intent);
                    }
                });
                title.setText("התראה!");
                text.setText("על מנת לצפות במזג אוויר, עליך לאפשר גישה למיקום המכשיר.");
                final Dialog dialog = builder.setView(pauseDialog).show();
                dialog.getWindow().setWindowAnimations(R.style.Dialog);

            }
            else { getLocation(); }
        }
    }

    private void getLocation() {
        isPermissionOk = true;
        refresh_rotateLoading.start();
        refresh.setVisibility(View.INVISIBLE);
        if(isLocationEnabled(getContext())) {
            if(isConnectingToInternet(getActivity().getApplicationContext())) {//check connection to network

                note.setVisibility(View.GONE);

                if (weatherBehindIv.getVisibility() == View.VISIBLE) {
                    rotateLoading.start();
                }
                refresh.setVisibility(View.INVISIBLE);
                refresh_rotateLoading.start();
                if (rotateLoading.isStart()) {
                    refresh.setVisibility(View.VISIBLE);
                    refresh_rotateLoading.stop();
                }


                client = LocationServices.getFusedLocationProviderClient(getContext());
                LocationRequest request = LocationRequest.create();
                request.setInterval(5000);//information unpdate from network every 5sec
                request.setFastestInterval(500);
                request.setMaxWaitTime(1000);
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//praiority location(100 meter)
                callback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location location = locationResult.getLastLocation();

                        //Web service
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        process = new WeatherData();
                        process.setLatitude(latitude);
                        process.setLongitude(longitude);
                        process.execute();
                        calendar = Calendar.getInstance();
                        jsonActivation();
                        //get name of city
                        if (client != null) { client.removeLocationUpdates(callback); }

                    }
                };

                if (Build.VERSION.SDK_INT >= 23 && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    client.requestLocationUpdates(request, callback, null);
                }
            }
            else {
                if(weatherBehindIv.getVisibility() == View.VISIBLE){
                    rotateLoading.stop();
                    refresh_rotateLoading.stop();
                    refresh.setVisibility(View.VISIBLE);
                    note.setText("אנא הפעל חיבור לאינטרנט");
                    note.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getContext(), "אנא הפעל חיבור לאינטרנט", Toast.LENGTH_LONG).show();
                    refresh_rotateLoading.stop();
                    refresh.setVisibility(View.VISIBLE);
                }
            }
        }
        else {
            if(weatherBehindIv.getVisibility() == View.VISIBLE){
                rotateLoading.stop();
                refresh_rotateLoading.stop();
                refresh.setVisibility(View.VISIBLE);
                note.setText("אנא הפעל חיישן מיקום");
                note.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(getContext(), "אנא הפעל חיישן מיקום", Toast.LENGTH_LONG).show();
                refresh_rotateLoading.stop();
                refresh.setVisibility(View.VISIBLE);
            }
        }
    }

    public static boolean isLocationEnabled(Context context) {
        if(context != null) {
            int locationMode = 0;
            String locationProviders;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF;

            } else {
                locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                return !TextUtils.isEmpty(locationProviders);
            }
        }
        return false;
    }

    public void jsonActivation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (process.isLoading());//wait untill load in finish. can be make a progressBar
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isLocationEnabled(getContext())) {
                            //Toast.makeText(getContext(), "GPS ON", Toast.LENGTH_SHORT).show();
                            note.setVisibility(View.GONE);
                        }
                        else {
                            //Toast.makeText(getContext(), "GPS OFF", Toast.LENGTH_SHORT).show();
                            if(isPermissionOk) {
                                rotateLoading.stop();
                                note.setText("אנא הפעל חיישן מיקום");
                                note.setVisibility(View.VISIBLE);
                            }
                        }
                        isFinishLocation = true;
                        WeatherStruct weatherStruct = process.getWeatherStruct();
                        city.setText(weatherStruct.getCity() + "");
                        /*String myCityName = getCityName();
                        if(myCityName != null) {
                            if(!myCityName.equals("null")) {
                                city.setText(myCityName + "");
                            }
                            else {
                                city.setText(weatherStruct.getCity() + "");
                            }
                        }
                        else {
                            city.setText(weatherStruct.getCity() + "");
                        }*/
                        temp_range.setText(weatherStruct.getTempMin() + "°/" + weatherStruct.getTempMax() + "°");
                        description_tv.setText(getDescriptionIL(weatherStruct.getDescription()) + "");
                        temp.setText(weatherStruct.getTemp() + "");
                        dayNight = weatherStruct.getDayNightIcon();
                        //setDayNightBg();
                        //set correctly icon
                        rotateLoading.stop();
                        weatherBehindIv.setVisibility(View.GONE);
                        refresh_rotateLoading.stop();
                        refresh.setVisibility(View.VISIBLE);
                        setClockAndRefreshTime();
                        setDate();
                        setIcon();
                    }
                });
            }
        }).start();
    }

    public void setClockAndRefreshTime() {
        //int isAm = calendar.get(Calendar.AM);//AM=0, PM=1
        //if(isAm == 0) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            if (hour < 10) {
                if (min < 10) {
                    clock.setText("0" + hour + ":" + "0" + min);
                    refresh_time.setText("עודכן ב- " + "0" + hour + ":" + "0" + min);
                } else {
                    clock.setText("0" + hour + ":" + min);
                    refresh_time.setText("עודכן ב- " + "0" + hour + ":" + min);
                }
            } else {
                if (min < 10) {
                    clock.setText(hour + ":" + "0" + min);
                    refresh_time.setText("עודכן ב- " + hour + ":" + "0" + min);
                } else {
                    clock.setText(hour + ":" + min);
                    refresh_time.setText("עודכן ב- " + hour + ":" + min);
                }
            }
    }

    public String getCityName() {
        String cityName = "null";
        Locale mLocale = new Locale("iw","IL");
        Geocoder geocoder = new Geocoder(getContext(), mLocale);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            //addresses = geocoder.getFromLocation(32.990990990990994, 35.530946874064455, 1);//hatzor haglilit
            //addresses = geocoder.getFromLocation(31.8309705, 34.8215374, 1);//tel nof
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null && addresses.size() > 0) {
            cityName = addresses.get(0).getLocality();
        }
        return cityName;
    }

    public  void setDate() {
        String dayOfWeekStr, dayOfMonthStr, monthStr;
        int dayOfWeekNum, dayOfMonthNum, monthNum;

        //Month//
        monthNum = calendar.get(Calendar.MONTH);
        switch (monthNum) {
            case 0: monthStr = "ינו'";  break;
            case 1: monthStr = "פבר'";  break;
            case 2: monthStr = "מרץ'";  break;
            case 3: monthStr = "אפר'";  break;
            case 4: monthStr = "מאי";  break;
            case 5: monthStr = "יוני";  break;
            case 6: monthStr = "יולי'";  break;
            case 7: monthStr = "אוג'";  break;
            case 8: monthStr = "ספט'";  break;
            case 9: monthStr = "אוק'";  break;
            case 10: monthStr = "נוב'";  break;
            case 11: monthStr = "דצמ'";  break;
            default: monthStr = ""; break;
        }

        //Day Of Month//
        dayOfMonthNum = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfMonthStr = String.valueOf(dayOfMonthNum);

        //Day Of Week//
        dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK);
        switch(dayOfWeekNum) {
            case 1: dayOfWeekStr = "יום א'"; break;
            case 2: dayOfWeekStr = "יום ב'"; break;
            case 3: dayOfWeekStr = "יום ג'"; break;
            case 4: dayOfWeekStr = "יום ד'"; break;
            case 5: dayOfWeekStr = "יום ה'"; break;
            case 6: dayOfWeekStr = "יום ו'"; break;
            case 7: dayOfWeekStr = "יום ש'"; break;
            default: dayOfWeekStr = "";   break;
        }

        date.setText(dayOfWeekStr + " " + dayOfMonthStr + " " + monthStr);
    }

    //check if internet connected//
    public boolean isConnectingToInternet(Context _context){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public String getDescriptionIL(String description) {
        switch(description) {
            case "clear sky": return "בהיר";
            case "few clouds": return "מעט עננים";
            case "scattered clouds": return "מעונן";
            case "broken clouds": return "עננים אפורים";
            case "shower rain": return "טפטופי גשם";
            case "rain": return "גשום";
            case "light rain": return "גשם קל";
            case "moderate rain": return "גשם מתון";
            case "heavy intensity rain": return "גשם כבד";
            case "very heavy rain": return "גשם כבד מאוד";
            case "extreme rain": return "גשם קיצוני";
            case "freezing rain": return "גשם קפוא";
            case "light intensity shower rain": return "גשם בהיר";
            case "heavy intensity shower rain": return "גשם בעוצמה כבדה";
            case "ragged shower rain": return "גשם מטורף";
            case "light intensity drizzle": return "טפטוף בעוצמה קלה";
            case "drizzle": return "טפטוף";
            case "heavy intensity drizzle": return "טפטוף בעוצמה גבוהה";
            case "thunderstorm": return "סופות רעמים";
            case "thunderstorm with light rain": return "סופות רעמים עם גשם קל";
            case "thunderstorm with rain": return "סופות רעמים עם גשם";
            case "thunderstorm with heavy rain": return "סופות רעמים עם גשם כבד";
            case "light thunderstorm": return "סופות רעמים קלה";
            case "heavy thunderstorm": return "סופות רעמים כבדה";
            case "thunderstorm with drizzle": return "סופות רעמים עם טפטופים";
            case "drizzle rain": return "טפטופי גשם";
            case "few clouds: 11-25%": return "מעונן";
            case "scattered clouds: 25-50%": return "מעונן מאוד";
            case "broken clouds: 51-84%": return "עננות כבדה";
            case "overcast clouds: 85-100%": return "עננות כבדה מאוד";
            case "snow": return "שלג";
            case "mist": return "ערפל";
            case "Smoke": return "ערפל";
            case "Haze": return "ערפל";
            case "fog": return "ערפל";
            case "sand/ dust whirls": return "חול ואבק";
            case "sand": return "חולי";
            case "dust": return "אבק";
            case "tornado": return "טורנדו";
            default: return "";
        }
    }

    /*public void getLocationAndJSON() {
        if(isConnectingToInternet(getActivity().getApplicationContext())){ process.execute(); }//check connection to network and call to JSON and Location
        else { Toast.makeText(getContext(), "אין חיבור לאינטרנט. עליך להתחבר לאינטרנט על מנת לצפות בחדשות", Toast.LENGTH_LONG).show(); }

    }*/

    public void setIcon() {
        switch (dayNight) {
            case "01d":
                description_iv.setImageResource(R.drawable.clear_sky_day);
                break;
            case "01n":
                description_iv.setImageResource(R.drawable.clear_sky_night);
                break;
            case "02d":
                description_iv.setImageResource(R.drawable.few_clouds_day);
                break;
            case "02n":
                description_iv.setImageResource(R.drawable.few_clouds_night);
                break;
            case "03d":
                description_iv.setImageResource(R.drawable.few_clouds);
                break;
            case "03n":
                description_iv.setImageResource(R.drawable.few_clouds);
                break;
            case "04d":
                description_iv.setImageResource(R.drawable.scattered_clouds);
                break;
            case "04n":
                description_iv.setImageResource(R.drawable.scattered_clouds);
                break;
            case "09d":
                description_iv.setImageResource(R.drawable.rain);
                break;
            case "09n":
                description_iv.setImageResource(R.drawable.rain);
                break;
            case "10d":
                description_iv.setImageResource(R.drawable.shower_rain_night);
                break;
            case "10n":
                description_iv.setImageResource(R.drawable.shower_rain_night);
                break;
            case "11d":
                description_iv.setImageResource(R.drawable.thunderstorm);
                break;
            case "11n":
                description_iv.setImageResource(R.drawable.thunderstorm);
                break;
            case "13d":
                description_iv.setImageResource(R.drawable.snow);
                break;
            case "13n":
                description_iv.setImageResource(R.drawable.snow);
                break;
            case "50d":
                description_iv.setImageResource(R.drawable.mist);
                break;
            case "50n":
                description_iv.setImageResource(R.drawable.mist);
                break;
        }
    }

    public void setDayNightBg() {
        switch (dayNight.charAt(2)) {
            case 'd': rootRelativeLayout.setBackground(getResources().getDrawable(R.drawable.shape_bg_weather_day));    break;
            case 'n': rootRelativeLayout.setBackground(getResources().getDrawable(R.drawable.shape_bg_weather_night));  break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(process != null) { process.cancel(true); }
        if (client != null) { client.removeLocationUpdates(callback); }
    }

    @Override
    public void onStop() {
        super.onStop();

        //check the state of the task
        if(process != null)
            process.cancel(true);
    }
}
