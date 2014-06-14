package com.example.chartbuster.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.gmariotti.cardslib.library.internal.Card;


public class MovieCard extends Card {

    private Movie movie;
    private Context mContext;
    private ArrayAdapter<String> movieSeatsAdapter;
    private ISelector iSelector;

    public MovieCard(Context context, Movie movie,ISelector iSelector) {
        super(context, R.layout.movie_card_layout);
        this.iSelector=iSelector;
        this.movie = movie;
        this.mContext = context;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        ImageView movieImage = (ImageView) view.findViewById(R.id.movieImage);
        TextView movieName = (TextView) view.findViewById(R.id.movieName);
        TextView moviePrice = (TextView) view.findViewById(R.id.moviePrice);
        final Spinner movieSeats = (Spinner) view.findViewById(R.id.movieSeats);
        RatingBar movieRatings = (RatingBar) view.findViewById(R.id.movieRatings);

        Picasso.with(mContext).load(movie.url).into(movieImage);
        movieName.setText(movie.name);
        moviePrice.setText(movie.price + " $");

        movieSeatsAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView)v).setTypeface(null, Typeface.BOLD);
                ((TextView) v).setTextColor(mContext.
                        getResources()
                        .getColorStateList(android.R.color.white));
                return v;

            }
        };
        movieSeatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(movie.seats>0) {
            for (int i = 1; i <= movie.seats; i++) {
                movieSeatsAdapter.add("" + i);
            }
        }

        movieRatings.setRating(movie.rating);
        movieSeats.setAdapter(movieSeatsAdapter);

        movieRatings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratings, boolean b) {
                if(movieSeatsAdapter.getCount()>0) {
                    iSelector.onSelectionMade
                            (movie.id, Integer.parseInt(movieSeatsAdapter.getItem(movieSeats.getSelectedItemPosition())), (int) ratings);
                }
            }
        });

    }

    public interface ISelector
    {
        public void onSelectionMade(String id,int seats,int ratings);
    }
}
