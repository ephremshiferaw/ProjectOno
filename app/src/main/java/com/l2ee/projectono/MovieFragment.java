package com.l2ee.projectono;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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



        return movieView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (savedInstanceState != null && currentMovie==null) {
            currentMovie = (Movie)savedInstanceState.getSerializable("MOVIE");
        }
        if (currentMovie != null) {
            TextView movieTitle = (TextView) movieView.findViewById(R.id.movieTitle);

            movieTitle.setText(currentMovie.getTitle());

            TextView overview = (TextView) movieView.findViewById(R.id.overview);
            overview.setText(currentMovie.getOverview());

            TextView releaseYear = (TextView) movieView.findViewById(R.id.releaseYear);
            releaseYear.setText(currentMovie.getReleaseDate());

            TextView vote = (TextView) movieView.findViewById(R.id.vote);
            vote.setText(currentMovie.getVoteAverage().toString() + "\\10");

            TextView voteCount = (TextView) movieView.findViewById(R.id.voteCount);
            voteCount.setText(currentMovie.getVoteCount().toString());

            RatingBar ratingBar = (RatingBar) movieView.findViewById(R.id.ratingBar);
            Double val = currentMovie.getPopularity() / 20;
            ratingBar.setRating(val.floatValue());

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
        super.onViewStateRestored(savedInstanceState);
    }

    void setCurrentMovie(Movie movie) {
        currentMovie = movie;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save to bundle
        outState.putSerializable("MOVIE", currentMovie);
        super.onSaveInstanceState(outState);
    }



    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar progBar) {
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


