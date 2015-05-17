package ru.rzn.myasoedov.vktest.service.collage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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
public class CollageFourImages extends Collage {
    private List<VKUser> users;

    public CollageFourImages(List<VKUser> users) {
        this.users = users;
    }

    @Override
    public Bitmap getCollageBitmap() throws Exception{
        List<Callable<Bitmap>> callables = new ArrayList<>();
        for(VKUser user : users) {
            callables.add(new BitmapCallable(user.photo_50));
        }
        List<Future<Bitmap>> futures = VKTest.getExecutors().invokeAll(callables);

        Bitmap bitmap1 = futures.get(0).get();
        Bitmap bitmap2 = futures.get(1).get();
        Bitmap bitmap3 = futures.get(2).get();
        Bitmap bitmap4 = futures.get(3).get();


        Bitmap collage = Bitmap.createBitmap(bitmap1.getWidth() * 2 + DELTA_DELIMITER,
                bitmap1.getHeight() * 2 + DELTA_DELIMITER, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(collage);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap1, 0 , 0, paint);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth() + DELTA_DELIMITER, 0, paint);
        canvas.drawBitmap(bitmap3, 0 , bitmap1.getHeight() + DELTA_DELIMITER, paint);
        canvas.drawBitmap(bitmap4, bitmap1.getWidth() + DELTA_DELIMITER,
                bitmap1.getHeight() + DELTA_DELIMITER, paint);

        bitmap1.recycle();
        bitmap2.recycle();
        bitmap3.recycle();
        bitmap4.recycle();

        return collage;
    }
}
