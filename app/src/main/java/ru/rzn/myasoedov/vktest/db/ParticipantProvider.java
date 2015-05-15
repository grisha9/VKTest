package ru.rzn.myasoedov.vktest.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class ParticipantProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.rzn.myasoedov.vktest.participant";
    public static final int URI_ALL_PARTICIPANTS = 1;
    public static final int URI_PARTICIPANT_ID = 2;
    public static final Uri PARTICIPANT_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + VKTestDBHelper.TABLE_PARTICIPANT);
    private static final UriMatcher uriMatcher;
    private VKTestDBHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, VKTestDBHelper.TABLE_PARTICIPANT, URI_ALL_PARTICIPANTS);
        uriMatcher.addURI(AUTHORITY, VKTestDBHelper.TABLE_PARTICIPANT + "/#", URI_PARTICIPANT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new VKTestDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        Cursor cursor = dbHelper.getWritableDatabase()
                .query(VKTestDBHelper.TABLE_PARTICIPANT, projection, selection, selectionArgs, null,
                        null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL_PARTICIPANTS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.participants";
            case URI_PARTICIPANT_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.participants";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = dbHelper.getWritableDatabase().insertWithOnConflict(VKTestDBHelper.TABLE_PARTICIPANT,
                null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        Uri resultUri = ContentUris.withAppendedId(PARTICIPANT_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().delete(VKTestDBHelper.TABLE_PARTICIPANT, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().update(VKTestDBHelper.TABLE_PARTICIPANT, contentValues,
                selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

}
