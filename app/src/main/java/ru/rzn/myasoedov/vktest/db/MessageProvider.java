package ru.rzn.myasoedov.vktest.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class MessageProvider extends ContentProvider {
    public static final String TAG = DialogProvider.class.getSimpleName();
    public static final String PARAMETER_LIMIT = "limit";
    public static final String DELETE_SELECTION = VKTestDBHelper.COLUMN_MESSAGE_ID + " NOT IN (%s)";
    public static final String QUERY_SELECTION = VKTestDBHelper.COLUMN_CHAT_ID + " = ?";
    public static final String AUTHORITY = "ru.rzn.myasoedov.vktest.message";
    public static final int URI_ALL_MESSAGE = 1;
    public static final int URI_MESSAGE_ID = 2;
    public static final Uri MESSAGE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + VKTestDBHelper.TABLE_MESSAGE);
    private static final UriMatcher uriMatcher;
    private VKTestDBHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, VKTestDBHelper.TABLE_MESSAGE, URI_ALL_MESSAGE);
        uriMatcher.addURI(AUTHORITY, VKTestDBHelper.TABLE_MESSAGE + "/#", URI_MESSAGE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new VKTestDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        String limit = uri.getQueryParameter(PARAMETER_LIMIT);

        if (TextUtils.isEmpty(sort)) {
            sort = VKTestDBHelper.COLUMN_DATE;
        }

        switch (uriMatcher.match(uri)) {
            case URI_ALL_MESSAGE:
                break;
            case URI_MESSAGE_ID:
                selection = " _ID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = dbHelper.getWritableDatabase()
                .query(VKTestDBHelper.TABLE_MESSAGE + " left join " + VKTestDBHelper.TABLE_PARTICIPANT
                        + " on message.userId =  PARTICIPANT.userId ", projection, selection, selectionArgs, null,
                        null, sort, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL_MESSAGE:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.message";
            case URI_MESSAGE_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.message";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = dbHelper.getWritableDatabase().insert(VKTestDBHelper.TABLE_MESSAGE,
                null, contentValues);
        Uri resultUri = ContentUris.withAppendedId(MESSAGE_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;
        boolean isNeedUpdate = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        for (ContentValues value : values) {
            try {
                db.insertOrThrow(VKTestDBHelper.TABLE_MESSAGE, null, value);
                isNeedUpdate = true;
            } catch (SQLException e) {
                Log.i(TAG, e.getMessage());
            }
        }
        if (isNeedUpdate) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return numValues;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().delete(VKTestDBHelper.TABLE_MESSAGE, selection, selectionArgs);
        if (cnt > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().update(VKTestDBHelper.TABLE_MESSAGE, contentValues,
                selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public static String prepareSelectionForDelete(int chatId, Iterable messageIds) {
        return VKTestDBHelper.COLUMN_CHAT_ID + " = " + chatId + " AND "
                + String.format(DELETE_SELECTION, TextUtils.join(",", messageIds));
    }

}
