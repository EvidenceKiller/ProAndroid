package com.zxn.contentprovider;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by Administrator on 2015/7/9.
 */
public class MyApplication extends Application
{
    public final static String tag="MyApplication";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(tag,"configuration changed");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag,"oncreate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(tag,"onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(tag, "onTerminate");
    }

}

