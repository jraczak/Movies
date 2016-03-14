package com.justinraczak.android.movies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class MovieDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (this.getIntent() != null) {
            ImageView imageView = (ImageView) findViewById(R.id.movie_poster_image);


        //    Bundle extras = getIntent().getExtras();
        //    String fileName = extras.getString("poster_image_filename");


        //    File filePath = getFileStreamPath(fileName);
        //    Drawable drawable = Drawable.createFromPath(filePath.toString());


        //    imageView.setBackground(drawable);

            Movie movie = (Movie) getIntent().getParcelableExtra("movie");

            imageView.setAdjustViewBounds(true);
            Picasso.with(this).load(movie.posterUrl).resize(185, 278).into(imageView);

            TextView titleTextView = (TextView) findViewById(R.id.movie_title);
            TextView releaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
            TextView synopsisTextView = (TextView) findViewById(R.id.movie_synopsis);

            titleTextView.setText(movie.title);
            releaseDateTextView.setText(movie.releaseDate);
            synopsisTextView.setText(movie.synopsis);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
