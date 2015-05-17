package ru.rzn.myasoedov.vktest;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by grisha on 10.05.15.
 */
public class VKTest extends Application {
    private static Context context;
    private static ExecutorService executors;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        executors = Executors.newFixedThreadPool(3);
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
    protected void finalize() throws Throwable {
        super.finalize();
        if (executors != null) {
            executors.shutdown();
        }
    }

    public static ExecutorService getExecutors() {
        return executors;
    }

    public static Context getContext() {
        return context;
    }

}
