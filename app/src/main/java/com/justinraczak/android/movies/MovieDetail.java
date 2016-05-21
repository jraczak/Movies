package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MovieDetail extends Activity {

    private final String LOG_TAG = MovieDetail.class.getSimpleName();

    private String mYouTubeVideoId;
    private int mMovieId;
    private Realm realm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        realm = Realm.getDefaultInstance();

        //TODO: Determine if movie is in favorites and update favorite button

        if (this.getIntent() != null) {
            ImageView imageView = (ImageView) findViewById(R.id.movie_poster_image);

            final Movie movie = getIntent().getParcelableExtra("movie");

            mMovieId = movie.id;

            // Get the favorite button
            final Button favoriteButton = (Button) findViewById(R.id.favorite_movie_button);

            // Update the button if the movie is already favorited
            if (isFavorite(Integer.toString(mMovieId))) {
                favoriteButton.setText("FAVORITED");
                favoriteButton.setBackgroundColor(getResources().getColor(R.color.accent));
            }

            // Set up the click listener for the favorite button
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavorite(Integer.toString(mMovieId))) {
                        removeMovieFromFavorites(Integer.toString(mMovieId), favoriteButton);
                    } else {
                        addMovieToFavorites(Integer.toString(movie.id), favoriteButton);
                    }
                }
            });

            imageView.setAdjustViewBounds(true);
            Picasso.with(this).load(movie.posterUrl).resize(185, 278).into(imageView);

            TextView titleTextView = (TextView) findViewById(R.id.movie_title);
            TextView releaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
            TextView synopsisTextView = (TextView) findViewById(R.id.movie_synopsis);
            TextView voteAverageTextView = (TextView) findViewById(R.id.movie_vote_average);

            titleTextView.setText(movie.title);
            releaseDateTextView.setText(movie.releaseDate);
            voteAverageTextView.setText(movie.voteAverage + " out of 10");
            synopsisTextView.setText(movie.synopsis);

            Log.d("MovieDetail onCreate", "Attempting to fetch trailers");
            FetchTrailersTask fetchTrailersTask = new FetchTrailersTask();
            fetchTrailersTask.execute(movie);

            Log.d("MovieDetail onCreate", "Attempting to fetch reviews");
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask();
            fetchReviewsTask.execute(movie);

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

    public class MovieReview {

        String author;
        String content;

        public MovieReview(String author, String content) {

            this.author = author;
            this.content = content;

        }
    }

    public Uri buildYouTubeUrl(String videoId) {

        final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
        final String VIDEO_PARAM = "v";

        Uri youTubeUrl = Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendQueryParameter(VIDEO_PARAM, videoId)
                .build();
        Log.d("YouTube Url Builder", "built uri: " + youTubeUrl.toString());

        return youTubeUrl;
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
        final String API_VIDEOS_PARAM = "videos";
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
                Log.d(LOG_TAG, "buffered reader value: " + inputStream.toString());

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                Log.d(LOG_TAG, "string buffer value: " + stringBuffer.toString());
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

            mYouTubeVideoId = result;
            Button trailerButton = new Button(getApplicationContext());
            LinearLayout parentLayout = (LinearLayout) findViewById(R.id.movie_detail_linear_layout);

            // This wasn't really working, so see how it displays without it
            //ViewGroup.LayoutParams layoutParams = trailerButton.getLayoutParams();
            //layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            //layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            trailerButton.setText("WATCH THE TRAILER");
            trailerButton.setTextColor(getApplication().getResources().getColor(R.color.icons));
            trailerButton.setBackgroundColor(getApplication().getResources().getColor(R.color.accent));
            trailerButton.setPadding(8, 0, 8, 0);
            trailerButton.setMinHeight(48);
            //trailerButton.setLayoutParams(layoutParams);

            final Uri youTubeUrl = buildYouTubeUrl(result);

            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(youTubeUrl);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, we can't load this trailer right now.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            parentLayout.addView(trailerButton);

        }
    }


    public class FetchReviewsTask extends AsyncTask<Movie, Void, ArrayList<MovieReview>> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private ArrayList<MovieReview> getReviewsFromJson(String apiReviewsJsonString)
                throws JSONException {

            final String TMD_RESULTS = "results";
            final String TMD_AUTHOR = "author";
            final String TMD_CONTENT = "content";

            // Create a JSON object
            JSONObject reviewsJsonObject = new JSONObject(apiReviewsJsonString);

            // Turn the JSON object into a searchable JSON array
            JSONArray reviewsArray = reviewsJsonObject.getJSONArray(TMD_RESULTS);

            // Create an ArrayList to hold the review objects
            ArrayList<MovieReview> reviewsArrayList = new ArrayList<>();

            // Loop through the array of review JSON and create review objects
            for (int i = 0; i < reviewsArray.length(); i++) {
                // Create a JSON object from each node in the array
                JSONObject reviewObject = reviewsArray.getJSONObject(i);

                // Load the JSON values into variables
                String author = reviewObject.getString(TMD_AUTHOR);
                String content = reviewObject.getString(TMD_CONTENT);

                // Create the movie review object
                MovieReview review = new MovieReview(author, content);

                // Add the review to the ArrayList
                reviewsArrayList.add(review);
            }
            for (MovieReview r : reviewsArrayList) {
                Log.d("Create reviews", "Review: " + r.content);
            }

            return reviewsArrayList;
        }

        @Override
        protected ArrayList<MovieReview> doInBackground(Movie... params) {
            final String LOG_TAG = "FetchReviewsTask, doInBackground";


            final String API_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_REVIEWS_PARAM = "reviews";
            final String API_KEY_PARAM = "api_key";

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String apiReviewsJsonString = null;

            try {

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendEncodedPath(Integer.toString(mMovieId))
                        .appendEncodedPath(API_REVIEWS_PARAM)
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
                Log.d(LOG_TAG, "buffered reader value: " + inputStream.toString());

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                Log.d(LOG_TAG, "string buffer value: " + stringBuffer.toString());
                if (stringBuffer.length() == 0) {
                    // Stream didn't get any content, so don't attempt any further processing
                    return null;
                }

                apiReviewsJsonString = stringBuffer.toString();
                Log.d(LOG_TAG, "JSON string: " + apiReviewsJsonString);
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
                return getReviewsFromJson(apiReviewsJsonString);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieReview> movieReviews) {
            //super.onPostExecute(movieReviews);

            for (int i=0; i<movieReviews.size(); i++) {
                MovieReview review = movieReviews.get(i);
                LinearLayout parentView = (LinearLayout) findViewById(R.id.reviews_container);

                String reviewString;
                reviewString = review.author + " says"
                        + "\n"
                        + '"' + review.content + '"';

                CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.card_movie_review, parentView, false);

                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.fragment_movie_review, parentView, false);

                //TextView textView = new TextView(getApplicationContext());
                //textView.setTextColor(getApplication().getResources().getColor(R.color.icons));
                textView.setText(reviewString);

                //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                //Log.d("Reviews postExecute", params.toString());
                //params.setMargins(0, 8, 0, 0);
                //textView.setLayoutParams(params);

                cardView.addView(textView);

                parentView.addView(cardView);
            }
        }
    }

    public void addMovieToFavorites(String id, Button button) {
        RealmQuery<FavoriteMovie> query = realm.where(FavoriteMovie.class);
        query.equalTo("movieId", id);
        RealmResults<FavoriteMovie> results = query.findAll();

        if (results.size() == 0) {


            realm.beginTransaction();

            Log.d(LOG_TAG, "Begin creating favorite movie");
            FavoriteMovie favoriteMovie = realm.createObject(FavoriteMovie.class);
            Log.d(LOG_TAG, "Assign UUID and ID");
            favoriteMovie.setId(UUID.randomUUID().toString());
            favoriteMovie.setMovieId(id);

            realm.commitTransaction();
            realm.close();

            Toast.makeText(getApplicationContext(), "Added movie to favorites", Toast.LENGTH_LONG)
                    .show();

            button.setText("FAVORITED");
            button.setBackgroundColor(getResources().getColor(R.color.accent));
        }
        else {
            Toast.makeText(getApplicationContext(), "Movie already favorited", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void removeMovieFromFavorites(String id, Button button) {
        realm.beginTransaction();
        RealmQuery<FavoriteMovie> query = realm.where(FavoriteMovie.class);
        query.equalTo("movieId", id);
        RealmResults<FavoriteMovie> results = query.findAll();
        FavoriteMovie favoriteMovie = results.get(0);
        favoriteMovie.deleteFromRealm();
        realm.commitTransaction();

        button.setBackgroundColor(getResources().getColor(R.color.secondary_text));
        button.setText("ADD TO FAVORITES");

        Toast.makeText(getApplicationContext(), "Movie removed from favorites", Toast.LENGTH_LONG)
                .show();
    }

    public boolean isFavorite(String id) {
        RealmQuery<FavoriteMovie> query = realm.where(FavoriteMovie.class);
        query.equalTo("movieId", id);

        Log.d(LOG_TAG, "Searching for favorite with id " + id);

        RealmResults<FavoriteMovie> results = query.findAll();
        Log.d(LOG_TAG, "Query returned " + results.size() + " results");

        if (results.size() == 0) {
            return false;
        }
        else {
            return true;
        }
    }
}
