package com.example.chartbuster.app;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class ApplicationController extends Application {

    private RequestQueue mRequestQueue;
    private static ApplicationController mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    public static ApplicationController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue==null)
        {
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addtoRequestQueue(Request<T> request)
    {
        getRequestQueue().add(request);
    }
}
