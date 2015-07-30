package com.libraries.api;

import com.libraries.core.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;




public class Movies {




    // JSON Node names
    private static final String TAG_TITLE = "title";
    private static final String TAG_VOTE_AVERAGE = "vote_average";
    private static final String TAG_POPULARITY = "popularity";
    private static final String TAG_ORIGINAL_TITLE = "original_title";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_VOTE_COUNT = "vote_count";
    private static final String TAG_POSTER_PATH = "poster_path";
    private static final String TAG_BACKDROP_PATH = "backdrop_path";
    private static final String TAG_ID = "id";
    private static final String TAG_RESULTS = "results";


    ArrayList<Movie> movieList;
    private static final  String TAG_BASE_POSTER = "http://image.tmdb.org/t/p/w185/";

    private String apiKey;
    private SortOptions sortBy;
    private String url;

    public enum SortOptions {
        VOTE_AVERAGE_DESC,
        VOTE_AVERAGE_ASC,
        FIRST_AIR_DATE_DESC,
        FIRST_AIR_DATE_ASC,
        POPULARITY_DESC,
        POPULARITY_ASC
    }

    public Movies(String apiKey, SortOptions sortBy) {
        this.apiKey = apiKey;

        setSortBy(sortBy);

    }

    public void setSortBy(SortOptions sortBy) {
        String sort = "";
        switch (sortBy) {
            case POPULARITY_ASC:
                sort = "popularity.acs";
                break;
            case POPULARITY_DESC:
                sort = "popularity.desc";
                break;
            case VOTE_AVERAGE_ASC:
                sort= "vote_average.asc";
                break;
            case VOTE_AVERAGE_DESC:
                sort = "vote_average.desc";
                break;
            case FIRST_AIR_DATE_ASC:
                sort = "first_air_date.asc";
                break;
            case FIRST_AIR_DATE_DESC:
                sort = "first_air_date.desc";
                break;
            default:
                sort = " popularity.desc";
                break;
        }

        this.sortBy = sortBy;
        url = "http://api.themoviedb.org/3/discover/movie?sort_by=" + sort + "&api_key="+apiKey;
    }

    public int getCacheCount()
    {
        if(movieList==null)
            return 0;
        return movieList.size();
    }


    public ArrayList<Movie> getMovies()throws Exception
    {
        getMovies(currentPage);
        currentPage++;
        return movieList;
    }

    public void clear()
    {
        if (movieList != null)
            movieList.clear();
        currentPage = 1;
    }

    private int currentPage=1;
    public ArrayList<Movie> getMovies(int page)throws Exception {
        String jsonStr = sendGet(url + "&page=" + page);

        if (movieList == null)
            movieList = new ArrayList<Movie>();


        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);


                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray(TAG_RESULTS);

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    Movie m = new Movie();
                    m.setId(c.getInt(TAG_ID));
                    m.setTitle(c.getString(TAG_TITLE));
                    m.setPosterPath(TAG_BASE_POSTER + c.getString(TAG_POSTER_PATH));

                    if (!movieList.contains(m))
                        movieList.add(m);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieList;
    }
    // HTTP GET request
    private String sendGet(String url) throws Exception {



        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");



        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }
}

