package com.noamkeisy.taskno2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

public class BusinessFragment extends Fragment {
    final String NEWS_FRAGMENT_TAG = "news_fragment";
    final String WEARHER_FRAGMENT_TAG = "weather_fragment";
    final String TECHNOLOGY_FRAGMENT_TAG = "technology_fragment";
    final String BUSINESS_FRAGMENT_TAG = "business_fragment";
    final String REMINDER_FRAGMENT_TAG = "reminder_fragment";
    RecyclerView recyclerView;
    ArrayList<News> news = new ArrayList<>();
    ArrayList<NewsStruct> arrayList = new ArrayList<>();
    NewsAdapter newsAdapter = new NewsAdapter(news);
    Handler handler = new Handler();
    boolean refreshFlag = true;
    RotateLoading rotateLoading, refresh_rotateLoading;
    Button refreshBtn, thermometer;
    ImageView behindWeather;
    BusinessData process;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_business, container, false);


        recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        rotateLoading = rootView.findViewById(R.id.rotateloading);
        rotateLoading.start();
        refresh_rotateLoading = rootView.findViewById(R.id.refresh_rotateloading);
        refreshBtn = rootView.findViewById(R.id.refresh_btn);
        thermometer = rootView.findViewById(R.id.thermometer);
        behindWeather = getActivity().findViewById(R.id.behind_weather_fragment);

        //JSON//
        refreshList();
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectingToInternet(getActivity().getApplicationContext())) {//check connection to network
                    if(process != null) {
                        process.cancel(true);
                    }
                    refreshList();
                }
                else { Toast.makeText(getContext(), "אין חיבור לאינטרנט. עליך להתחבר לאינטרנט על מנת לצפות בחדשות", Toast.LENGTH_SHORT).show(); }
            }
        });
        ///////

        thermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherCreateOrDestroy();
            }
        });


        newsAdapter.setListener(new NewsAdapter.MyNewsListener() {
            @Override
            public void onNewsClicked(int position, View view) {
                if(!news.get(position).getLink().equals("null")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(news.get(position).getLink()));
                    startActivity(intent);
                }
            }

            @Override
            public void onNewsLongClicked(int position, View view) {

            }

            @Override
            public void onNewsSummaryClicked(int position, View view) {
                if(news.get(position).getSummary().equals("null")) {
                    news.get(position).setSummary("ללא תקציר");
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View pauseDialog = getLayoutInflater().inflate(R.layout.dialog_summary, null);
                //AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.SummaryDialog);
                TextView title = pauseDialog.findViewById(R.id.title);
                TextView text = pauseDialog.findViewById(R.id.text);
                title.setText("תקציר:");
                text.setText(news.get(position).getSummary() + "");
                final Dialog dialog = builder.setView(pauseDialog).show();
                dialog.getWindow().setWindowAnimations(R.style.Dialog);
            }
        });
        //rotateLoading.stop();
        recyclerView.setAdapter(newsAdapter);

        return  rootView;
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

    public void refreshList() {
        recyclerView.stopScroll();
        recyclerView.setLayoutFrozen(true);
        //set progress
        handler.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(null);
                if(isConnectingToInternet(getActivity().getApplicationContext())) {
                    refreshBtn.setVisibility(View.INVISIBLE);
                    refresh_rotateLoading.start();
                }
                else {
                    Toast.makeText(getContext(), "אין חיבור לאינטרנט. עליך להתחבר לאינטרנט על מנת לצפות בחדשות", Toast.LENGTH_SHORT).show();
                    refresh_rotateLoading.stop();
                    rotateLoading.stop();
                }
            }
        });
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                process = new BusinessData();
                process.execute();
                while (process.isLoading()) ;//wait untill load in finish. can be make a progressBar
                arrayList = process.getArrayList();
                news.clear();
                for (int i = 0; i < arrayList.size() - 1; i++) {//insert all arrayList's data into news. in arrayList have all news one by one int struct
                    news.add(new News(arrayList.get(i).getUrlToImage(),
                            arrayList.get(i).getTitle(),
                            arrayList.get(i).getDate(),
                            arrayList.get(i).getDescription(),
                            arrayList.get(i).getUrl()));
                    //repair the date view//
                    String date = news.get(i).getDate();
                    String tempDate = "";
                    int flagEndString = 0;
                    for (int j = 0; j < date.length(); j++) {
                        char curCh = date.charAt(j);
                        switch (curCh) {
                            case '-':
                                tempDate = tempDate + ".";
                                break;
                            case 'T':
                                tempDate = tempDate + "  ";
                                break;
                            case ':':
                                if (flagEndString == 0) {
                                    tempDate = tempDate + ":";
                                }
                                flagEndString++;
                                break;
                            default:
                                tempDate = tempDate + date.charAt(j);
                                break;
                        }
                        if (flagEndString >= 2) {
                            break;
                        }
                    }
                    news.get(i).setDate(tempDate);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        rotateLoading.stop();
                        recyclerView.setAdapter(newsAdapter);
                        //remove progress
                        refresh_rotateLoading.stop();
                        refreshBtn.setVisibility(View.VISIBLE);
                        //
                    }
                });

            }
        }).start();
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
