package com.example.chartbuster.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class MoviesFragment extends Fragment {

    private ArrayList<Card> movieCards;
    private CardArrayAdapter mCardArrayAdapter;
    private CardListView mCardListView;
    private static final String movie_list_url="http://chart-buster.appspot.com/movies";
    private static final String movie_book_url="http://chart-buster.appspot.com/book";
    private static final String movie_rating_url="http://chart-buster.appspot.com/rate";
    private List<Movie> movieList;
    private Movie movieToPost;
    private SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipeRefreshLayout=new SwipeRefreshLayout(getActivity());
        mCardListView=new CardListView(getActivity());
        mSwipeRefreshLayout.addView(mCardListView);
        return mSwipeRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getMoviesList();

        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMoviesList();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.base,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(movieToPost==null)
        {
            Toast.makeText(getActivity(),"Make Selection before Booking!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            postMovieBookingInfoToServer();
        }

        return true;
    }

    private void getMoviesList()
    {
        JsonArrayRequest request=new JsonArrayRequest(movie_list_url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                Type listType=new TypeToken<List<Movie>>(){}.getType();

                movieList=new Gson().fromJson(jsonArray.toString(), listType);

                createMoviesCardList();

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("vipul",volleyError.toString());
            }
        });

        ApplicationController.getInstance().addtoRequestQueue(request);
    }

    private void createMoviesCardList()
    {
        movieCards=new ArrayList<>();

        for(int i=0;i<movieList.size();i++)
        {
            MovieCard mCard=new MovieCard(getActivity(),movieList.get(i), new MovieCard.ISelector() {
                @Override
                public void onSelectionMade(String id,int seats, int ratings) {
                    movieToPost=new Movie();
                    movieToPost.id=id;
                    movieToPost.seats=seats;
                    movieToPost.rating=ratings;
                }
            });
            movieCards.add(mCard);
        }


        mCardArrayAdapter=new CardArrayAdapter(getActivity(),movieCards);
        mCardListView.setAdapter(mCardArrayAdapter);
    }

    private void postMovieBookingInfoToServer()
    {
        Toast.makeText(getActivity(),movieToPost.seats+"\n"+movieToPost.rating,Toast.LENGTH_SHORT).show();
        StringRequest seatsUpdaterequest=new StringRequest(Request.Method.POST,movie_book_url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(),"Response "+s,Toast.LENGTH_LONG).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(),"Error "+volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> moviePostParams = new HashMap<>();
                moviePostParams.put("movieid", movieToPost.id);
                moviePostParams.put("seats", ""+movieToPost.seats);
                return moviePostParams;
            }
        };


        StringRequest ratingUpdaterequest=new StringRequest(Request.Method.POST,movie_rating_url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(),"Response "+s,Toast.LENGTH_LONG).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(),"Error "+volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> moviePostParams = new HashMap<>();
                moviePostParams.put("movieid", movieToPost.id);
                moviePostParams.put("rating", ""+movieToPost.rating);
                return moviePostParams;
            }
        };

        ApplicationController.getInstance().addtoRequestQueue(seatsUpdaterequest);
        ApplicationController.getInstance().addtoRequestQueue(ratingUpdaterequest);
    }
}
