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


    MoviesFragment firstFragment;
    MovieFragment secondFragment;
    MenuItem menuSortPopular,menuSortRating,menusettings;

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
                   .commit();
        }
    }

    @Override
    public void onBackPressed() {
        menusettings.setVisible(true);
        super.onBackPressed();
    }

    @Override
    public void onMovieSelected(Movie movie) {
        secondFragment = new MovieFragment();
        secondFragment.setCurrentMovie(movie);
        if (findViewById(R.id.movie_container) == null) {
            menusettings.setVisible(false);
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_container, secondFragment)
                    .addToBackStack("secondFragment")
                    .commit();
        }
        else  {
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
        menusettings = menu.findItem(R.id.menuSettings);
        menuSortRating = menu.findItem(R.id.menuSortRating);
        menuSortPopular = menu.findItem(R.id.menuSortPopular);
        adjustIcons( getSortOrder(this));
        return true;
    }

    void resetIcons()
    {
        menuSortPopular.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        menuSortRating.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    void adjustIcons(int order)
    {
        resetIcons();
        if(order== Movies.SortOptions.POPULARITY_DESC.ordinal() ) {
            menuSortPopular.setIcon(R.drawable.ic_arrow_down);
        }
        else if(order== Movies.SortOptions.POPULARITY_ASC.ordinal() )
        {
            menuSortPopular.setIcon(R.drawable.ic_arrow_up);
        }
        else if(order==Movies.SortOptions.VOTE_AVERAGE_DESC.ordinal() ) {
            menuSortRating.setIcon(R.drawable.ic_arrow_down);
        }
        else {
            menuSortRating.setIcon(R.drawable.ic_arrow_up);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.menuSortPopular || id == R.id.menuSortRating) {
            int v = getSortOrder(this);
            int newSort = 0;
            if (id == R.id.menuSortPopular) {
                if(v== Movies.SortOptions.POPULARITY_DESC.ordinal() ) {
                    newSort = Movies.SortOptions.POPULARITY_ASC.ordinal();
                }
                else {
                    newSort = Movies.SortOptions.POPULARITY_DESC.ordinal();
                }

            } else if (id == R.id.menuSortRating) {
                if(v!=Movies.SortOptions.VOTE_AVERAGE_DESC.ordinal() ) {
                    newSort = Movies.SortOptions.VOTE_AVERAGE_DESC.ordinal();
                }
                else {
                    newSort = Movies.SortOptions.VOTE_AVERAGE_ASC.ordinal();
                }
            }
            adjustIcons(newSort);
            saveSortOrder(this, newSort);
            firstFragment.Refresh(Movies.SortOptions.values()[newSort]);
        }
        return super.onOptionsItemSelected(item);
    }


    private static void saveSortOrder(Activity activity, int order)
    {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SORT_ORDER", order);
        editor.commit();
    }
    public static int getSortOrder(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt("SORT_ORDER", 0);
    }
}