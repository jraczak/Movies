package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;

public class MovieDetail extends Activity {

    private final String LOG_TAG = MovieDetail.class.getSimpleName();

    private String mYouTubeVideoId;
    private int mMovieId;
    private Realm realm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new MovieDetailFragment())
                            .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    //public boolean isFavorite(String id) {
    //    RealmQuery<FavoriteMovie> query = realm.where(FavoriteMovie.class);
    //    query.equalTo("movieId", id);
//
    //    Log.d(LOG_TAG, "Searching for favorite with id " + id);
//
    //    RealmResults<FavoriteMovie> results = query.findAll();
    //    Log.d(LOG_TAG, "Query returned " + results.size() + " results");
//
    //    if (results.size() == 0) {
    //        return false;
    //    }
    //    else {
    //        return true;
    //    }
    //}
}
