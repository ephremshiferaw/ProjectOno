package com.l2ee.projectono;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.libraries.core.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ephrem.shiferaw on 7/27/2015.
 */
public class MoviesAdapter extends BaseAdapter {

    ArrayList<Movie> movieList;
    Context mContext;
    LayoutInflater inflater;

    public MoviesAdapter(Context c) {
        mContext = c;
        movieList = new ArrayList<Movie>();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public void setMovieList(ArrayList<Movie> movieList) {
        if (movieList != null)
            this.movieList = movieList;
    }

    public ArrayList<Movie>  getMovieList() {
       return  movieList;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {

        return movieList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.movie_grid_item, null);
        }
        Movie movie = movieList.get(position);
        // set image based on selected text
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.grid_item_image);

        ProgressBar progressBar = null;
            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);


        String poster = movie.getPosterPath();
        Picasso.with(mContext)
                .load(poster).into(imageView, new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (this.progressBar != null) {
                            this.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            return convertView;
        }

        private class ImageLoadedCallback implements Callback {
            ProgressBar progressBar;

            public  ImageLoadedCallback(ProgressBar progBar){
                progressBar = progBar;
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        }
}

