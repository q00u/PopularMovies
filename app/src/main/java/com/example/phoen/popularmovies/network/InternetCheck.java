package com.example.phoen.popularmovies.network;

//Originally from https://github.com/Flatlyn/AndroidInternetCheck
//Modified to correct permissions, context leaks, and null pointer exceptions, (and spelling)

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by James A. Krawczyk
 * Licensed under MIT
 * Details see license file https://github.com/Flatlyn/AndroidInternetCheck/blob/master/LICENSE
 **/

//modified for private access and to remove context leaks
public class InternetCheck extends AsyncTask<String, Void, Void> {

    //We need the app context to give some network things context
    private WeakReference<Context> appContext;

    //We need the app calling activity to end it and create context
    private WeakReference<Activity> callingActivity;

    //Activity to go to after check
    private String gotoName;

    //Store result of internet check.
    private Boolean internetLive = true;

    //Log ID
    private static final String TAG = "InternetCheck";

    //This will be set depending on whether we are checking on the main activity or on error activity;
    //TRUE means do something if internet is broken, FALSE means do something if internet is working.
    //Normally you would call this activity with True and in the error activity have a recurring task
    //call it with false to check when the internet comes back online. In the error activity you should pass
    //the activity as you main activity so when the internet comes back online it will send the user back to
    //the main activity.
    private boolean isError;

    //Constructor
    public InternetCheck (Activity passedActivity, String whereToGo, boolean onError)
    {
        this.callingActivity = new WeakReference<>(passedActivity);
        this.appContext = new WeakReference<>(callingActivity.get().getApplicationContext());
        this.gotoName = whereToGo;
        this.isError = onError;
    }

    //Do the actual check.
    @Override
    protected Void doInBackground(String... params)
    {

        if(isNetworkAvailable())
        {
            //Check internet by checking a google site. You can substitute the
            //URL for a site of your choosing but Google is a good choice since
            //they have very high uptime.
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                internetLive = (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);

            }
            catch (IOException e)
            {
                //Something bad happened, will assume no internet
                Log.e(TAG, "Error checking internet connection", e);
                internetLive = false;
            }

        }
        else
        {
            internetLive = false;
        }

        return null;
    }

    //Check for network existence, e.g. WiFi, Ethernet. This does not mean actual
    //internet access.
    //modified to avoid null pointer exceptions, and to verify isConnectedOrConnecting
    private boolean isNetworkAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager) appContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (null!=cm && null!=cm.getActiveNetworkInfo() && cm.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    //Do whatever we want after check. In this case kill activity that created it and start a error
    //activity.
    @Override
    protected void onPostExecute(Void aVoid)
    {
        if(isError)
        {
            if(!internetLive)
            {
                try
                {

                    Intent i = new Intent(appContext.get(), Class.forName(gotoName));
                    callingActivity.get().startActivity(i);
                    callingActivity.get().finish();
                }
                catch(ClassNotFoundException e)
                {
                    //If this error is generated it means you don't have a valid
                    //activity for the passed activity.
                    Log.e(TAG, "Go to class couldn't be found.", e);
                }
            }
        }
        else
        {
            if(internetLive)
            {
                try
                {

                    Intent i = new Intent(appContext.get(), Class.forName(gotoName));
                    callingActivity.get().startActivity(i);
                    callingActivity.get().finish();
                }
                catch(ClassNotFoundException e)
                {
                    //If this error is generated it means you don't have a valid
                    //activity for the passed activity.
                    Log.e(TAG, "Go to class couldn't be found.", e);
                }
            }
        }

    }

}
