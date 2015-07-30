package com.l2ee.projectono;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.libraries.api.Movies;
import com.libraries.core.Movie;

import java.util.ArrayList;





public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {


    SwipeRefreshLayout swipeLayout;

    @Override
    public void onRefresh() {

        moviesManager.clear();
        mAdapter.notifyDataSetChanged();
        refreshing = true;
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();

           }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        activity = this;

        mAdapter = new MoviesAdapter(this);
        String api_key = getResources().getString(R.string.api_key);
        if (moviesManager == null)
            moviesManager = new Movies(api_key, Movies.SortOptions.POPULARITY_DESC);


        gridView = (GridView) findViewById(R.id.moviesGridView);
        gridView.setAdapter(mAdapter);

        bottomSpinner = (ProgressBar) findViewById(R.id.bottomProgressBar);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount >= moviesManager.getCacheCount()) & !refreshing) {
                    refreshing = true;
                    aGetMoviesAsyncTask = new GetMoviesAsyncTask();
                    aGetMoviesAsyncTask.execute();
                }

                int topRowVerticalPosition =
                        (gridView == null || gridView.getChildCount() == 0) ?
                                0 : gridView.getChildAt(0).getTop();
                swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);

            }
        });
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();
    }

    GetMoviesAsyncTask  aGetMoviesAsyncTask;
    private ProgressBar spinner;
    private ProgressBar bottomSpinner;
    boolean refreshing = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    Activity activity;
    GridView gridView;
    Movies moviesManager;
    private MoviesAdapter mAdapter;

    private class GetMoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected ArrayList<Movie> doInBackground(String... page) {


            activity.runOnUiThread(new Runnable() {
                public void run() {

                    bottomSpinner.setVisibility(View.VISIBLE);


                }
            });

            ArrayList<Movie> t = null;
            try {
                t = moviesManager.getMovies();
                /*try {
                    Thread.sleep(5000);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return t;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
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

    }
}
