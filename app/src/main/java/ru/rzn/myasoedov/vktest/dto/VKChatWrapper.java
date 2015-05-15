package ru.rzn.myasoedov.vktest.dto;

import android.content.ContentValues;
import android.database.Cursor;

import ru.rzn.myasoedov.vktest.db.VKTestDBHelper;

/**
 * Created by grisha on 12.05.15.
 */
public class VKChatWrapper {

    public static ContentValues getContentValues(VKChat vkChat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKTestDBHelper.COLUMN_CHAT_ID, vkChat.id);
        contentValues.put(VKTestDBHelper.COLUMN_DATE, vkChat.date);
        contentValues.put(VKTestDBHelper.COLUMN_TITLE, vkChat.title);
        contentValues.put(VKTestDBHelper.COLUMN_PREVIEW, vkChat.preview);
        contentValues.put(VKTestDBHelper.COLUMN_PHOTO_URL, vkChat.photoUrl);
        contentValues.put(VKTestDBHelper.COLUMN_CUSTOM_PHOTO_URL, vkChat.customPhotoUrl);
        return contentValues;
    }

    public static VKChat getChatFromCursor(Cursor cursor) {
        VKChat chat = new VKChat();
        chat.id = cursor.getInt(cursor.getColumnIndex(VKTestDBHelper.COLUMN_CHAT_ID));
        chat.date = cursor.getLong(cursor.getColumnIndex(VKTestDBHelper.COLUMN_DATE));
        chat.title = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_TITLE));
        chat.preview = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PREVIEW));
        chat.photoUrl = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_PHOTO_URL));
        chat.customPhotoUrl = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_CUSTOM_PHOTO_URL));
        return chat;
    }
}
