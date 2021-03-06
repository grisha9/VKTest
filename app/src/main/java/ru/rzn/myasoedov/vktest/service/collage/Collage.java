package ru.rzn.myasoedov.vktest.service.collage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import ru.rzn.myasoedov.vktest.VKTest;

/**
 * Created by grisha on 16.05.15.
 */
public abstract class Collage {
    protected static final int DELTA_DELIMITER = 1;
    private static final String IMAGE_DIR = "imageDir";
    private static final String FILE_PREFIX = "file://";

    /**
     * get collage bitmap
     *
     * @return bitmap
     */
    protected abstract Bitmap getCollageBitmap() throws Exception;

    /**
     * get path to collage file
     * @return path to file
     */
    public String getCollage() throws Exception {
        Bitmap collage = getCollageBitmap();
        ContextWrapper contextWrapper = new ContextWrapper(VKTest.getContext());
        File directory = contextWrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File file = new File(directory, String.valueOf(new Date().getTime()));

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            collage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        collage.recycle();
        return FILE_PREFIX + file.getAbsolutePath();
    }
}
