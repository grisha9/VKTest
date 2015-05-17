package ru.rzn.myasoedov.vktest.service.collage;

import android.graphics.Bitmap;

import ru.rzn.myasoedov.vktest.dto.VKUser;

/**
 * Created by grisha on 16.05.15.
 */
public class CollageOneImage extends Collage {
    private VKUser user;

    public CollageOneImage(VKUser user) {
        this.user = user;
    }

    @Override
    protected Bitmap getCollageBitmap() {
        return null;
    }

    @Override
    public String getCollage() {
        return user.photo_50;
    }
}
