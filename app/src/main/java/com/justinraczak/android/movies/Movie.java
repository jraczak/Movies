package com.justinraczak.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by justinr on 3/14/16.
 */
public class Movie implements Parcelable {

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

    //TODO: See if this is actually doable?
    public ArrayList<Movie> createMovieArrayFromJson(JSONArray jsonArray) {
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

}
