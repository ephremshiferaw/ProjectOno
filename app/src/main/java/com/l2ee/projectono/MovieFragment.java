package com.l2ee.projectono;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.libraries.api.MoviesManager;
import com.libraries.core.Movie;
import com.libraries.core.Review;
import com.libraries.core.Video;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieFragment extends Fragment {


    private View movieView,movieHeaderView;
    private Movie currentMovie;
    MoviesManager moviesManager;
    ListView trailersListView;
    String VIEW_ULR = "https://www.youtube.com/watch?v=";
    RadioGroup group;
    int GRIDVIEWMODE;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        movieView = inflater.inflate(R.layout.fragment_movie, container, false);
        movieHeaderView = inflater.inflate(R.layout.movie_header, null, false);


        return movieView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (savedInstanceState != null && currentMovie==null) {
            currentMovie = (Movie)savedInstanceState.getSerializable("MOVIE");
            GRIDVIEWMODE = savedInstanceState.getInt("GRIDVIEWMODE",0);
        }
        if(activity==null)
            activity = getActivity();

        if (currentMovie != null) {

            TextView movieTitle = (TextView) movieHeaderView.findViewById(R.id.movieTitle);

            movieTitle.setText(currentMovie.getTitle());

            TextView overview = (TextView) movieHeaderView.findViewById(R.id.overview);
            overview.setText(currentMovie.getOverview());

            TextView releaseYear = (TextView) movieHeaderView.findViewById(R.id.releaseYear);
            releaseYear.setText(currentMovie.getReleaseDate());

            TextView vote = (TextView) movieHeaderView.findViewById(R.id.vote);
            vote.setText(currentMovie.getVoteAverage().toString() + "\\10");

            TextView voteCount = (TextView) movieHeaderView.findViewById(R.id.voteCount);
            voteCount.setText(currentMovie.getVoteCount().toString());

            RatingBar ratingBar = (RatingBar) movieHeaderView.findViewById(R.id.ratingBar);
            Double val = currentMovie.getPopularity() / 20;
            ratingBar.setRating(val.floatValue());

            FrameLayout posterFrame = (FrameLayout) movieHeaderView.findViewById(R.id.posterFrame);
            ImageView imageView = (ImageView) posterFrame
                    .findViewById(R.id.item_image);
            ProgressBar progressBar = null;
            progressBar = (ProgressBar) posterFrame.findViewById(R.id.itemProgressBar);
            progressBar.setVisibility(View.VISIBLE);

            bottomSpinner = (ProgressBar) movieView.findViewById(R.id.bottomProgressBar);

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


            CheckBox favoriteButton = (CheckBox)movieHeaderView.findViewById(R.id.favoriteButton);

            favoriteButton.setChecked(((MainActivity) activity).isFavorites(currentMovie));
            favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ((MainActivity) activity).toggleFavorites(currentMovie);
                }
            });


                    group = (RadioGroup) movieHeaderView.findViewById(R.id.viewModeRadioGroup);
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    View radioButton = radioGroup.findViewById(i);
                    GRIDVIEWMODE = radioGroup.indexOfChild(radioButton);
                    SetupVideoView(null);

                }
            });

            moviesManager = ((MainActivity)activity).getMoviesManager(savedInstanceState);

            trailersListView = (ListView) movieView.findViewById(R.id.trailersListView);
            trailersListView.addHeaderView(movieHeaderView, null, false);


        }

        SetupVideoView(savedInstanceState);

        super.onViewStateRestored(savedInstanceState);
    }

    private void setupGridView()
    {

    }


    private void SetupVideoView(Bundle savedInstanceState)
    {

        if(GRIDVIEWMODE==0) {

            if (mAdapter == null)
                mAdapter = new MoviesTrailerAdapter(activity);



            try {
                trailersListView.setAdapter(mAdapter);
                trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Video movie = (Video) (trailersListView.getItemAtPosition(position));


                        String videoUrl = VIEW_ULR + movie.getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        activity.startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


            initMovieTrailersAdapter(savedInstanceState);
        }
        else if(GRIDVIEWMODE==1) {

            if (mMoviesReviewAdapter == null)
                mMoviesReviewAdapter = new MoviesReviewAdapter(activity);



            try {
                trailersListView.setAdapter(mMoviesReviewAdapter);


            } catch (Exception e) {
                e.printStackTrace();
            }


            initMovieReviewsAdapter(savedInstanceState);
        }
    }

    void setCurrentMovie(Movie movie) {
        currentMovie = movie;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save to bundle
        outState.putSerializable("MOVIE", currentMovie);
        int i =  group.getCheckedRadioButtonId();
        outState.putInt("GRIDVIEWMODE",i);
        super.onSaveInstanceState(outState);
    }



    //Init Adapter, set movies if found in savedInstanceState otherwise recreate
    private void initMovieTrailersAdapter(Bundle savedInstanceState) {
        MoviesTrailerAdapter ret;

        if (savedInstanceState != null) {
            ArrayList<Video> movies = (ArrayList<Video>) savedInstanceState.getSerializable("MOVIESTRAILERS");
            mAdapter.setMovieTrailerList(movies);
        } else {
            aGetMoviesAsyncTask = new GetMovieTrailersAsyncTask();
            aGetMoviesAsyncTask.execute();
        }
    }
    //Init Adapter, set movies if found in savedInstanceState otherwise recreate
    private void initMovieReviewsAdapter(Bundle savedInstanceState) {
        MoviesReviewAdapter ret;

        if (savedInstanceState != null) {
            ArrayList<Review> movies = (ArrayList<Review>) savedInstanceState.getSerializable("MOVIESTRAILERS");
            mMoviesReviewAdapter.setReviews(movies);
        } else {
            aGetMovieTrailersAsyncTask = new GetMovieReviewsAsyncTask();
            aGetMovieTrailersAsyncTask.execute();
        }
    }



    public void setMoviesManager(MoviesManager moviesManager) {
       this.moviesManager = moviesManager;
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

    private Activity activity;
    private ProgressBar bottomSpinner;
    MoviesTrailerAdapter mAdapter;
    MoviesReviewAdapter mMoviesReviewAdapter;
    private GetMovieTrailersAsyncTask aGetMoviesAsyncTask;
    private GetMovieReviewsAsyncTask aGetMovieTrailersAsyncTask;

    private class GetMovieTrailersAsyncTask extends AsyncTask<String, Void, ArrayList<Video>> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected ArrayList<Video> doInBackground(String... page) {
            activity.runOnUiThread(new Runnable() {
                public void run() {

                    bottomSpinner.setVisibility(View.VISIBLE);
                }
            });
            ArrayList<Video> t = null;
            try {
                t = moviesManager.getMovieTrailers(currentMovie);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(ArrayList<Video> result) {
            mAdapter.setMovieTrailerList(result);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    bottomSpinner.setVisibility(View.GONE);

                }
            });
        }

    }


    private class GetMovieReviewsAsyncTask extends AsyncTask<String, Void, ArrayList<Review>> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected ArrayList<Review> doInBackground(String... page) {
            activity.runOnUiThread(new Runnable() {
                public void run() {

                    bottomSpinner.setVisibility(View.VISIBLE);
                }
            });
            ArrayList<Review> t = null;
            try {
                t = moviesManager.getMovieReviews(currentMovie);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(ArrayList<Review> result) {
            mMoviesReviewAdapter.setReviews(result);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    mMoviesReviewAdapter.notifyDataSetChanged();
                    bottomSpinner.setVisibility(View.GONE);

                }
            });
        }

    }
}


