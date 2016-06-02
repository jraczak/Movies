package com.justinraczak.android.movies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 interface
 * to handle interaction events.
 * Use the {@link MovieListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieListFragment extends Fragment {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private ImageAdapter mImageAdapter;
    private ArrayList<Movie> moviesList;

    //private OnFragmentInteractionListener mListener;

    public MovieListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MovieListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieListFragment newInstance() {
        MovieListFragment fragment = new MovieListFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    mParam1 = getArguments().getString(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TextView textView = new TextView(getActivity());
        //textView.setText(R.string.hello_blank_fragment);
        //return textView;

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        Log.d(LOG_TAG, "Made it to onCreateView of MovieListFragment");

        moviesList = new ArrayList<>();

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext(), moviesList, mImageAdapter);
        fetchMoviesTask.execute();

        Log.d("onCreate", "ArrayList size is " + moviesList.size());

        // Instantiate an ImageAdapter and pass it the context, number of movies, and array of movie objects
        mImageAdapter = new ImageAdapter(getContext(), moviesList.size(), moviesList);
        // Grab the GridView for displaying the images
        GridView gridView = (GridView) getActivity().findViewById(R.id.gridview);
        // Attach the ImageAdapter to the GridView
        Log.d(LOG_TAG, "GridView is: " + gridView.toString());
        Log.d("onCreate", "Setting the adapter on the grid view");
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO: Copy this image compression code out to a snippet for future use cases
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

                //TODO: Remove this debugging toast
                //Toast.makeText(getApplicationContext(), "The item position is " + position, Toast.LENGTH_SHORT).show();

                Movie m = (Movie) mImageAdapter.getItem(position);
                Intent intent = new Intent(getContext(), MovieDetail.class);
                intent.putExtra("movie", m);
                startActivity(intent);
            }
        });
        return rootView;
    }

    //// TODO: Rename method, update argument and hook method into UI event
    //public void onButtonPressed(Uri uri) {
    //    if (mListener != null) {
    //        mListener.onFragmentInteraction(uri);
    //    }
    //}
//
    //@Override
    //public void onAttach(Context context) {
    //    super.onAttach(context);
    //    if (context instanceof MovieDetailFragment.OnFragmentInteractionListener) {
    //        mListener = (MovieDetailFragment.OnFragmentInteractionListener) context;
    //    } else {
    //        throw new RuntimeException(context.toString()
    //                + " must implement OnFragmentInteractionListener");
    //    }
    //}
//
    //@Override
    //public void onDetach() {
    //    super.onDetach();
    //    mListener = null;
    //}

}
