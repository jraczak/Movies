package com.justinraczak.android.movies;

import io.realm.RealmObject;

/**
 * Created by Justin on 5/20/16.
 */
public class FavoriteMovie extends RealmObject {

    private String id;
    private String movieId;

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
}
