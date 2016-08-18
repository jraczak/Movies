package com.justinraczak.android.movies;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Justin on 5/20/16.
 */
public class MoviesApplication extends Application {

    private static final String LOG_TAG = MoviesApplication.class.getSimpleName();

    public static Context sContext;
    private static MoviesApplication applicationInstance;

    public static MoviesApplication getInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Application Subclass", "Assigning application context to sContext");
        applicationInstance = this;
        sContext = getApplicationContext();
        Log.d(LOG_TAG, "Set sContext to " + sContext);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("movies.realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
