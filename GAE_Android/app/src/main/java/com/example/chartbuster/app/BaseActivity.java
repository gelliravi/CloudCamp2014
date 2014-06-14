package com.example.chartbuster.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class BaseActivity extends Activity {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        mFragmentManager=getFragmentManager();
        mFragmentTransaction=mFragmentManager.beginTransaction();

        if(getFragmentManager().findFragmentById(R.id.container)==null)
        {
            mFragmentTransaction.replace(R.id.container,new MoviesFragment(),"MoviesFragment").commit();
        }
    }

}
