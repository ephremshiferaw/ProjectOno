package com.l2ee.projectono;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libraries.core.Video;

import java.util.ArrayList;

public class MoviesTrailerAdapter extends BaseAdapter {

    ArrayList<Video> movieTrailerList;
    Context mContext;
    LayoutInflater inflater;

    public MoviesTrailerAdapter(Context c) {
        mContext = c;
        movieTrailerList = new ArrayList<Video>();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public void setMovieTrailerList(ArrayList<Video> movieTrailerList) {
        if (movieTrailerList != null)
            this.movieTrailerList = movieTrailerList;
    }

    public ArrayList<Video> getMovieTrailerList() {
        return movieTrailerList;
    }

    @Override
    public int getCount() {
        return movieTrailerList.size();
    }

    @Override
    public Object getItem(int position) {

        return movieTrailerList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.movie_trailer_item, null);
        }
        Video movie = movieTrailerList.get(position);
        // set image based on selected text
        TextView textView = (TextView) convertView
                .findViewById(R.id.trailerTitle);

        textView.setText(movie.getName());

        return convertView;
    }
}
