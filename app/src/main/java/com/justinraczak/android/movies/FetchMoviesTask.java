package com.justinraczak.android.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Justin on 5/29/16.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private ArrayList<Movie> moviesList;
    private ImageAdapter imageAdapter;
    private Context context;

    public FetchMoviesTask(Context context, ArrayList<Movie> moviesList, ImageAdapter imageAdapter) {
        this.moviesList = moviesList;
        this.imageAdapter = imageAdapter;
        this.context = context;
    }

    // Manipulate the retrieved JSON to traverse objects and pull poster URLs
    private ArrayList<Movie> getMovieDataFromJson(String moviesJsonString, int numberOfPages)
            throws JSONException {

        // List of JSON leaf nodes that need to be pulled from the result
        final String TMD_RESULTS = "results";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_ID = "id";
        final String TMD_TITLE = "title";
        final String TMD_SYNOPSIS = "overview";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_VOTE_AVERAGE = "vote_average";

        // Parameter values to construct the poster url
        String IMAGE_SIZE = "w185";
        String BASE_URL = "http://image.tmdb.org/t/p/";

        // Instantiate a JSONObject from the JSON string
        JSONObject moviesJson = new JSONObject(moviesJsonString);
        // Convert the results object into a searchable JSONArray
        JSONArray moviesArray = moviesJson.getJSONArray(TMD_RESULTS);

        //TODO: Is this string array still necessary when using objects?
        //String[] posterUrlStrings = new String[moviesArray.length()];

        // An ArrayList to hold the movie objects
        ArrayList<Movie> moviesArrayList = new ArrayList<>();

        for (int i=0; i < moviesArray.length(); i++) {

            //TODO: Remove the old code when using image URLs only
            //String posterUrl;
            //JSONObject movieObject = moviesArray.getJSONObject(i);
            //posterUrl = BASE_URL + IMAGE_SIZE + "/" + movieObject.getString(TMD_POSTER_PATH);
            //posterUrlStrings[i] = posterUrl;

            // New loop using Movie objects instead of a string array
            JSONObject movieObject = moviesArray.getJSONObject(i);
            int id = movieObject.getInt(TMD_ID);
            String title = movieObject.getString(TMD_TITLE);
            String synopsis = movieObject.getString(TMD_SYNOPSIS);
            String releaseDate = movieObject.getString(TMD_RELEASE_DATE);
            String voteAverage = movieObject.getString(TMD_VOTE_AVERAGE);
            String posterUrl = BASE_URL + IMAGE_SIZE + "/" + movieObject.getString(TMD_POSTER_PATH);

            Movie movie = new Movie(id, title, releaseDate, synopsis, voteAverage, posterUrl);
            movie.formatDateForDisplay();

            moviesArrayList.add(movie);
        }

        //for (String s: posterUrlStrings) {
        for (Movie m: moviesArrayList) {
            Log.d(LOG_TAG, "Built movie: " + m.title);
        }
        //TODO: Remove this old return statement
        //return posterUrlStrings;
        return moviesArrayList;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String moviesJsonString = null;
        int numberOfPages = 1;

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
        String sortPreference = sharedPreferences.getString("sort_order", "popular");

        try {
            final String API_URL = "https://api.themoviedb.org/3/movie/" + sortPreference + "?";
            final String PAGE_PARAM = "page";
            final String API_PARAM = "api_key";


            Uri builtUri = Uri.parse(API_URL + sortPreference).buildUpon()
                    .appendQueryParameter(PAGE_PARAM, Integer.toString(numberOfPages))
                    .appendQueryParameter(API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "Built uri: " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                // No results, then do nothing
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0) {
                // The stream has no content, so do not attempt to parse
                return null;
            }
            moviesJsonString = stringBuffer.toString();
            Log.d(LOG_TAG, "JSON: " + moviesJsonString);
        }  catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader !=  null) {
                try {
                    bufferedReader.close();
                }  catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        }
        try {
            return getMovieDataFromJson(moviesJsonString, numberOfPages);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> result) {
        //super.onPostExecute(aVoid);
        if (result != null) {
            moviesList.clear();

            Log.d(LOG_TAG, "Pushing results to moviesList array list");
            for (Movie m: result) {
                moviesList.add(m);
            }

            Log.d(LOG_TAG, "Notifying adapter data set has changed");
            imageAdapter.notifyDataSetChanged();
        }
    }
}
