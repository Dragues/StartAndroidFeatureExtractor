package com.example.chist.startandroidfeatureextraction;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 1 on 28.01.2017.
 */
class HtmlContentLoader extends AsyncTask {

    String parseUrl;
    Context ctx;
    StringBuilder result;

    public HtmlContentLoader(String parseUrl,Context ctx) {
        this.parseUrl = parseUrl;
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        if(result.toString().length() > 0 ){
            Toast.makeText(ctx, "Data was getted. Success!", Toast.LENGTH_LONG).show();
            ((FeatureSelectorActivity)ctx).setHtml(result);
        }
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        this.result = new StringBuilder();
        try {
            HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
            HttpGet httpget = new HttpGet(parseUrl); // Set the action you want to do
            HttpResponse response = httpclient.execute(httpget); // Executeit
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent(); // Create an InputStream with the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                result.append(line);
            is.close(); // Close the stream
        }
        catch (Exception e){
            Toast.makeText(ctx, "Connection failed. Check your Ethernet connection.. ", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public StringBuilder getHtml(){
        return result;
    }
}
