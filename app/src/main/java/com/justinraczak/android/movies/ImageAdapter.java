package com.justinraczak.android.movies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by justinr on 3/10/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    public int numberOfImages;
    public ArrayList<String> mImageUrls;

    public ImageAdapter(Context c, int count, ArrayList<String> urls) {
        mContext = c;
        numberOfImages = count;
        mImageUrls = urls;
    }

    public int getCount() {
        return mImageUrls.size();
    }

    public Object getItem(int position) {
        return mImageUrls.toArray()[position];
        //return mImageUrls[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
            imageView.setAdjustViewBounds(true);
        }
        else {
            imageView = (ImageView) convertView;
        }
        Log.d("getView", "Url set length is " + this.mImageUrls.size());

        String url = this.mImageUrls.get(position);
        Log.d("getView", "Sending image at " + url + "to loader");
        new UrlImageLoader(url, imageView).execute();

        return imageView;
        }


    public class ImageContainer {
        public ImageView view;
        public Bitmap image;
    }

    public class UrlImageLoader extends AsyncTask<Void, Void, ImageContainer> {

        String imageUrl;
        ImageView imageView;

        public UrlImageLoader(String imageUrl, ImageView imageView) {
            this.imageUrl = imageUrl;
            this.imageView = imageView;
        }

        @Override
        protected ImageContainer doInBackground(Void... params) {

            Log.d("ImageBackground", imageUrl + " has reached background task");

            try {
                URL url = new URL(imageUrl);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ImageContainer container = new ImageContainer();
                container.image = bitmap;
                container.view = imageView;


                inputStream.close();
                return container;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ImageContainer container) {
            super.onPostExecute(container);

            container.view.setImageBitmap(container.image);
        }
    }
}
