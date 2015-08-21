package com.l2ee.projectono;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.libraries.api.Movies;
import com.libraries.core.Movie;

import java.util.ArrayList;

public class MoviesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_movies, container, false);
        initialize(v);
        return v;
    }

    private OnMovieSelectedListener mListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMovieSelectedListener) activity;
        } catch (ClassCastException ignore) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void Refresh(Movies.SortOptions sort)
    {
        moviesManager.setSortBy(sort);
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMovieSelectedListener {
        public void onMovieSelected(Movie movie);
    }

    @Override
    public void onRefresh() {
        moviesManager.clear();
        mAdapter.notifyDataSetChanged();
        refreshing = true;
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();
    }

    Activity activity;
    SwipeRefreshLayout swipeLayout;
    GridView gridView;
    Movies moviesManager;
    private MoviesAdapter mAdapter;
    private ProgressBar spinner;
    private ProgressBar bottomSpinner;
    GetMoviesAsyncTask aGetMoviesAsyncTask;
    boolean refreshing = false;

    void initialize(View container) {
        activity = this.getActivity();
        mAdapter = new MoviesAdapter(activity);
        String api_key = getResources().getString(R.string.api_key);
        if (moviesManager == null)
            moviesManager = new Movies(api_key, Movies.SortOptions.POPULARITY_DESC);

        swipeLayout = (SwipeRefreshLayout) container.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        gridView = (GridView) container.findViewById(R.id.moviesGridView);
        gridView.setAdapter(mAdapter);
        bottomSpinner = (ProgressBar) container.findViewById(R.id.bottomProgressBar);
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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener!=null)
                    mListener.onMovieSelected((Movie) mAdapter.getItem(position));
            }
        });
        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mListener!=null)
                    mListener.onMovieSelected((Movie) mAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();
    }

    private class GetMoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
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

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
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
