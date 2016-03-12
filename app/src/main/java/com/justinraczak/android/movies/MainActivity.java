package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Arrays;

public class MainActivity extends Activity {

    private ImageAdapter mImageAdapter;
    private ArrayList<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlList = new ArrayList<>();
        //urls.addAll("I WILL BE A COLLECTION")//
        //urls.add("http://hawaii.kauai.com/images/guide-to-kauai.jpg");
        //urls.add("http://www.eventscr.com/wp-content/uploads/2011/01/arenal-volcano-daytime.jpg");
        //urls.add("http://www.hawaiilife.com/articles/wp-content/uploads/2012/10/kauai.jpg");
        //urls.add("http://www.kauailandmark.com/images/kauai_beach_scene_488_01.jpg");
        //urls.add("http://adventureinhawaii.com/site/wp-content/uploads/2014/03/waialeale-crater.jpg");
        //urls.add("https://luckywelive808.com/wp-content/uploads/2015/02/kauai-beaches.jpg");
        // Instantiate an ImageAdapter and pass it the context, number of URLs, and array of URLs

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();



        mImageAdapter = new ImageAdapter(this, urlList.size(), urlList);
        // Grab the GridView for displaying the images
        GridView gridView = (GridView) findViewById(R.id.gridview);
        // Attach the ImageAdapter to the GridView
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Grab the image that's displayed in the selected ImageView
                Bitmap image = ((BitmapDrawable)((ImageView) view).getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // Compress the image and pass the bytes into the output stream
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                // Pass the compressed bytes into a byte array
                byte[] b = byteArrayOutputStream.toByteArray();

                // Save the image to file so there isn't a large image in the Intent
                String fileName = "image" + position;
                try {
                    FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                    fileOutputStream.write(b);
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Send the byte array to the MovieDetail activity to be uncompressed
                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra("poster_image_filename", fileName);
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

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        // Manipulate the retrieved JSON to traverse objects and pull poster URLs
        private String[] getMovieDataFromJson(String moviesJsonString, int numberOfPages)
            throws JSONException {

            // List of JSON leaf nodes that need to be pulled from the result
            final String TMD_RESULTS = "results";
            final String TMD_POSTER_PATH = "poster_path";
            final String TMD_ID = "id";
            final String TMD_TITLE = "title";

            // Parameter values to construct the poster url
            String IMAGE_SIZE = "w185";
            String BASE_URL = "http://image.tmdb.org/t/p/";

            // Instantiate a JSONObject from the JSON string
            JSONObject moviesJson = new JSONObject(moviesJsonString);
            // Convert the results object into a searchable JSONArray
            JSONArray moviesArray = moviesJson.getJSONArray(TMD_RESULTS);

            String[] posterUrlStrings = new String[moviesArray.length()];

            for (int i=0; i < moviesArray.length(); i++) {
                String posterUrl;

                JSONObject movieObject = moviesArray.getJSONObject(i);

                posterUrl = BASE_URL + IMAGE_SIZE + "/" + movieObject.getString(TMD_POSTER_PATH);

                posterUrlStrings[i] = posterUrl;
            }

            for (String s: posterUrlStrings) {
                Log.d(LOG_TAG, "Poster URL: " + s);
            }
            return posterUrlStrings;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String moviesJsonString = null;
            int numberofPages = 1;

            try {
                final String API_URL = "https://api.themoviedb.org/3/movie/popular?";
                final String PAGE_PARAM = "page";
                final String API_PARAM = "api_key";


                Uri builtUri = Uri.parse(API_URL).buildUpon()
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(numberofPages))
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
                return getMovieDataFromJson(moviesJsonString, numberofPages);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //super.onPostExecute(aVoid);
            if (result != null) {
                urlList.clear();
                urlList.addAll(Arrays.asList(result));
                mImageAdapter.notifyDataSetChanged();
            }
        }
    }

}
