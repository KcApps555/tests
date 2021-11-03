package com.noamkeisy.taskno2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.negateExact;

public class ReminderFragment extends Fragment {
    final String NEWS_FRAGMENT_TAG = "news_fragment";
    final String WEARHER_FRAGMENT_TAG = "weather_fragment";
    final String TECHNOLOGY_FRAGMENT_TAG = "technology_fragment";
    final String BUSINESS_FRAGMENT_TAG = "business_fragment";
    final String REMINDER_FRAGMENT_TAG = "reminder_fragment";
    final static int NOTIF_WEATHER_ID = 0;
    final static int NOTIF_GENERAL_ID = 1;
    final static int NOTIF_TECHNOLOGY_ID = 2;
    final static int NOTIF_BUSINESS_ID = 3;
    boolean[] cbStatus = new boolean[]{false, false, false, false};
    SwitchCompat weatherCb, generalCb, technologyCb, businessCb;
    TextView weatherActive, generalActive, technologyActive, businessActive;
    Button thermometer;
    ImageButton addRemind;
    boolean isRbWorking = false;
    int notif_id = NOTIF_WEATHER_ID, notif_resIconId = R.drawable.notif_weather_icon_24dp;
    String notif_title = "מזג אוויר", notif_text = "תוכן מזג אוויר";
    EditText repeatEt;
    AlarmManager alarmManager;
    Calendar calendar = Calendar.getInstance();
    int curHour, curMinute, choosenHour, choosenMinute , timeToRemind;
    int requestCode = 0;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    boolean firstRun = true;
    ImageView behindWeather;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_reminder, container, false);

        //SETTING//
        weatherCb = rootView.findViewById(R.id.weather_cb);
        generalCb = rootView.findViewById(R.id.general_cb);
        technologyCb = rootView.findViewById(R.id.technology_cb);
        businessCb = rootView.findViewById(R.id.business_cb);
        weatherActive = rootView.findViewById(R.id.weather_active);
        generalActive = rootView.findViewById(R.id.general_active);
        technologyActive = rootView.findViewById(R.id.technology_active);
        businessActive = rootView.findViewById(R.id.business_active);
        repeatEt = rootView.findViewById(R.id.time_range_et);
        thermometer = rootView.findViewById(R.id.thermometer);
        behindWeather = getActivity().findViewById(R.id.behind_weather_fragment);
        sp = getContext().getSharedPreferences("general", MODE_PRIVATE);
        editor = sp.edit();
        firstRun = sp.getBoolean("firstRun", true);
        if(firstRun) {
            editor.putBoolean("firstRun", false);

            editor.putInt("notif_weather_id", NOTIF_WEATHER_ID);
            editor.putInt("notif_weather_resIconId", R.drawable.notif_weather_icon_24dp);
            editor.putString("notif_weather_title", "חדשות - מזג אוויר");
            editor.putString("notif_weather_text", "הכנס לצפות בעדכוני מזג האוויר");

            editor.putInt("notif_general_id", NOTIF_GENERAL_ID);
            editor.putInt("notif_general_resIconId", R.drawable.notif_news_icon_24dp);
            editor.putString("notif_general_title", "חדשות");
            editor.putString("notif_general_text", "הכנס לצפות בכל מה שחם");

            editor.putInt("notif_technology_id", NOTIF_TECHNOLOGY_ID);
            editor.putInt("notif_technology_resIconId", R.drawable.notif_news_icon_24dp);
            editor.putString("notif_technology_title", "חדשות");
            editor.putString("notif_technology_text", "הכנס לצפות בחדשות - טכנולוגיה");

            editor.putInt("notif_business_id", NOTIF_BUSINESS_ID);
            editor.putInt("notif_business_resIconId", R.drawable.notif_news_icon_24dp);
            editor.putString("notif_business_title", "חדשות");
            editor.putString("notif_business_text", "הכנס לצפות בחדשות - עסקים");
            //commit in the Enter Button
            editor.commit();
        }
        ///////////

        //Read from file the status of CheckBox//
        boolean weatherCbTmp = sp.getBoolean("weatherCb", false);
        boolean generalCbTmp = sp.getBoolean("generalCb", false);
        boolean technologyCbTmp = sp.getBoolean("technologyCb", false);
        boolean businessCbTmp = sp.getBoolean("businessCb", false);
        if(weatherCbTmp) {
            cbStatus[0] = true;
            weatherCb.setChecked(true);
            weatherActive.setText("פעיל");
            weatherActive.setTextColor(getResources().getColor(R.color.green));
        }
        else {
            cbStatus[0] = false;
            weatherCb.setChecked(false);
            weatherActive.setText("לא פעיל");
            weatherActive.setTextColor(getResources().getColor(R.color.red));
        }
        if(generalCbTmp) {
            cbStatus[1] = true;
            generalCb.setChecked(true);
            generalActive.setText("פעיל");
            generalActive.setTextColor(getResources().getColor(R.color.green));
        }
        else {
            cbStatus[1] = false;
            generalCb.setChecked(false);
            generalActive.setText("לא פעיל");
            generalActive.setTextColor(getResources().getColor(R.color.red));
        }
        if(technologyCbTmp) {
            cbStatus[2] = true;
            technologyCb.setChecked(true);
            technologyActive.setText("פעיל");
            technologyActive.setTextColor(getResources().getColor(R.color.green));
        }
        else {
            cbStatus[2] = false;
            technologyCb.setChecked(false);
            technologyActive.setText("לא פעיל");
            technologyActive.setTextColor(getResources().getColor(R.color.red));

        }
        if(businessCbTmp) {
            cbStatus[3] = true;
            businessCb.setChecked(true);
            businessActive.setText("פעיל");
            businessActive.setTextColor(getResources().getColor(R.color.green));
        }
        else {
            cbStatus[3] = false;
            businessCb.setChecked(false);
            businessActive.setText("לא פעיל");
            businessActive.setTextColor(getResources().getColor(R.color.red));
        }
        /////////////////////////////////////////

        //XML BUTTON//
        Button enterBtn = rootView.findViewById(R.id.enter_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeat = 0;
                boolean isAllCbClear = true, pass = false;
                for(int i = 0; i < 4; i++) {
                    if(cbStatus[i]) { isAllCbClear = false; }
                }
                if(isAllCbClear) {
                    repeat = 0;
                    pass = true;
                    cbStatus[0] = false;
                    weatherActive.setText("לא פעיל");
                    weatherActive.setTextColor(getResources().getColor(R.color.red));
                    editor.putBoolean("weatherCb", false);
                    cbStatus[1] = false;
                    generalActive.setText("לא פעיל");
                    generalActive.setTextColor(getResources().getColor(R.color.red));
                    editor.putBoolean("generalCb", false);
                    cbStatus[2] = false;
                    technologyActive.setText("לא פעיל");
                    technologyActive.setTextColor(getResources().getColor(R.color.red));
                    editor.putBoolean("technologyCb", false);
                    cbStatus[3] = false;
                    businessActive.setText("לא פעיל");
                    businessActive.setTextColor(getResources().getColor(R.color.red));
                    editor.putBoolean("businessCb", false);
                    Toast.makeText(getContext(), "כל ההתראות מבוטלות.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(repeatEt.length() == 0) {
                        Toast.makeText(getContext(), "אנא הזן טווח זמן.", Toast.LENGTH_SHORT).show();
                        pass = false;
                    }
                    else {
                        repeat = Integer.parseInt(repeatEt.getText().toString());
                        pass = true;
                        if(weatherCb.isChecked()) {
                            cbStatus[0] = true;
                            weatherActive.setText("פעיל");
                            weatherActive.setTextColor(getResources().getColor(R.color.green));
                            editor.putBoolean("weatherCb", true);
                        }
                        else {
                            cbStatus[0] = false;
                            weatherActive.setText("לא פעיל");
                            weatherActive.setTextColor(getResources().getColor(R.color.red));
                            editor.putBoolean("weatherCb", false);
                        }
                        if(generalCb.isChecked()) {
                            cbStatus[1] = true;
                            generalActive.setText("פעיל");
                            generalActive.setTextColor(getResources().getColor(R.color.green));
                            editor.putBoolean("generalCb", true);
                        }
                        else {
                            cbStatus[1] = false;
                            generalActive.setText("לא פעיל");
                            generalActive.setTextColor(getResources().getColor(R.color.red));
                            editor.putBoolean("generalCb", false);
                        }
                        if(technologyCb.isChecked()) {
                            cbStatus[2] = true;
                            technologyActive.setText("פעיל");
                            technologyActive.setTextColor(getResources().getColor(R.color.green));
                            editor.putBoolean("technologyCb", true);
                        }
                        else {
                            cbStatus[2] = false;
                            technologyActive.setText("לא פעיל");
                            technologyActive.setTextColor(getResources().getColor(R.color.red));
                            editor.putBoolean("technologyCb", false);

                        }
                        if(businessCb.isChecked()) {
                            cbStatus[3] = true;
                            businessActive.setText("פעיל");
                            businessActive.setTextColor(getResources().getColor(R.color.green));
                            editor.putBoolean("businessCb", true);
                        }
                        else {
                            cbStatus[3] = false;
                            businessActive.setText("לא פעיל");
                            businessActive.setTextColor(getResources().getColor(R.color.red));
                            editor.putBoolean("businessCb", false);
                        }
                    }
                }

                if(pass) {
                    editor.putInt("repeat", repeat);
                    editor.putBoolean("notif_weather_is_on", cbStatus[0]);
                    editor.putBoolean("notif_general_is_on", cbStatus[1]);
                    editor.putBoolean("notif_technology_is_on", cbStatus[2]);
                    editor.putBoolean("notif_business_is_on", cbStatus[3]);
                    editor.commit();


                    alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getActivity(), NotifReceive.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeat*60*1000, pendingIntent);
                    if (!isAllCbClear) {
                        Toast.makeText(getContext(), "התזכורת הופעלה לעוד " + repeat + " דקות.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        thermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherCreateOrDestroy();
            }
        });

        //////////////

        //XML RADIO BUTTON//
        weatherCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { cbStatus[0] = true; }
                else { cbStatus[0] = false; }
            }
        });

        generalCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { cbStatus[1] = true; }
                else { cbStatus[1] = false; }
            }
        });

        technologyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { cbStatus[2] = true; }
                else { cbStatus[2] = false; }
            }
        });

        businessCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { cbStatus[3] = true; }
                else { cbStatus[3] = false; }
            }
        });
        ////////////////////

        return rootView;
    }

    public void weatherCreateOrDestroy() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(WEARHER_FRAGMENT_TAG);
        if(fragment == null) {

            behindWeather.setVisibility(View.VISIBLE);
            fragmentTransaction.add(R.id.root_container, new WeatherFragment(), WEARHER_FRAGMENT_TAG);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else {
            behindWeather.setVisibility(View.GONE);
            fragmentTransaction.remove(fragment).commit();
        }
    }

}
