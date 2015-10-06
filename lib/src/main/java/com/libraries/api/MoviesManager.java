package com.libraries.api;

import com.libraries.core.Movie;
import com.libraries.core.Review;
import com.libraries.core.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;




public class MoviesManager implements Serializable {




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
    private static final String TAG_RELEASE_DATE = "release_date";
    private static final String TAG_TOTAL_PAGES = "total_pages";
    private static final String   TAG_PAGE = "page";


    private static final  String TAG_BASE_POSTER = "http://image.tmdb.org/t/p/w185/";
    private static final  String BASE_URL = "http://api.themoviedb.org/3/";



    private static final String TAG_ISO_639_1 = "iso_639_1";
    private static final String TAG_KEY = "key";
    private static final String TAG_NAME= "name";
    private static final String TAG_SITE = "site";
    private static final String TAG_SIZE = "size";
    private static final String TAG_TYPE = "type";

    private static final String TAG_AUTHOR= "author";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_URL = "url";





    private String apiKey;
    private SortOptions sortBy;
    private String url;

    public enum SortOptions {
        VOTE_AVERAGE_DESC,
        VOTE_AVERAGE_ASC,
        FIRST_AIR_DATE_DESC,
        FIRST_AIR_DATE_ASC,
        POPULARITY_DESC,
        POPULARITY_ASC,
        FAVORITES

    }

    public MoviesManager(String apiKey, SortOptions sortBy) {
        this.apiKey = apiKey;

        setSortBy(sortBy);

    }

    public SortOptions getSortBy()
    {
        return this.sortBy;
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
        url = BASE_URL + "discover/movie?sort_by=" + sort + "&api_key="+apiKey;
        clear();
    }

    public int getCacheCount()
    {
        if(this.sortBy==SortOptions.FAVORITES) {
            if (fmovieList == null)
                return 0;
            return fmovieList.size();
        }
        else
        {
            if (movieList == null)
                return 0;
            return movieList.size();
        }

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

    public ArrayList<Video> getMovieTrailers(Movie movie)throws Exception
    {
        ArrayList<Video> ret = movie.getVideos();
        if(ret==null) {
            //fetch trailers
           ret = new ArrayList<>();
            String trailersUrl = BASE_URL + "movie/" + movie.getId() + "/videos?api_key="+apiKey;
            String jsonStr = sendGet(trailersUrl);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray(TAG_RESULTS);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                       // if("Trailer" != c.getString(TAG_TYPE)) continue;
                        Video m = new Video();
                        m.setId(c.getString(TAG_ID));
                        m.setIso6391(c.getString(TAG_ISO_639_1));
                        m.setKey(c.getString(TAG_KEY));
                        m.setName(c.getString(TAG_NAME));
                        m.setSite(c.getString(TAG_SITE));
                        m.setSize(c.getInt(TAG_SIZE));
                        m.setType(c.getString(TAG_TYPE));

                        if (!ret.contains(m))
                            ret.add(m);


                    }
                    movie.setVideos(ret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    public ArrayList<Review> getMovieReviews(Movie movie)throws Exception
    {
        ArrayList<Review> ret = movie.getReviews();
        if(ret==null) {
            //fetch trailers
            ret = new ArrayList<>();
        }
        int reviewPage= movie.getReviewPage();
        int totalReviews= movie.getTotalReviews();

        if(totalReviews<reviewPage)
            return ret;

        reviewPage++;



            String reviewUrl = BASE_URL + "movie/" + movie.getId() + "/reviews?page=" + reviewPage +"&api_key="+apiKey;
            String jsonStr = sendGet(reviewUrl);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    totalReviews = jsonObj.getInt(TAG_TOTAL_PAGES);
                    reviewPage = jsonObj.getInt(TAG_PAGE);
                    movie.setReviewPage(reviewPage);
                    movie.setTotalPages(totalReviews);
                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray(TAG_RESULTS);
                    int i=0;
                    // looping through All Contacts
                    for ( i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        Review m = new Review();
                        m.setId(c.getString(TAG_ID));
                        m.setContent(c.getString(TAG_CONTENT));
                        m.setAuthor(c.getString(TAG_AUTHOR));

                        if (!ret.contains(m))
                            ret.add(m);


                    }
                    movie.setReviews(ret);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        return ret;
    }


    private int currentPage=1;

    ArrayList<Movie> movieList;
    ArrayList<Movie> fmovieList;

    public ArrayList<Movie> getFavoriteMovies(int[] movieIds)throws Exception {
        fmovieList = new ArrayList<Movie>();

        if (movieList == null)
            movieList = new ArrayList<Movie>();

        Movie m ;
        for(int i=0;i<movieIds.length;i++) {
            m = new Movie();
            m.setId(movieIds[i]);
            int j =  movieList.indexOf(m);
            if(j>=0) {
                fmovieList.add(movieList.get(j));
                continue;
            }

            String trailersUrl = BASE_URL + "movie/" + movieIds[i] + "?api_key=" + apiKey;

            String jsonStr = sendGet(trailersUrl);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);
                    m = new Movie();
                    m.setId(c.getInt(TAG_ID));
                    m.setTitle(c.getString(TAG_TITLE));
                    m.setPosterPath(TAG_BASE_POSTER + c.getString(TAG_POSTER_PATH));
                    m.setOverview(c.getString(TAG_OVERVIEW));
                    m.setVoteAverage(c.getDouble(TAG_VOTE_AVERAGE));
                    m.setVoteCount(c.getInt(TAG_VOTE_COUNT));
                    m.setPopularity(c.getDouble(TAG_POPULARITY));
                    m.setReleaseDate(c.getString(TAG_RELEASE_DATE));

                    if (!movieList.contains(m))
                        movieList.add(m);

                    if (!fmovieList.contains(m))
                        fmovieList.add(m);


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return fmovieList;
    }


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
                    m.setOverview(c.getString(TAG_OVERVIEW));
                    m.setVoteAverage(c.getDouble(TAG_VOTE_AVERAGE));
                    m.setVoteCount(c.getInt(TAG_VOTE_COUNT));
                    m.setPopularity(c.getDouble(TAG_POPULARITY));
                    m.setReleaseDate(c.getString(TAG_RELEASE_DATE));

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



