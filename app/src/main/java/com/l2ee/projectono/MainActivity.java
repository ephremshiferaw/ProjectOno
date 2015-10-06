package com.l2ee.projectono;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.libraries.api.MoviesManager;
import com.libraries.core.Movie;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMovieSelectedListener {


    private MoviesFragment firstFragment;
    private MovieFragment secondFragment;
    private MenuItem menuSortPopular, menuSortRating, menuSettings;
    private boolean mTwoPane;
    private MoviesManager moviesManager;

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
            firstFragment.setMoviesManager(getMoviesManager(savedInstanceState));
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_container, firstFragment)
                    .commit();
        }

        //if (findViewById(R.id.movie_container) != null) {

           // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //} else
           // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onBackPressed() {
        menuSettings.setVisible(true);
        super.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getMoviesManager(savedInstanceState);

        //this is to setup the fragment local variables
        if (firstFragment == null) {
            Fragment f1 = this.getSupportFragmentManager().findFragmentById(R.id.movies_container);
            if (f1.getClass().equals(MoviesFragment.class)) {
                firstFragment = (MoviesFragment) f1;
                firstFragment.setMoviesManager(moviesManager);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("MOVIES_MANAGER", moviesManager);
        if(hasChanged)
            saveFavoritesMovies(this,mySet1);
        super.onSaveInstanceState(outState);
    }

    //Get MoviesManager from memory if it exists, from savedInstanceState if it was saved or return new
    MoviesManager getMoviesManager(Bundle savedInstanceState) {

        if (moviesManager != null)
            return moviesManager;
        if (savedInstanceState != null) {
            MoviesManager ret = (MoviesManager) savedInstanceState.getSerializable("MOVIES_MANAGER");
            if (ret != null) return ret;
        }
        //if all the above conditions fail then reinitialize the manager
        return new MoviesManager(getResources().getString(R.string.api_key), MoviesManager.SortOptions.POPULARITY_DESC);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        secondFragment = new MovieFragment();
        secondFragment.setCurrentMovie(movie);
        if (findViewById(R.id.movie_container) == null) {
            menuSettings.setVisible(false);
            this.setTitle("Movie Detail");
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_container, secondFragment)
                    .addToBackStack("secondFragment")
                    .commit();
        } else {
            //for big screens
            // Add the fragment to the 'fragment_container' FrameLayout but not to backStack
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_container, secondFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuSettings = menu.findItem(R.id.menuSettings);
        menuSortRating = menu.findItem(R.id.menuSortRating);
        menuSortPopular = menu.findItem(R.id.menuSortPopular);
        adjustIcons(getSortOrder(this));
        return true;
    }

    void resetIcons() {
        menuSortPopular.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        menuSortRating.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    void adjustIcons(int order) {
        resetIcons();
        if (order == MoviesManager.SortOptions.POPULARITY_DESC.ordinal()) {
            menuSortPopular.setIcon(R.drawable.ic_arrow_down);
        } else if (order == MoviesManager.SortOptions.POPULARITY_ASC.ordinal()) {
            menuSortPopular.setIcon(R.drawable.ic_arrow_up);
        } else if (order == MoviesManager.SortOptions.VOTE_AVERAGE_DESC.ordinal()) {
            menuSortRating.setIcon(R.drawable.ic_arrow_down);
        } else if (order == MoviesManager.SortOptions.VOTE_AVERAGE_ASC.ordinal()) {
            menuSortRating.setIcon(R.drawable.ic_arrow_up);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id != R.id.menuSettings ) {
            int v = getSortOrder(this);
            int newSort = 0;
            if (id == R.id.menuSortPopular) {
                if (v == MoviesManager.SortOptions.POPULARITY_DESC.ordinal()) {
                    newSort = MoviesManager.SortOptions.POPULARITY_ASC.ordinal();
                } else {
                    newSort = MoviesManager.SortOptions.POPULARITY_DESC.ordinal();
                }

            } else if (id == R.id.menuSortRating) {
                if (v != MoviesManager.SortOptions.VOTE_AVERAGE_DESC.ordinal()) {
                    newSort = MoviesManager.SortOptions.VOTE_AVERAGE_DESC.ordinal();
                } else {
                    newSort = MoviesManager.SortOptions.VOTE_AVERAGE_ASC.ordinal();
                }
            }
            else if (id == R.id.menuSortRating) {
                if (v != MoviesManager.SortOptions.VOTE_AVERAGE_DESC.ordinal()) {
                    newSort = MoviesManager.SortOptions.VOTE_AVERAGE_DESC.ordinal();
                } else {
                    newSort = MoviesManager.SortOptions.VOTE_AVERAGE_ASC.ordinal();
                }
            }
            else
                newSort =  MoviesManager.SortOptions.FAVORITES.ordinal();
            adjustIcons(newSort);
            saveSortOrder(this, newSort);
            firstFragment.Refresh(MoviesManager.SortOptions.values()[newSort]);
        }
        return super.onOptionsItemSelected(item);
    }


    private static void saveSortOrder(Activity activity, int order) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SORT_ORDER", order);
        editor.commit();
    }

    private static void saveFavoritesMovies(Activity activity, Set<String> favorites) {

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("FAVORITES", favorites);
        editor.commit();
    }

    Set<String> mySet1;
    Boolean hasChanged = false;
    public Boolean toggleFavorites(Movie movie) {
        hasChanged = true;
        if (mySet1 == null)
            mySet1 = getFavorites(this);
        String id = Integer.toString(movie.getId()) ;
        Boolean val = mySet1.contains(id);

        if (!val)
            mySet1.add(id);
        else
            mySet1.remove(id);
        return true;
    }

    public Boolean isFavorites(Movie movie) {
        hasChanged = true;
        if (mySet1 == null)
            mySet1 = getFavorites(this);
        String id = Integer.toString(movie.getId()) ;
        Boolean val = mySet1.contains(id);

        return val;
    }
    public   int[]  getFavoriteMovieIds() {
        if (mySet1 == null)
            mySet1 = getFavorites(this);
        Object[] set = mySet1.toArray();
        int[] ret = new int[set.length];
        for (int i = 0; i < set.length; i++) {
            ret[i] = Integer.parseInt(set[i].toString());
        }
        return ret;
    }

    public static  Set<String> getFavorites(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        Set<String> mySet1 = new HashSet<String>();
        mySet1 = sharedPref.getStringSet("FAVORITES", mySet1);
        return mySet1;
    }

    public static int getSortOrder(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("SORT_ORDER", 0);
    }
}