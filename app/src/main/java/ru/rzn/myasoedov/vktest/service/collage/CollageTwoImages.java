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
public class CollageTwoImages extends Collage {
    private List<VKUser> users;

    public CollageTwoImages(List<VKUser> users) {
        this.users = users;
    }

    @Override
    public Bitmap getCollageBitmap() {
        Bitmap bitmap1 = ImageLoader.getInstance().loadImageSync(users.get(0).photo_50);
        Bitmap bitmap2 = ImageLoader.getInstance().loadImageSync(users.get(1).photo_50);

        int startX = bitmap1.getWidth() / 4;
        int halfWeight = bitmap1.getWidth() / 2;
        bitmap1 = Bitmap.createBitmap(bitmap1, startX, 0, halfWeight, bitmap1.getHeight());
        bitmap2 = Bitmap.createBitmap(bitmap2, startX, 0, halfWeight, bitmap2.getHeight());

        Bitmap collage = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(collage);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap1, 0 , 0, paint);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth() , 0, paint);

        bitmap1.recycle();
        bitmap2.recycle();
        return null;
    }
}
