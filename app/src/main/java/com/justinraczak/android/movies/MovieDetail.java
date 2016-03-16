package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (this.getIntent() != null) {
            ImageView imageView = (ImageView) findViewById(R.id.movie_poster_image);

            Movie movie = getIntent().getParcelableExtra("movie");

            imageView.setAdjustViewBounds(true);
            Picasso.with(this).load(movie.posterUrl).resize(185, 278).into(imageView);

            TextView titleTextView = (TextView) findViewById(R.id.movie_title);
            TextView releaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
            TextView synopsisTextView = (TextView) findViewById(R.id.movie_synopsis);
            TextView voteAverageTextView = (TextView) findViewById(R.id.movie_vote_average);

            titleTextView.setText(movie.title);
            releaseDateTextView.setText("Released " +movie.releaseDate);
            voteAverageTextView.setText("Rated " + movie.voteAverage + " out of 10");
            synopsisTextView.setText(movie.synopsis);

            FetchTrailersTask fetchTrailersTask = new FetchTrailersTask();
            fetchTrailersTask.execute(movie);

        }
        else {
            Toast.makeText(this, "Sorry, we couldn't load the movie.", Toast.LENGTH_LONG).show();
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

    private String getVideoIdFromJson(String apiVideosJsonString)
            throws JSONException {

        // Prepare the values for parsing the JSON data
        final String TMD_RESULTS = "results";
        final String TMD_VIDEO_ID = "key";

        // Put a JSON object together from the JSON string
        JSONObject videosObject = new JSONObject(apiVideosJsonString);

        // Convert the JSON object into a searchable array from results on down
        JSONArray videosArray = videosObject.getJSONArray(TMD_RESULTS);

        // For now let's just grab the first video and assume it's the trailer
        JSONObject video = videosArray.getJSONObject(0);

        // Return a string that is the YouTube video ID
        return video.getString(TMD_VIDEO_ID);

    }

    public class FetchTrailersTask extends AsyncTask<Movie, Void, String> {
        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";
        final String API_VIDEOS_PARAM = "videos?";
        final String API_KEY_PARAM = "api_key";


        @Override
        protected String doInBackground(Movie... params) {

            final int MOVIE_ID = params[0].id;
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String apiVideosJsonString = null;

            try {

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendEncodedPath(Integer.toString(MOVIE_ID))
                        .appendEncodedPath(API_VIDEOS_PARAM)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.d(LOG_TAG, "Built uri: " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {
                    // We didn't get anything back, so don't bother doing anything else
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    // Stream didn't get any content, so don't attempt any further processing
                    return null;
                }

                apiVideosJsonString = stringBuffer.toString();
                Log.d(LOG_TAG, "JSON string: " + apiVideosJsonString);
            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.d(LOG_TAG, e.getMessage());
                    }
                }
            }
            try {
                return getVideoIdFromJson(apiVideosJsonString);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // super.onPostExecute(result);

            Button trailerButton = new Button(getApplicationContext());
            LinearLayout parentLayout = (LinearLayout) findViewById(R.id.movie_detail_linear_layout);

            ViewGroup.LayoutParams layoutParams = trailerButton.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            trailerButton.setText("WATCH THE TRAILER");
            trailerButton.setTextColor(getApplication().getResources().getColor(R.color.icons));
            trailerButton.setBackgroundColor(getApplication().getResources().getColor(R.color.accent));
            trailerButton.setPadding(8, 0, 8, 0);
            trailerButton.setMinHeight(48);
            trailerButton.setLayoutParams(layoutParams);

            parentLayout.addView(trailerButton);

        }
    }
}
