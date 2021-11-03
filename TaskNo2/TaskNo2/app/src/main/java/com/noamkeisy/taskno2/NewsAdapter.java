package com.noamkeisy.taskno2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.loading.rotate.RotateLoading;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> news;
    private MyNewsListener listener;
    News newss;
    Handler handler = new Handler();

    interface MyNewsListener {
        void onNewsClicked(int position, View view);
        void onNewsLongClicked(int position, View view);
        void onNewsSummaryClicked(int position, View view);
    }

    public void setListener(MyNewsListener listener) {
        this.listener = listener;
    }

    public NewsAdapter(List<News> foods) {
        this.news = foods;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView picIsNull;
        TextView title;
        TextView date;
        TextView summary;
        CardView cardView;
        RotateLoading rotateLoading;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.pic);
            picIsNull = itemView.findViewById(R.id.pic_null_tv);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date_tv);
            summary = itemView.findViewById(R.id.summary);
            cardView = itemView.findViewById(R.id.card_view);
            rotateLoading = itemView.findViewById(R.id.rotateloading);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onNewsClicked(getAdapterPosition(), v);
                        //listener.onNewsSummaryClicked(getAdapterPosition(), v);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener != null) {
                        listener.onNewsLongClicked(getAdapterPosition(), v);
                    }
                    return false;
                }
            });

            summary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onNewsSummaryClicked(getAdapterPosition(), v);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news, viewGroup, false);
        NewsViewHolder newsViewHolder = new NewsViewHolder(view);
        return newsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder newsViewHolder, int i) {//פונקציה זו נקראת בעת הגלילה לכל כרטיס וכרטיס
        newss = news.get(i);
        newsViewHolder.rotateLoading.start();
        newsViewHolder.pic.setImageBitmap(null);
        newsViewHolder.title.setText(newss.getTitle());
        newsViewHolder.date.setText(newss.getDate() + "");
        if(!newss.getPicLink().equals("null") && newss.getPicLink().length() != 0) {
            newsViewHolder.picIsNull.setVisibility(View.INVISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(newss.getPicLink());
                        final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                newsViewHolder.rotateLoading.stop();
                                newsViewHolder.pic.setImageBitmap(bitmap);
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else {
            newsViewHolder.pic.setImageResource(R.color.white_opacity);
            newsViewHolder.rotateLoading.stop();
            newsViewHolder.picIsNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}
