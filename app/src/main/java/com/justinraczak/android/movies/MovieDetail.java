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

import java.io.File;

public class MovieDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (this.getIntent() != null) {
            ImageView imageView = (ImageView) findViewById(R.id.movie_poster_image);
            //imageView.setImageResource(getIntent().getStringExtra("image_id"));

            Bundle extras = getIntent().getExtras();
            String fileName = extras.getString("poster_image_filename");
            //byte[] b = extras.getByteArray("poster_image_bytes");

            File filePath = getFileStreamPath(fileName);
            Drawable drawable = Drawable.createFromPath(filePath.toString());

            //Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(imageView, "2dp", "3dp");
            imageView.setBackground(drawable);
            //imageView.setLayoutParams();
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
