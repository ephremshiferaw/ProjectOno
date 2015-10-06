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

import com.libraries.api.MoviesManager;
import com.libraries.core.Movie;

import java.util.ArrayList;

public class MoviesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private OnMovieSelectedListener mListener;
    private Activity activity;
    private SwipeRefreshLayout swipeLayout;
    private GridView gridView;
    private MoviesManager moviesManager;
    private MoviesAdapter mAdapter;
    private ProgressBar spinner;
    private ProgressBar bottomSpinner;
    private GetMoviesAsyncTask aGetMoviesAsyncTask;
    boolean refreshing = false;
    private View moviesView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        moviesView = inflater.inflate(R.layout.fragment_movies, container, false);
        activity = this.getActivity();

        return moviesView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save to bundle
        outState.putSerializable("MOVIES", mAdapter.movieList);
        outState.putSerializable("MOVIES_MANAGER", moviesManager);
        super.onSaveInstanceState(outState);
    }

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

    public void Refresh(MoviesManager.SortOptions sort)
    {
        moviesManager.setSortBy(sort);
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();
        setTitle(sort);

    }

    public MoviesManager getMoviesManager() {
        return moviesManager;
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
    public void onViewStateRestored(Bundle savedInstanceState) {
        mAdapter = getMoviesAdapter(savedInstanceState);
        setupView(moviesView);
        MoviesManager.SortOptions sort = moviesManager.getSortBy();
        setTitle(sort);
        super.onViewStateRestored(savedInstanceState);
    }

    private void setTitle(MoviesManager.SortOptions sort) {
        if(activity==null)
            return;
        if(sort== MoviesManager.SortOptions.FAVORITES)
            activity.setTitle("Favorites");
        else if(sort== MoviesManager.SortOptions.POPULARITY_ASC || sort== MoviesManager.SortOptions.POPULARITY_DESC)
            activity.setTitle("Movies by Popularity");
        else if(sort== MoviesManager.SortOptions.VOTE_AVERAGE_ASC || sort== MoviesManager.SortOptions.VOTE_AVERAGE_DESC)
            activity.setTitle("Movies by Rating");
    }

    public void setMoviesManager(MoviesManager moviesManager) {
        this.moviesManager = moviesManager;


    }

    //Init Adapter, set movies if found in savedInstanceState otherwise recreate
    private MoviesAdapter getMoviesAdapter(Bundle savedInstanceState) {
        MoviesAdapter ret;
        if (mAdapter != null)
            ret = mAdapter;
        else
            ret = new MoviesAdapter(activity);

        if (savedInstanceState != null) {
            ArrayList<Movie> movies = (ArrayList<Movie>) savedInstanceState.getSerializable("MOVIES");
            ret.setMovieList(movies);
        } else {
            aGetMoviesAsyncTask = new GetMoviesAsyncTask();
            aGetMoviesAsyncTask.execute();
        }
        return ret;
    }

    @Override
    public void onRefresh() {
        moviesManager.clear();
        mAdapter.notifyDataSetChanged();
        refreshing = true;
        aGetMoviesAsyncTask = new GetMoviesAsyncTask();
        aGetMoviesAsyncTask.execute();
    }


    void setupView(View container) {
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
                if ((firstVisibleItem + visibleItemCount >= moviesManager.getCacheCount()) & !refreshing & moviesManager.getSortBy()!= MoviesManager.SortOptions.FAVORITES) {
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
                if (mListener != null)
                    mListener.onMovieSelected((Movie) mAdapter.getItem(position));
            }
        });
        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null)
                    mListener.onMovieSelected((Movie) mAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private class GetMoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected ArrayList<Movie> doInBackground(String... page) {
            refreshing = true;
            activity.runOnUiThread(new Runnable() {
                public void run() {

                    bottomSpinner.setVisibility(View.VISIBLE);
                }
            });
            ArrayList<Movie> t = null;
            try {
                if( moviesManager.getSortBy()== MoviesManager.SortOptions.FAVORITES)
                {
                    int[] f = ((MainActivity)activity).getFavoriteMovieIds();
                    t = moviesManager.getFavoriteMovies(f);
                }
                else {
                    t = moviesManager.getMovies();
                }
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
                    //mListener.onMovieSelected(mAdapter.movieList.get(0));
                    swipeLayout.setRefreshing(false);
                }
            });
            refreshing = false;

        }

    }

}
