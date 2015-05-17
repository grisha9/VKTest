package ru.rzn.myasoedov.vktest.service.collage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

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
    public Bitmap getCollageBitmap() {
        Bitmap bitmap1 = ImageLoader.getInstance().loadImageSync(users.get(0).photo_50);
        Bitmap bitmap2 = ImageLoader.getInstance().loadImageSync(users.get(1).photo_50);
        Bitmap bitmap3 = ImageLoader.getInstance().loadImageSync(users.get(2).photo_50);
        Bitmap bitmap4 = ImageLoader.getInstance().loadImageSync(users.get(3).photo_50);

        Bitmap collage = Bitmap.createBitmap(bitmap1.getWidth() * 2,
                bitmap1.getHeight() * 2, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(collage);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap1, 0 , 0, paint);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth() , 0, paint);
        canvas.drawBitmap(bitmap3, 0 , bitmap1.getHeight(), paint);
        canvas.drawBitmap(bitmap4, bitmap1.getWidth() , bitmap1.getHeight(), paint);

        bitmap1.recycle();
        bitmap2.recycle();
        bitmap3.recycle();
        bitmap4.recycle();

        return collage;
    }
}
