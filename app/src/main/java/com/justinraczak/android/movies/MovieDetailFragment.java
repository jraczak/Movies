package com.justinraczak.android.movies;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


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
    public static MovieDetailFragment newInstance() {
        MovieDetailFragment fragment = new MovieDetailFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "In onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(LOG_TAG, "Made it to onCreateView of MovieDetailFragment");
        View containerView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView titleTextView = (TextView) containerView.findViewById(R.id.movie_title);
        titleTextView.setText("SAMPLE TITLE");

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
