package com.l2ee.projectono;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.libraries.core.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieFragment extends Fragment {




    private View movieView;
    private Movie currentMovie;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        movieView = inflater.inflate(R.layout.fragment_movie, container, false);
        if(currentMovie!=null) {
            TextView movieTitle = (TextView) movieView.findViewById(R.id.movieTitle);
            movieTitle.setText(currentMovie.getTitle());
            FrameLayout posterFrame = (FrameLayout) movieView.findViewById(R.id.posterFrame);
            ImageView imageView = (ImageView) posterFrame
                    .findViewById(R.id.item_image);
            ProgressBar progressBar = null;
            progressBar = (ProgressBar) posterFrame.findViewById(R.id.itemProgressBar);
            progressBar.setVisibility(View.VISIBLE);

            String poster = currentMovie.getPosterPath();
            Picasso.with(this.getActivity())
                    .load(poster).into(imageView, new ImageLoadedCallback(progressBar) {
                @Override
                public void onSuccess() {
                    if (this.progressBar != null) {
                        this.progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
        return movieView;
    }

    void setCurrentMovie(Movie movie)
    {
        currentMovie = movie;


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


