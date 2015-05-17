package ru.rzn.myasoedov.vktest.service.collage;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.concurrent.Callable;

/**
 * Created by grisha on 17.05.15.
 */
public class BitmapCallable implements Callable<Bitmap> {
    private String url;

    public BitmapCallable(String url) {
        this.url = url;
    }

    @Override
    public Bitmap call() throws Exception {
        try {
            return ImageLoader.getInstance().loadImageSync(url);
        } catch (Exception e) {
            return null;
        }
    }
}
