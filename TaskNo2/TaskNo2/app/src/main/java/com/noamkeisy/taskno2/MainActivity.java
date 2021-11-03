package com.noamkeisy.taskno2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final String NEWS_FRAGMENT_TAG = "news_fragment";
    final String WEARHER_FRAGMENT_TAG = "weather_fragment";
    final String TECHNOLOGY_FRAGMENT_TAG = "technology_fragment";
    final String BUSINESS_FRAGMENT_TAG = "business_fragment";
    final String REMINDER_FRAGMENT_TAG = "reminder_fragment";
    ImageView behindWeatherFragment;
    boolean[] newsViewStatus = new boolean[]{true, false, false, false};//[general, technology, business, reminder]
    String TAG = "NotifService";
    Button newsBtn, technologyBtn, businessBtn, reminderBtn;
    boolean isThreadContinue = true;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"on create ACTIVITY");
        //SETTING//
        behindWeatherFragment = findViewById(R.id.behind_weather_fragment);
        //////////



        //button to view the weather fragment//
        Button weatherBtn = findViewById(R.id.weather_btn);
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //weatherCreateOrDestroy();
            }
        });

        //button to view the NEWS fragment// ~~~need to make it default option fragment
        newsBtn = findViewById(R.id.news_btn);
        newsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newsViewStatus[0]) {
                    removeAllFrag();
                    newsCreate();
                    setBtnBg(0);
                }
            }
        });

        //button to view the TECHNOLOGY fragment//
        technologyBtn = findViewById(R.id.technology_btn);
        technologyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newsViewStatus[1]) {
                    removeAllFrag();
                    technologyCreate();
                    setBtnBg(1);
                }
            }
        });

        //button to view the BUSINESS fragment//
        businessBtn = findViewById(R.id.business_btn);
        businessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newsViewStatus[2]) {
                    removeAllFrag();
                    businessCreate();
                    setBtnBg(2);
                }
            }
        });

        //button to view the REMINDER fragment//
        reminderBtn = findViewById(R.id.reminder_btn);
        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newsViewStatus[3]) {
                    removeAllFrag();
                    reminderCreate();
                    setBtnBg(3);
                }
            }
        });

        //Open Correctly Fragments//
        weatherCreateOrDestroy();
        switch (getIntent().getIntExtra("notif_callback", 0)) {
            case 0:
                newsCreate();
                setBtnBg(0);
                break;
            case 1:
                newsCreate();
                setBtnBg(0);
                break;
            case 2:
                technologyCreate();
                setBtnBg(1);
                break;
            case 3:
                businessCreate();
                setBtnBg(2);
                break;
        }
        ///////////////////////////


    }

    public void weatherCreateOrDestroy() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(WEARHER_FRAGMENT_TAG);
        if(fragment == null) {
            behindWeatherFragment.setVisibility(View.VISIBLE);
            fragmentTransaction.add(R.id.root_container, new WeatherFragment(), WEARHER_FRAGMENT_TAG);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else {
            behindWeatherFragment.setVisibility(View.GONE);
            fragmentTransaction.remove(fragment).commit();
        }
    }

    public void newsCreate() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(NEWS_FRAGMENT_TAG);
        if(fragment == null) {
            fragmentTransaction.add(R.id.root_container, new NewsFragment(), NEWS_FRAGMENT_TAG);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            newsViewStatus[0] = true;
        }
        else {
            fragmentTransaction.commit();
        }
    }

    public void technologyCreate() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(TECHNOLOGY_FRAGMENT_TAG);
        if(fragment == null) {
            fragmentTransaction.add(R.id.root_container, new TechnologyFragment(), TECHNOLOGY_FRAGMENT_TAG);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            newsViewStatus[1] = true;
        }
        else {
            fragmentTransaction.commit();
        }
    }

    public void businessCreate() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(BUSINESS_FRAGMENT_TAG);
        if(fragment == null) {
            fragmentTransaction.add(R.id.root_container, new BusinessFragment(), BUSINESS_FRAGMENT_TAG);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            newsViewStatus[2] = true;
        }
        else {
            fragmentTransaction.commit();
        }
    }

    public void reminderCreate() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(REMINDER_FRAGMENT_TAG);
        if(fragment == null) {
            fragmentTransaction.add(R.id.root_container, new ReminderFragment(), REMINDER_FRAGMENT_TAG);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            newsViewStatus[3] = true;
        }
        else {
            fragmentTransaction.commit();
        }
    }

    public void removeAllFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for(int i = 0; i < 4; i++) {
            if(newsViewStatus[i]) {
                switch(i) {
                    case 0: Fragment fragment0 = fragmentManager.findFragmentByTag(NEWS_FRAGMENT_TAG);
                            if(fragment0 != null) { fragmentTransaction.remove(fragment0).commit(); }
                            newsViewStatus[i] = false;
                            break;
                    case 1: Fragment fragment1 = fragmentManager.findFragmentByTag(TECHNOLOGY_FRAGMENT_TAG);
                            if(fragment1 != null) { fragmentTransaction.remove(fragment1).commit(); }
                            newsViewStatus[i] = false;
                            break;
                    case 2: Fragment fragment2 = fragmentManager.findFragmentByTag(BUSINESS_FRAGMENT_TAG);
                            if(fragment2 != null) { fragmentTransaction.remove(fragment2).commit(); }
                            newsViewStatus[i] = false;
                            break;
                    case 3: Fragment fragment3 = fragmentManager.findFragmentByTag(REMINDER_FRAGMENT_TAG);
                            if(fragment3 != null) { fragmentTransaction.remove(fragment3).commit(); }
                            newsViewStatus[i] = false;
                            break;
                }
            }
        }
    }

    public void setBtnBg(int btnId) {
        float scale = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams bigParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)(60*scale));
        bigParams.weight = 1;
        bigParams.gravity = Gravity.TOP;

        LinearLayout.LayoutParams smallParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        smallParams.weight = 1;
        switch (btnId) {
            case 0:
                newsBtn.setLayoutParams(bigParams);             newsBtn.setBackground(getResources().getDrawable(R.drawable.shape_btn_general));
                technologyBtn.setLayoutParams(smallParams);     technologyBtn.setBackground(getResources().getDrawable(R.color.bg1));
                businessBtn.setLayoutParams(smallParams);       businessBtn.setBackground(getResources().getDrawable(R.color.bg2));
                reminderBtn.setLayoutParams(smallParams);       reminderBtn.setBackground(getResources().getDrawable(R.color.bg3));
                break;
            case 1:
                newsBtn.setLayoutParams(smallParams);           newsBtn.setBackground(getResources().getDrawable(R.color.bg0));
                technologyBtn.setLayoutParams(bigParams);       technologyBtn.setBackground(getResources().getDrawable(R.drawable.shape_btn_technology));
                businessBtn.setLayoutParams(smallParams);       businessBtn.setBackground(getResources().getDrawable(R.color.bg2));
                reminderBtn.setLayoutParams(smallParams);       reminderBtn.setBackground(getResources().getDrawable(R.color.bg3));
                break;
            case 2:
                newsBtn.setLayoutParams(smallParams);           newsBtn.setBackground(getResources().getDrawable(R.color.bg0));
                technologyBtn.setLayoutParams(smallParams);     technologyBtn.setBackground(getResources().getDrawable(R.color.bg1));
                businessBtn.setLayoutParams(bigParams);         businessBtn.setBackground(getResources().getDrawable(R.drawable.shape_btn_business));
                reminderBtn.setLayoutParams(smallParams);       reminderBtn.setBackground(getResources().getDrawable(R.color.bg3));
                break;
            case 3:
                newsBtn.setLayoutParams(smallParams);           newsBtn.setBackground(getResources().getDrawable(R.color.bg0));
                technologyBtn.setLayoutParams(smallParams);     technologyBtn.setBackground(getResources().getDrawable(R.color.bg1));
                businessBtn.setLayoutParams(smallParams);       businessBtn.setBackground(getResources().getDrawable(R.color.bg2));
                reminderBtn.setLayoutParams(bigParams);         reminderBtn.setBackground(getResources().getDrawable(R.drawable.shape_btn_remind));
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"on destroy ACTIVITY");
        isThreadContinue = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //isThreadContinue = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //isThreadContinue = true;
        //weatherHindThread();
    }
}
