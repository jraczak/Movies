package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;

public class MainActivity extends Activity
implements MovieListFragment.MovieCallbackInterface {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    // To hold the first movie returned from the fetched collection
    private Movie firstMovie;

    //TODO: See if these belong here or in the fragment class
    //private ImageAdapter mImageAdapter;
    //private ArrayList<Movie> moviesList;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // Detail pane is only present in large screen layouts
            // If its present, the activity should run in two pane mode
            mTwoPane = true;

            // In two pane mode, show detail in this activity by adding
            // the detail fragment
            if (savedInstanceState == null) {
                Log.d(LOG_TAG, "First movie is " + firstMovie);
                MovieDetailFragment fragment = MovieDetailFragment.newInstance(firstMovie);
                getFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment
                        , DETAILFRAGMENT_TAG)
                        .commit();
            }
        }  else {
            mTwoPane = false;
        }

        realm = Realm.getDefaultInstance();

       //moviesList = new ArrayList<>();

       //FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(this, moviesList, mImageAdapter);
       //fetchMoviesTask.execute();

       //Log.d("onCreate", "ArrayList size is " + moviesList.size());

       //// Instantiate an ImageAdapter and pass it the context, number of movies, and array of movie objects
       //mImageAdapter = new ImageAdapter(this, moviesList.size(), moviesList);
       //// Grab the GridView for displaying the images
       //GridView gridView = (GridView) findViewById(R.id.gridview);
       //// Attach the ImageAdapter to the GridView
       //Log.d(LOG_TAG, "GridView is: " + gridView.toString());
       //Log.d("onCreate", "Setting the adapter on the grid view");
       //gridView.setAdapter(mImageAdapter);

       //gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       //    @Override
       //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

       //        //TODO: Copy this image compression code out to a snippet for future use cases
       //        // Grab the image that's displayed in the selected ImageView
       //        //    Bitmap image = ((BitmapDrawable)((ImageView) view).getDrawable()).getBitmap();
       //        //    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       //        // Compress the image and pass the bytes into the output stream
       //        //    image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
       //        // Pass the compressed bytes into a byte array
       //        //    byte[] b = byteArrayOutputStream.toByteArray();

       //        // Save the image to file so there isn't a large image in the Intent
       //        //    String fileName = "image" + position;
       //        //    try {
       //        //        FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
       //        //        fileOutputStream.write(b);
       //        //        fileOutputStream.close();
       //        //    } catch (IOException e) {
       //        //        e.printStackTrace();
       //        //    }

       //        // Send the byte array to the MovieDetail activity to be uncompressed
       //        //    Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
       //        //    intent.putExtra("poster_image_filename", fileName);
       //        //    startActivity(intent);

       //        //TODO: Remove this debugging toast
       //        //Toast.makeText(getApplicationContext(), "The item position is " + position, Toast.LENGTH_SHORT).show();

       //        Movie m = (Movie) mImageAdapter.getItem(position);
       //        Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
       //        intent.putExtra("movie", m);
       //        startActivity(intent);
       //    }
       //});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.sort_menu, menu);
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

    public void onMovieSelected(Movie movie) {

        MovieDetailFragment detailFragment = (MovieDetailFragment)
                getFragmentManager().findFragmentById(R.id.movie_detail_container);

        if (mTwoPane) {
            detailFragment.updateMovieDetails(movie);
        } else {
                    Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                    intent.putExtra("movie", movie);
                    startActivity(intent);
        }
    }

    public void setFirstMovie(Movie movie) {
        firstMovie = movie;
    }



    //public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
//
    //    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
//
    //    // Manipulate the retrieved JSON to traverse objects and pull poster URLs
    //    private ArrayList<Movie> getMovieDataFromJson(String moviesJsonString, int numberOfPages)
    //        throws JSONException {
//
    //        // List of JSON leaf nodes that need to be pulled from the result
    //        final String TMD_RESULTS = "results";
    //        final String TMD_POSTER_PATH = "poster_path";
    //        final String TMD_ID = "id";
    //        final String TMD_TITLE = "title";
    //        final String TMD_SYNOPSIS = "overview";
    //        final String TMD_RELEASE_DATE = "release_date";
    //        final String TMD_VOTE_AVERAGE = "vote_average";
//
    //        // Parameter values to construct the poster url
    //        String IMAGE_SIZE = "w185";
    //        String BASE_URL = "http://image.tmdb.org/t/p/";
//
    //        // Instantiate a JSONObject from the JSON string
    //        JSONObject moviesJson = new JSONObject(moviesJsonString);
    //        // Convert the results object into a searchable JSONArray
    //        JSONArray moviesArray = moviesJson.getJSONArray(TMD_RESULTS);
//
    //        //TODO: Is this string array still necessary when using objects?
    //        //String[] posterUrlStrings = new String[moviesArray.length()];
//
    //        // An ArrayList to hold the movie objects
    //        ArrayList<Movie> moviesArrayList = new ArrayList<>();
//
    //        for (int i=0; i < moviesArray.length(); i++) {
//
    //            //TODO: Remove the old code when using image URLs only
    //            //String posterUrl;
    //            //JSONObject movieObject = moviesArray.getJSONObject(i);
    //            //posterUrl = BASE_URL + IMAGE_SIZE + "/" + movieObject.getString(TMD_POSTER_PATH);
    //            //posterUrlStrings[i] = posterUrl;
//
    //            // New loop using Movie objects instead of a string array
    //            JSONObject movieObject = moviesArray.getJSONObject(i);
    //            int id = movieObject.getInt(TMD_ID);
    //            String title = movieObject.getString(TMD_TITLE);
    //            String synopsis = movieObject.getString(TMD_SYNOPSIS);
    //            String releaseDate = movieObject.getString(TMD_RELEASE_DATE);
    //            String voteAverage = movieObject.getString(TMD_VOTE_AVERAGE);
    //            String posterUrl = BASE_URL + IMAGE_SIZE + "/" + movieObject.getString(TMD_POSTER_PATH);
//
    //            Movie movie = new Movie(id, title, releaseDate, synopsis, voteAverage, posterUrl);
    //            movie.formatDateForDisplay();
//
    //            moviesArrayList.add(movie);
    //        }
//
    //        //for (String s: posterUrlStrings) {
    //        for (Movie m: moviesArrayList) {
    //            Log.d(LOG_TAG, "Built movie: " + m.title);
    //        }
    //        //TODO: Remove this old return statement
    //        //return posterUrlStrings;
    //        return moviesArrayList;
    //    }
//
    //    @Override
    //    protected ArrayList doInBackground(Void... params) {
//
    //        HttpURLConnection urlConnection = null;
    //        BufferedReader bufferedReader = null;
    //        String moviesJsonString = null;
    //        int numberOfPages = 1;
//
    //        String sortPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
    //                .getString(getString(R.string.prefs_key_sort_order),
    //                        getString(R.string.prefs_default_sort_order));
//
    //        try {
    //            final String API_URL = "https://api.themoviedb.org/3/movie/" + sortPreference + "?";
    //            final String PAGE_PARAM = "page";
    //            final String API_PARAM = "api_key";
//
//
    //            Uri builtUri = Uri.parse(API_URL + sortPreference).buildUpon()
    //                    .appendQueryParameter(PAGE_PARAM, Integer.toString(numberOfPages))
    //                    .appendQueryParameter(API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
    //                    .build();
//
    //            URL url = new URL(builtUri.toString());
    //            Log.d(LOG_TAG, "Built uri: " + builtUri.toString());
//
    //            urlConnection = (HttpURLConnection) url.openConnection();
    //            urlConnection.setRequestMethod("GET");
    //            urlConnection.connect();
//
    //            InputStream inputStream = urlConnection.getInputStream();
    //            StringBuffer stringBuffer = new StringBuffer();
    //            if (inputStream == null) {
    //                // No results, then do nothing
    //                return null;
    //            }
    //            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
    //            String line;
    //            while ((line = bufferedReader.readLine()) != null) {
    //                stringBuffer.append(line + "\n");
    //            }
//
    //            if (stringBuffer.length() == 0) {
    //                // The stream has no content, so do not attempt to parse
    //                return null;
    //            }
    //            moviesJsonString = stringBuffer.toString();
    //            Log.d(LOG_TAG, "JSON: " + moviesJsonString);
    //        }  catch (IOException e) {
    //            Log.e(LOG_TAG, e.toString());
    //        } finally {
    //            if (urlConnection != null) {
    //                urlConnection.disconnect();
    //            }
    //            if (bufferedReader !=  null) {
    //                try {
    //                    bufferedReader.close();
    //                }  catch (IOException e) {
    //                    Log.e(LOG_TAG, e.toString());
    //                }
    //            }
    //        }
    //        try {
    //            return getMovieDataFromJson(moviesJsonString, numberOfPages);
    //        } catch (JSONException e) {
    //            Log.e(LOG_TAG, e.getMessage(), e);
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }
//
    //    @Override
    //    protected void onPostExecute(ArrayList<Movie> result) {
    //        //super.onPostExecute(aVoid);
    //        if (result != null) {
    //            moviesList.clear();
//
    //            Log.d(LOG_TAG, "Pushing results to moviesList array list");
    //            for (Movie m: result) {
    //                moviesList.add(m);
    //            }
//
    //            Log.d(LOG_TAG, "Notifying adapter data set has changed");
    //            mImageAdapter.notifyDataSetChanged();
    //        }
    //    }
    //}
//
    @Override
    protected void onResume() {
        super.onResume();

        //TODO: Have to assign the fragment to a variable here. Need fragment first
        MovieListFragment movieListFragment = (MovieListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_list_movies);
    }
}
