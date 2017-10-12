package com.blueangles.weatherapp;

import android.app.Application;
import android.util.Log;

import com.blueangles.weatherapp.Helper.GoogleApiHelper;

/**
 * Created by Ashith VL on 10/8/2017.
 */

public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        googleApiHelper = new GoogleApiHelper(mInstance);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }
}