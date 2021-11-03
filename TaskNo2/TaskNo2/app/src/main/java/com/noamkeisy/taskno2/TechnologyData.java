package com.noamkeisy.taskno2;

import android.os.AsyncTask;

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

public class TechnologyData extends AsyncTask<Void, Void, Void> {
    private ArrayList<NewsStruct> arrayList = new ArrayList<>();
    private boolean loading = true;

    @Override
    protected Void doInBackground(Void... voids) {
        ArrayList<NewsStruct> arrayListTmp = new ArrayList<>();
        String data ="";

        try {
            URL url = new URL("https://newsapi.org/v2/top-headlines?country=il&category=technology&apiKey=ac326d18823d400c86b4a42c46d5a6f9");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONObject rootObject = new JSONObject(data);
            JSONArray newsArr  = rootObject.getJSONArray("articles");
            for(int i = 0; i < newsArr.length(); i++) {
                JSONObject bestResult = newsArr.getJSONObject(i);
                NewsStruct newsStructTmp = new NewsStruct();
                newsStructTmp.setTitle(bestResult.getString("title"));
                newsStructTmp.setDescription(bestResult.getString("description"));
                newsStructTmp.setUrl(bestResult.getString("url"));
                newsStructTmp.setUrlToImage(bestResult.getString("urlToImage"));
                newsStructTmp.setDate(bestResult.getString("publishedAt"));
                arrayListTmp.add(newsStructTmp);
            }
            addToList(arrayListTmp);
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

    public ArrayList<NewsStruct> getArrayList(){
        return this.arrayList;
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void addToList(ArrayList<NewsStruct> arrList) {
        this.arrayList = arrList;
    }

}
