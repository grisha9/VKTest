package ru.rzn.myasoedov.vktest.dto;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import ru.rzn.myasoedov.vktest.db.VKTestDBHelper;

/**
 * Created by grisha on 12.05.15.
 */
public class VKChatWrapper {
    public static final String USER_DELIMITER = ",";

    public static ContentValues getContentValues(VKChat vkChat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKTestDBHelper.COLUMN_CHAT_ID, vkChat.id);
        contentValues.put(VKTestDBHelper.COLUMN_DATE, vkChat.date);
        contentValues.put(VKTestDBHelper.COLUMN_TITLE, vkChat.title);
        contentValues.put(VKTestDBHelper.COLUMN_PREVIEW, vkChat.preview);
        contentValues.put(VKTestDBHelper.COLUMN_PHOTO_URL, vkChat.photoUrl);
        if (vkChat.users != null && vkChat.users.length > 0) {
            contentValues.put(VKTestDBHelper.COLUMN_CHAT_USERS, TextUtils.join(USER_DELIMITER,
                    vkChat.getUsers()));

        }
        if (vkChat.collageUsers!= null && !vkChat.collageUsers.isEmpty()) {
            contentValues.put(VKTestDBHelper.COLUMN_COLLAGE_USERS, TextUtils.join(USER_DELIMITER,
                    vkChat.collageUsers));
        }
        return contentValues;
    }

    public static ContentValues getContentValuesForUpdateAvatar(VKChat vkChat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKTestDBHelper.COLUMN_CHAT_ID, vkChat.id);
        contentValues.put(VKTestDBHelper.COLUMN_CUSTOM_PHOTO_URL, vkChat.customPhotoUrl);
        if (vkChat.collageUsers!= null && !vkChat.collageUsers.isEmpty()) {
            contentValues.put(VKTestDBHelper.COLUMN_COLLAGE_USERS, TextUtils.join(USER_DELIMITER,
                    vkChat.collageUsers));
        }
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

        String stringUsers = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_CHAT_USERS));
        if (!TextUtils.isEmpty(stringUsers)) {
            String[] split = stringUsers.split(USER_DELIMITER);
            int[] users = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                users[i] = Integer.valueOf(split[i]);
            }
            chat.users = users;
        }
        stringUsers = cursor.getString(cursor.getColumnIndex(VKTestDBHelper.COLUMN_COLLAGE_USERS));
        if (!TextUtils.isEmpty(stringUsers)) {
            String[] split = stringUsers.split(USER_DELIMITER);
            for(String userId : split) {
                chat.collageUsers.add(Integer.valueOf(userId));
            }
        }
        return chat;
    }
}
