package com.justinraczak.android.movies;

import io.realm.RealmObject;

/**
 * Created by Justin on 5/20/16.
 */
public class FavoriteMovie extends RealmObject {

    private String id;
    private String movieId;
    private String title;
    private String releaseDate;
    private String synopsis;
    private String voteAverage;
    private String posterUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Movie convertFavoriteToMovie() {
        Movie convertedMovie = new Movie(Integer.parseInt(this.movieId), this.title, this.releaseDate,
                this.synopsis, this.voteAverage, this.posterUrl);
        return convertedMovie;
    }
}
