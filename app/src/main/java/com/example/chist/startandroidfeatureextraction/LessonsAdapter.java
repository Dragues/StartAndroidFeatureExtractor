package com.example.chist.startandroidfeatureextraction;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 1 on 29.01.2017.
 */
public class LessonsAdapter extends ArrayAdapter<Pair<Integer,String>> {

    ArrayList<String> lessons = new ArrayList<>();
    LayoutInflater inflater;

    public LessonsAdapter(Context ctx, ArrayList<Pair<Integer,String>> lessons) {
        super(ctx, R.layout.lesson_item, lessons);
        inflater = ((Activity) ctx).getLayoutInflater();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            View view = inflater.inflate(R.layout.lesson_item, parent, false);
            ((TextView) view.findViewById(R.id.lessonName)).setText(getItem(position).second);
            return view;
        }
        ((TextView) convertView.findViewById(R.id.lessonName)).setText(getItem(position).second);
        return convertView;

    }
}
