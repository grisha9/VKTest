package ru.rzn.myasoedov.vktest.dto;

import android.content.ContentValues;

import com.vk.sdk.api.model.VKApiUser;

import ru.rzn.myasoedov.vktest.db.VKTestDBHelper;

/**
 * Created by grisha on 12.05.15.
 */
public class VKUserWrapper {

    public static ContentValues getContentValues(VKApiUser vkApiUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKTestDBHelper.COLUMN_USER_ID, vkApiUser.id);
        contentValues.put(VKTestDBHelper.COLUMN_PHOTO_URL, vkApiUser.photo_50);
        return contentValues;
    }

}
