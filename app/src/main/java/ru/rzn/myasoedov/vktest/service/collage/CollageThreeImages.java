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
public class CollageThreeImages extends Collage {
    private List<VKUser> users;

    public CollageThreeImages(List<VKUser> users) {
        this.users = users;
    }

    @Override
    public Bitmap getCollageBitmap() {
        Bitmap bitmap1 = ImageLoader.getInstance().loadImageSync(users.get(0).photo_50);
        Bitmap bitmap2 = ImageLoader.getInstance().loadImageSync(users.get(1).photo_50);
        Bitmap bitmap3 = ImageLoader.getInstance().loadImageSync(users.get(2).photo_50);

        Bitmap collage = Bitmap.createBitmap(bitmap1.getWidth() + DELTA_DELIMITER,
                bitmap1.getHeight() + DELTA_DELIMITER, Bitmap.Config.ARGB_8888);

        bitmap1 = Bitmap.createBitmap(bitmap1, bitmap1.getWidth() / 4, 0, bitmap1.getWidth() / 2,
                bitmap1.getHeight());
        bitmap2 = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth() / 2,
                bitmap2.getHeight() / 2, true);
        bitmap3 = Bitmap.createScaledBitmap(bitmap3, bitmap3.getWidth() / 2,
                bitmap3.getHeight() / 2, true);



        Canvas canvas = new Canvas(collage);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap1, 0 , 0, paint);
        canvas.drawBitmap(bitmap2, bitmap1.getWidth() + DELTA_DELIMITER , 0, paint);
        canvas.drawBitmap(bitmap3, bitmap1.getWidth() + DELTA_DELIMITER,
                bitmap2.getHeight() + DELTA_DELIMITER, paint);


        bitmap1.recycle();
        bitmap2.recycle();
        bitmap3.recycle();

        return collage;
    }
}
