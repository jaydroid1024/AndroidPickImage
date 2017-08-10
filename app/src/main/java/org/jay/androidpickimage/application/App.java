package org.jay.androidpickimage.application;

import android.app.Application;

import org.jay.androidpickimage.R;

import cn.bmob.v3.Bmob;

/**
 * Created by jay on 2017/8/10.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, getResources().getString(R.string.bmob_application_id));
    }
}
