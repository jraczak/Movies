package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private ImageAdapter mImageAdapter;
    private ArrayList<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<>();

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();

        Log.d("onCreate", "ArrayList size is " + moviesList.size());

        // Instantiate an ImageAdapter and pass it the context, number of movies, and array of movie objects
        mImageAdapter = new ImageAdapter(this, moviesList.size(), moviesList);
        // Grab the GridView for displaying the images
        GridView gridView = (GridView) findViewById(R.id.gridview);
        // Attach the ImageAdapter to the GridView
        Log.d("onCreate", "Setting the adapter on the grid view");
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO: Copy this image compression code out to a snippet
                // Grab the image that's displayed in the selected ImageView
                //    Bitmap image = ((BitmapDrawable)((ImageView) view).getDrawable()).getBitmap();
                //    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // Compress the image and pass the bytes into the output stream
                //    image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                // Pass the compressed bytes into a byte array
                //    byte[] b = byteArrayOutputStream.toByteArray();

                // Save the image to file so there isn't a large image in the Intent
                //    String fileName = "image" + position;
                //    try {
                //        FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                //        fileOutputStream.write(b);
                //        fileOutputStream.close();
                //    } catch (IOException e) {
                //        e.printStackTrace();
                //    }

                // Send the byte array to the MovieDetail activity to be uncompressed
                //    Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                //    intent.putExtra("poster_image_filename", fileName);
                //    startActivity(intent);

                //TODO: Remove this toast
                Toast.makeText(getApplicationContext(), "The item position is " + position, Toast.LENGTH_SHORT).show();

                Movie m = (Movie) mImageAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra("movie", m);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

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

                //TODO: Review the old loop after moving to objects
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

            try {
                final String API_URL = "https://api.themoviedb.org/3/movie/popular?";
                final String PAGE_PARAM = "page";
                final String API_PARAM = "api_key";


                Uri builtUri = Uri.parse(API_URL).buildUpon()
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
                mImageAdapter.notifyDataSetChanged();
            }
        }
    }

}
