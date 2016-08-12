package com.justinraczak.android.movies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //MovieDetailFragment.//OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Movie mMovie;

    private int movieId;
    private String mYouTubeVideoId;
    private Realm realm;
    private View containerView;
    private Activity parentActivity;

    //private OnFragmentInteractionListener mListener;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MovieDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        args.putParcelable("mMovie", movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "In onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mMovie = getArguments().getParcelable("mMovie");
        }
        parentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(LOG_TAG, "Made it to onCreateView of MovieDetailFragment");
        containerView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        realm = Realm.getDefaultInstance();

        //TODO: Determine if mMovie is in favorites and update favorite button

        Log.d(LOG_TAG, getActivity().getIntent().toString());
        if (getActivity().getIntent() != null) {
            ImageView imageView = (ImageView) containerView.findViewById(R.id.movie_poster_image);
            Log.d(LOG_TAG, "ImageView is " + imageView);

            //TODO: Figure out how to get the list of movies

            //TODO: See if this conditional is needed anymore
            //if (savedInstanceState == null) {
            //    final Movie movie = this.mMovie;
            //} else {
            //        final Movie movie = getActivity().getIntent().getParcelableExtra("mMovie");
            //    }

            movieId = mMovie.getId();

            // Get the favorite button
            final Button favoriteButton = (Button) containerView.findViewById(R.id.favorite_movie_button);
            Log.d(LOG_TAG, "Favorite button is " + favoriteButton);

            // Update the button if the mMovie is already favorited
            //if (isFavorite(Integer.toString(mMovieId))) {
            if (mMovie.isFavorite()) {
                Log.d(LOG_TAG, "Movie is a favorite");
                favoriteButton.setText("FAVORITED");
                favoriteButton.setBackgroundColor(getResources().getColor(R.color.accent));
            }

            // Set up the click listener for the favorite button
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (isFavorite(Integer.toString(mMovieId))) {
                    if (mMovie.isFavorite()) {
                        removeMovieFromFavorites(Integer.toString(movieId), mMovie.title, favoriteButton);
                    } else {
                        addMovieToFavorites(Integer.toString(mMovie.id), mMovie.title, favoriteButton);
                    }
                }
            });

            imageView.setAdjustViewBounds(true);
            Picasso.with(getActivity()).load(mMovie.posterUrl).resize(185, 278).into(imageView);

            TextView titleTextView = (TextView) containerView.findViewById(R.id.movie_title);
            TextView releaseDateTextView = (TextView) containerView.findViewById(R.id.movie_release_date);
            TextView synopsisTextView = (TextView) containerView.findViewById(R.id.movie_synopsis);
            TextView voteAverageTextView = (TextView) containerView.findViewById(R.id.movie_vote_average);

            titleTextView.setText(mMovie.title);
            releaseDateTextView.setText(mMovie.releaseDate);
            voteAverageTextView.setText(mMovie.voteAverage + " out of 10");
            synopsisTextView.setText(mMovie.synopsis);

            Log.d("MovieDetail onCreate", "Attempting to fetch trailers");
            FetchTrailersTask fetchTrailersTask = new FetchTrailersTask();
            fetchTrailersTask.execute(mMovie);

            Log.d("MovieDetail onCreate", "Attempting to fetch reviews");
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask();
            fetchReviewsTask.execute(mMovie);

        }
        else {
            Toast.makeText(getActivity(), "Sorry, we couldn't load the mMovie.", Toast.LENGTH_LONG).show();
        }

        return containerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    //public void onButtonPressed(Uri uri) {
    //    if (mListener != null) {
    //        mListener.onFragmentInteraction(uri);
    //    }
    //}

    public void updateMovieDetails(Movie movie) {

        Button favoriteButton = (Button) getActivity().findViewById(R.id.favorite_movie_button);
        TextView titleTextView = (TextView) getActivity().findViewById(R.id.movie_title);
        TextView releaseDateTextView = (TextView) getActivity().findViewById(R.id.movie_release_date);
        TextView synopsisTextView = (TextView) getActivity().findViewById(R.id.movie_synopsis);
        TextView voteAverageTextView = (TextView) getActivity().findViewById(R.id.movie_vote_average);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.movie_poster_image);
        Boolean favorite = movie.isFavorite();

        titleTextView.setText(movie.title);
        releaseDateTextView.setText(movie.releaseDate);
        voteAverageTextView.setText(movie.voteAverage + " out of 10");
        synopsisTextView.setText(movie.synopsis);
        imageView.setAdjustViewBounds(true);
        Picasso.with(getActivity()).load(movie.posterUrl).resize(185, 278).into(imageView);

        if (favorite) {
            Log.d(LOG_TAG, "Movie is a favorite");
            favoriteButton.setText("FAVORITED");
            favoriteButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        //TODO: Get the favorite button wired up to add and remove favorites
    }

    public void addMovieToFavorites(String id, String movieName, Button button) {
        RealmQuery<FavoriteMovie> query = realm.where(FavoriteMovie.class);
        query.equalTo("movieId", id);
        RealmResults<FavoriteMovie> results = query.findAll();

        if (results.size() == 0) {


            realm.beginTransaction();

            Log.d(LOG_TAG, "Begin creating favorite mMovie");
            FavoriteMovie favoriteMovie = realm.createObject(FavoriteMovie.class);
            Log.d(LOG_TAG, "Assign UUID and ID");
            favoriteMovie.setId(UUID.randomUUID().toString());
            favoriteMovie.setMovieId(id);

            realm.commitTransaction();
            realm.close();

            Toast.makeText(getActivity(), "Added " + movieName + " to favorites",
                    Toast.LENGTH_LONG).show();

            button.setText("FAVORITED");
            button.setBackgroundColor(getResources().getColor(R.color.accent));
        }
        else {
            Toast.makeText(getActivity(), movieName + " already favorited",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void removeMovieFromFavorites(String id, String movieName, Button button) {
        realm.beginTransaction();
        RealmQuery<FavoriteMovie> query = realm.where(FavoriteMovie.class);
        query.equalTo("movieId", id);
        RealmResults<FavoriteMovie> results = query.findAll();
        FavoriteMovie favoriteMovie = results.get(0);
        favoriteMovie.deleteFromRealm();
        realm.commitTransaction();

        button.setBackgroundColor(getResources().getColor(R.color.secondary_text));
        button.setText("ADD TO FAVORITES");

        Toast.makeText(getActivity(), movieName + " removed from favorites",
                Toast.LENGTH_LONG).show();
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
            Button trailerButton = new Button(getActivity());
            LinearLayout parentLayout = (LinearLayout) containerView.findViewById(R.id.movie_detail_linear_layout);

            // This wasn't really working, so see how it displays without it
            //ViewGroup.LayoutParams layoutParams = trailerButton.getLayoutParams();
            //layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            //layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

            trailerButton.setText("WATCH THE TRAILER");
            trailerButton.setTextColor(getActivity().getResources().getColor(R.color.icons));
            trailerButton.setBackgroundColor(getActivity().getResources().getColor(R.color.accent));
            trailerButton.setPadding(8, 0, 8, 0);
            trailerButton.setMinHeight(48);
            //trailerButton.setLayoutParams(layoutParams);

            final Uri youTubeUrl = buildYouTubeUrl(result);

            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(youTubeUrl);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getActivity(),
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

                // Create the mMovie review object
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
                        .appendEncodedPath(Integer.toString(movieId))
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
                LinearLayout parentView = (LinearLayout) containerView.findViewById(R.id.reviews_container);

                String reviewString;
                reviewString = review.author + " says"
                        + "\n"
                        + '"' + review.content + '"';

                CardView cardView = (CardView) getActivity().getLayoutInflater().inflate(R.layout.card_movie_review, parentView, false);

                TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.fragment_movie_review, parentView, false);

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



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    //public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    //    void onFragmentInteraction(Uri uri);
    //}
}
