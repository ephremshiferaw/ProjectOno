package com.libraries.core;

/**
 * Created by ephrem.shiferaw on 7/10/2015.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movie implements Serializable {


    private Boolean adult;
    private String backdropPath;
    private List<Integer> genreIds = new ArrayList<Integer>();
    private int id;
    private String originalLanguage;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private Double popularity;
    private String title;
    private Boolean video;
    private Double voteAverage;
    private Integer voteCount;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The adult
     */
    public Boolean getAdult() {
        return adult;
    }

    /**
     *
     * @param adult
     * The adult
     */
     public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    /**
     *
     * @return
     * The backdropPath
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     *
     * @param backdropPath
     * The backdrop_path
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     *
     * @return
     * The genreIds
     */

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    /**
     *
     * @param genreIds
     * The genre_ids
     */

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    /**
     *
     * @return
     * The id
     */

    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */

    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The originalLanguage
     */

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     *
     * @param originalLanguage
     * The original_language
     */

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    /**
     *
     * @return
     * The originalTitle
     */

    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     *
     * @param originalTitle
     * The original_title
     */

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    /**
     *
     * @return
     * The overview
     */

    public String getOverview() {
        return overview;
    }

    /**
     *
     * @param overview
     * The overview
     */

    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     *
     * @return
     * The releaseDate
     */

    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     *
     * @param releaseDate
     * The release_date
     */

    public void setReleaseDate(String releaseDate) {
        this.releaseDate =  releaseDate;
    }

    /**
     *
     * @return
     * The posterPath
     */

    public String getPosterPath() {
        return posterPath;
    }

    /**
     *
     * @param posterPath
     * The poster_path
     */

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     *
     * @return
     * The popularity
     */

    public Double getPopularity() {
        return popularity;
    }

    /**
     *
     * @param popularity
     * The popularity
     */

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    /**
     *
     * @return
     * The title
     */

    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The video
     */

    public Boolean getVideo() {
        return video;
    }

    /**
     *
     * @param video
     * The video
     */

    public void setVideo(Boolean video) {
        this.video = video;
    }

    /**
     *
     * @return
     * The voteAverage
     */

    public Double getVoteAverage() {
        return voteAverage;
    }

    /**
     *
     * @param voteAverage
     * The vote_average
     */

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     *
     * @return
     * The voteCount
     */

    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     *
     * @param voteCount
     * The vote_count
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    private ArrayList<Video> results;

    /**
     *
     * @return ArrayList<Video>
     */
    public ArrayList<Video> getVideos() { return this.results; }

    /**
     *
     * @param results
     */
    public void setVideos(ArrayList<Video> results) { this.results = results; }



    private int reviewPage;

    public int getReviewPage() { return this.reviewPage; }

    public void setReviewPage(int reviewPage) { this.reviewPage = reviewPage; }

    private ArrayList<Review> reviews;

    public ArrayList<Review> getReviews() { return this.reviews; }

    public void setReviews(ArrayList<Review> results) { this.reviews = results; }

    private int total_reviews;

    public int getTotalReviews() { return this.total_reviews; }

    public void setTotalPages(int total_reviews) { this.total_reviews = total_reviews;}

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Movie)
        {
            Movie v = ((Movie) object);

            sameSame = this.getId() == v.getId();
        }

        return sameSame;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}

