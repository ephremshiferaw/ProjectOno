package com.l2ee.projectono;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.libraries.api.Movies;
import com.libraries.core.Movie;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMovieSelectedListener {


   // SwipeRefreshLayout swipeLayout;
/*
    @Override
    implements SwipeRefreshLayout.OnRefreshListener
    public void onRefresh() {

        moviesManager.clear();
        mAdapter.notifyDataSetChanged();
        refreshing = true;
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();

           }*/

    MoviesFragment firstFragment;
    MovieFragment secondFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.movies_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            firstFragment = new MoviesFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
           getSupportFragmentManager().beginTransaction()
                   .add(R.id.movies_container, firstFragment)
                   //.addToBackStack("firstFragment")
                   .commit();
        }

        //for big screens

    }

    @Override
    public void onMovieSelected(Movie movie) {
        if (findViewById(R.id.movies_container) != null) {

            secondFragment = new MovieFragment();
            secondFragment.setCurrentMovie(movie);
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_container, secondFragment)
                    .addToBackStack("secondFragment")
                    .commit();
        }
    }



    boolean refreshing = false;
    MenuItem menuSortPopular,menuSortRating;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuSortRating = menu.findItem(R.id.menuSortRating);
        menuSortPopular = menu.findItem(R.id.menuSortPopular);
        resetIcons();
        return true;
    }

    void resetIcons()
    {
        menuSortPopular.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        menuSortRating.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.menuSortPopular || id == R.id.menuSortRating) {

            int v = getSortorder(this);
            int newSort = 0;
            resetIcons();

            if (id == R.id.menuSortPopular) {

                if(v== Movies.SortOptions.POPULARITY_DESC.ordinal() ) {
                    newSort = Movies.SortOptions.POPULARITY_ASC.ordinal();
                    menuSortRating.setIcon(R.drawable.ic_arrow_down);
                }
                else {
                    newSort = Movies.SortOptions.POPULARITY_DESC.ordinal();
                    menuSortRating.setIcon(R.drawable.ic_arrow_up);
                }

            } else if (id == R.id.menuSortRating) {
                if(v!=Movies.SortOptions.VOTE_AVERAGE_DESC.ordinal() ) {
                    newSort = Movies.SortOptions.VOTE_AVERAGE_DESC.ordinal();
                    menuSortRating.setIcon(R.drawable.ic_arrow_down);
                }
                else {
                    newSort = Movies.SortOptions.VOTE_AVERAGE_ASC.ordinal();
                    menuSortRating.setIcon(R.drawable.ic_arrow_up);
                }
            }

            saveSortorder(this, newSort);
            firstFragment.Refresh(Movies.SortOptions.values()[newSort]);
        }


        return super.onOptionsItemSelected(item);
    }


    private static void saveSortorder(Activity activity,int order)
    {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SORT_ORDER", order);
        editor.commit();
    }

    public static int getSortorder(Activity activity)
    {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("SORT_ORDER",0) ;
    }

/*  Activity activity;
    GridView gridView;
    Movies moviesManager;
    private MoviesAdapter mAdapter;


    /*
    private class GetMoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        protected ArrayList<Movie> doInBackground(String... page) {
            activity.runOnUiThread(new Runnable() {
                public void run() {

                    bottomSpinner.setVisibility(View.VISIBLE);
                }
            });
            ArrayList<Movie> t = null;
            try {
                t = moviesManager.getMovies();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            mAdapter.setMovieList(result);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    bottomSpinner.setVisibility(View.GONE);
                    swipeLayout.setRefreshing(false);
                }
            });
            refreshing = false;

        }

    }*/
}
