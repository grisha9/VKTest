package ru.rzn.myasoedov.vktest.dto;

import android.content.ContentValues;
import android.database.Cursor;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import java.util.LinkedList;

import ru.rzn.myasoedov.vktest.db.VKTestDBHelper;

/**
 * Created by grisha on 14.05.15.
 */
public class VKMessageWrapper {

    public static ContentValues getContentValues(VKMessage message, int chatId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKTestDBHelper.COLUMN_CHAT_ID, chatId);
        contentValues.put(VKTestDBHelper.COLUMN_MESSAGE_ID, message.id);
        contentValues.put(VKTestDBHelper.COLUMN_DATE, message.date);
        contentValues.put(VKTestDBHelper.COLUMN_MESSAGE, message.body);
        contentValues.put(VKTestDBHelper.COLUMN_USER_ID, message.user_id);
        contentValues.put(VKTestDBHelper.COLUMN_OUT, message.out ? 1 : 0);
        contentValues.put(VKTestDBHelper.COLUMN_IS_FIRST, message.isFirst() ? 1 : 0);

        int index = 1;
        for (VKAttachments.VKApiAttachment attachment : message.attachments) {
            if (attachment instanceof VKApiPhoto) {
                contentValues.put(VKTestDBHelper.COLUMN_PHOTO_URL + index,
                        ((VKApiPhoto) attachment).photo_130);
                index++;
            }
        }
        return contentValues;
    }

    public static VKMessage getMessageFromCursor(Cursor cursor) {
        VKMessage message = new VKMessage();
        message.id = cursor.getInt(cursor.getColumnIndex(VKTestDBHelper.COLUMN_MESSAGE_ID));
        message.date = cursor.getLong(cursor.getColumnIndex(VKTestDBHelper.COLUMN_DATE));
        message.body = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_MESSAGE));
        message.out = cursor.getInt(cursor.getColumnIndex(VKTestDBHelper.COLUMN_OUT)) == 1;
        message.setFirst(cursor.getInt(cursor.getColumnIndex(VKTestDBHelper.COLUMN_IS_FIRST)) == 1);
        message.setUserPhoto(cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL)));

        LinkedList<String> urls = new LinkedList<>();
        String url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL1));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL2));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL3));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL4));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL5));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL6));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL7));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL8));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL9));
        if (url != null) {
            urls.add(url);
        }
        url = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL10));
        if (url != null) {
            urls.add(url);
        }

        VKAttachments attachments = new VKAttachments();
        for(String photoUrl : urls) {
            VKApiPhoto vkApiPhoto = new VKApiPhoto();
            vkApiPhoto.photo_130 = photoUrl;
            attachments.add(vkApiPhoto);
        }
        message.attachments = attachments;

        return message;
    }
}
