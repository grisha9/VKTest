package ru.rzn.myasoedov.vktest;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by grisha on 10.05.15.
 */
public class VKTest extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        DisplayImageOptions defaultImageDisplayOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        // init image loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(1024 * 1024))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultImageDisplayOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public Looper getMainLooper() {
        return super.getMainLooper();
    }

    public static Context getContext() {
        return context;
    }

}
