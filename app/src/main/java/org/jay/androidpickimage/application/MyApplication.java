package org.jay.androidpickimage.application;

import android.app.Application;
import android.content.Context;

import org.jay.androidpickimage.R;

import cn.bmob.v3.Bmob;

/**
 * Created by jay on 2017/8/10.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, getResources().getString(R.string.bmob_application_id));
        app = this;
        mContext = getApplicationContext();
    }

    private static MyApplication app;
    private Context mContext;

    public static MyApplication getInstance() {
        return app;
    }

    public Context getContext() {
        return mContext;
    }
}
