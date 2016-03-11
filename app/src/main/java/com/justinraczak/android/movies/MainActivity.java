package com.justinraczak.android.movies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> urls = new ArrayList<>();
        //urls.addAll("I WILL BE A COLLECTION")//
        urls.add("http://hawaii.kauai.com/images/guide-to-kauai.jpg");
        urls.add("http://www.eventscr.com/wp-content/uploads/2011/01/arenal-volcano-daytime.jpg");
        urls.add("http://www.hawaiilife.com/articles/wp-content/uploads/2012/10/kauai.jpg");
        urls.add("http://www.kauailandmark.com/images/kauai_beach_scene_488_01.jpg");
        final ImageAdapter imageAdapter = new ImageAdapter(this, urls.size(), urls);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                //Image image = (Image) imageAdapter.getItem(position);

                //Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                //intent.putExtra("image_id", id);
                //startActivity(intent);



                int convertedId = (int) id;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), convertedId);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();

                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra("poster_image", b);
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
}
