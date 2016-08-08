package com.justinraczak.android.movies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by justinr on 3/14/16.
 */
public class Movie implements Parcelable {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    int id;
    String title;
    String releaseDate;
    String synopsis;
    String voteAverage;
    String posterUrl;

    public Movie(int id, String title, String releaseDate, String synopsis, String voteAverage, String posterUrl) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.voteAverage = voteAverage;
        this.posterUrl = posterUrl;
    }

    public int describeContents() {
        return 0;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        synopsis = in.readString();
        voteAverage = in.readString();
        posterUrl = in.readString();
    }

    public void formatDateForDisplay() {
        try {
            String currentDateString = this.releaseDate;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = format.parse(currentDateString);

            format = new SimpleDateFormat("MM-dd-yyyy");
            this.releaseDate = format.format(newDate);
            Log.d("Date formatter", "Updated the date format to " + this.releaseDate);
        } catch (ParseException e) {
            Log.d("Date formatter", "Failed to parse date data");
        }
    }

    //TODO: See if this is actually doable?
    public ArrayList<Movie> createMovieArrayFromJson(JSONArray jsonArray) {
        // Create a batch of movie objects from a JSONArray of movie data
        return null;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(synopsis);
        parcel.writeString(voteAverage);
        parcel.writeString(posterUrl);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel sourceParcel) {
            return new Movie(sourceParcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



    public boolean isFavorite() {

        RealmQuery<FavoriteMovie> query = Realm.getDefaultInstance().where(FavoriteMovie.class);
        query.equalTo("movieId", Integer.toString(this.id));

        Log.d("Movie Class", "Searching for favorite with id " + id);

        RealmResults<FavoriteMovie> results = query.findAll();
        Log.d("Movie Class", "Query returned " + results.size() + " results");

        if (results.size() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    

}
