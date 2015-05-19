package ru.rzn.myasoedov.vktest.service.collage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import ru.rzn.myasoedov.vktest.VKTest;
import ru.rzn.myasoedov.vktest.dto.VKUser;

/**
 * Created by grisha on 16.05.15.
 */
public class CollageTwoImages extends Collage {
    private List<VKUser> users;

    public CollageTwoImages(List<VKUser> users) {
        this.users = users;
    }

    @Override
    public Bitmap getCollageBitmap() throws Exception {
        List<Callable<Bitmap>> callables = new ArrayList<>();
        for(VKUser user : users) {
            callables.add(new BitmapCallable(user.photo_50));
        }
        List<Future<Bitmap>> futures = VKTest.getExecutors().invokeAll(callables);

        Bitmap bitmap1 = futures.get(0).get();
        Bitmap bitmap2 = futures.get(1).get();

        Bitmap collage = Bitmap.createBitmap(bitmap1.getWidth() + DELTA_DELIMITER,
                bitmap1.getHeight(), Bitmap.Config.ARGB_8888);

        int startX = bitmap1.getWidth() / 4;
        int halfWeight = bitmap1.getWidth() / 2;
        bitmap1 = Bitmap.createBitmap(bitmap1, startX, 0, halfWeight, bitmap1.getHeight());
        bitmap2 = Bitmap.createBitmap(bitmap2, startX, 0, halfWeight, bitmap2.getHeight());

        Canvas canvas = new Canvas(collage);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap1, 0 , 0, paint);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth() + DELTA_DELIMITER, 0, paint);

        bitmap1.recycle();
        bitmap2.recycle();
        return collage;
    }
}
