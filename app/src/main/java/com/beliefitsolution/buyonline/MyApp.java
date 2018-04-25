package com.beliefitsolution.buyonline;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class MyApp extends Application {
    private static Context context;
    private String TAG = "myApp";

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.e(TAG, "  myapp stater");
    }
}
