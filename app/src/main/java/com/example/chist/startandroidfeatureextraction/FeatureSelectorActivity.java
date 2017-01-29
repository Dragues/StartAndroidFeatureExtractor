package com.example.chist.startandroidfeatureextraction;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureSelectorActivity extends AppCompatActivity {

    private String myMail = "chist-34dml@mail.ru";
    private String customSubject = "Add feature in application";
    private String body = "Write there anything...";
    private String parseUrl = "http://startandroid.ru/ru/uroki/vse-uroki-spiskom.html";
    private StringBuilder html;
    private ArrayList<AsyncTask> taskList = new ArrayList<AsyncTask>();
    private CustomObserver htmlObserver;
    private Uri uri = Uri.parse("content://data/html");
    private ListView listLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeatureSelectorActivity.this.startActivity(prepareMailIntent(myMail, customSubject, body));
                ;
            }
        });
        htmlObserver = new CustomObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, htmlObserver);

        HtmlContentLoader loadTask = new HtmlContentLoader(parseUrl,this);
        taskList.add(loadTask);
        loadTask.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feature_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Send mail to my address
    public Intent prepareMailIntent (String to,String subject, String body){
        return new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + to + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(body)));
    }

    // Observer for updating html
    class CustomObserver extends ContentObserver {
        private ArrayList<Pair<String, String>> themesFromHtml;

        public CustomObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            ArrayList<Pair<Integer,String>>  themes = getThemesFromHtml();
            LessonsAdapter adapter = new LessonsAdapter(FeatureSelectorActivity.this,themes);
            listLessons = ((ListView)findViewById(R.id.lessons));
            listLessons.setAdapter(adapter);
        }


        public ArrayList<Pair<Integer,String>>  getThemesFromHtml() {
            ArrayList<Pair<Integer,String>> result = new ArrayList<>();
            Pattern p = Pattern.compile("<a href=\"/ru/uroki/vse-uroki-spiskom/(.*?).html\">(.*?)Урок(.*?)</a>");
            Matcher m = p.matcher(html);
            while(m.find())
                result.add(new Pair(Integer.parseInt(m.group(3).trim().split(". ")[0]),m.group(3).trim()));
            Collections.sort(result, new PairComparator());
            return result;
        }
    }

    public void setHtml(StringBuilder html) {
        this.html = html;
        getContentResolver().notifyChange(uri, null);
    }

    @Override
    protected void onStop() {
        for (AsyncTask task : taskList) {
            task.cancel(true);
        }
        getContentResolver().unregisterContentObserver(htmlObserver);
        super.onStop();
    }

    private static class PairComparator implements Comparator<Pair<Integer,String>> {

        @Override
        public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
            return new Integer((o1.first)).compareTo(new Integer(o2.first));
        }
    }
}