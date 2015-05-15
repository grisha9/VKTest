package ru.rzn.myasoedov.vktest;

import android.app.Application;
import android.content.Context;

/**
 * Created by grisha on 10.05.15.
 */
public class VKTest extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
