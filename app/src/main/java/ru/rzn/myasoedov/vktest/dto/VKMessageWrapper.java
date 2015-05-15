package ru.rzn.myasoedov.vktest.dto;

import android.content.ContentValues;
import android.database.Cursor;

import com.vk.sdk.api.model.VKApiMessage;

import ru.rzn.myasoedov.vktest.db.VKTestDBHelper;

/**
 * Created by grisha on 14.05.15.
 */
public class VKMessageWrapper {

    public static ContentValues getContentValues(VKApiMessage message, int chatId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKTestDBHelper.COLUMN_CHAT_ID, chatId);
        contentValues.put(VKTestDBHelper.COLUMN_MESSAGE_ID, message.id);
        contentValues.put(VKTestDBHelper.COLUMN_DATE, message.date);
        contentValues.put(VKTestDBHelper.COLUMN_MESSAGE, message.body);
        contentValues.put(VKTestDBHelper.COLUMN_USER_ID, message.user_id);

        return contentValues;
    }

    public static VKMessage getMessageFromCursor(Cursor cursor) {
        VKMessage message = new VKMessage();
        message.id = cursor.getInt(cursor.getColumnIndex(VKTestDBHelper.COLUMN_MESSAGE_ID));
        message.date = cursor.getLong(cursor.getColumnIndex(VKTestDBHelper.COLUMN_DATE));
        message.body = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_MESSAGE));
        message.setUserPhoto(cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL)));
        return message;
    }
}
