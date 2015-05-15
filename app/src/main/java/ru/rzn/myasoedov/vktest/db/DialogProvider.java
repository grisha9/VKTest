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


public class DialogProvider extends ContentProvider {
    public static final String TAG = DialogProvider.class.getSimpleName();
    public static final String DELETE_SELECTION = VKTestDBHelper.COLUMN_CHAT_ID + " NOT IN (%s)";
    public static final String AUTHORITY = "ru.rzn.myasoedov.vktest.dialog";
    public static final int URI_ALL_DIALOGS = 1;
    public static final int URI_DIALOG_ID = 2;
    public static final Uri DIALOG_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + VKTestDBHelper.TABLE_DIALOG);
    private static final UriMatcher uriMatcher;
    private VKTestDBHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, VKTestDBHelper.TABLE_DIALOG, URI_ALL_DIALOGS);
        uriMatcher.addURI(AUTHORITY, VKTestDBHelper.TABLE_DIALOG + "/#", URI_DIALOG_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new VKTestDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        if (TextUtils.isEmpty(sort)) {
            sort = VKTestDBHelper.COLUMN_DATE + " DESC";
        }
        switch (uriMatcher.match(uri)) {
            case URI_ALL_DIALOGS:
                break;
            case URI_DIALOG_ID:
                selection = " _ID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = dbHelper.getWritableDatabase()
                .query(VKTestDBHelper.TABLE_DIALOG, projection, selection, selectionArgs, null,
                        null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL_DIALOGS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.dialogs";
            case URI_DIALOG_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.dialogs";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = dbHelper.getWritableDatabase().insert(VKTestDBHelper.TABLE_DIALOG, null,
                contentValues);
        Uri resultUri = ContentUris.withAppendedId(DIALOG_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        for (ContentValues value : values) {
            try {
                long l = db.insertOrThrow(VKTestDBHelper.TABLE_DIALOG, null, value);
                System.out.print(l);
            } catch (SQLException e) {
                int update = db.update(VKTestDBHelper.TABLE_DIALOG, value, VKTestDBHelper
                                .COLUMN_CHAT_ID + "= ?", new String[]{value
                        .getAsString(VKTestDBHelper.COLUMN_CHAT_ID)});
                System.out.print(update);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);
        return numValues;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().delete(VKTestDBHelper.TABLE_DIALOG, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().update(VKTestDBHelper.TABLE_DIALOG, contentValues,
                selection, selectionArgs);
        if (cnt > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return cnt;
    }

    public static String prepareSelectionForDelete(Iterable ids) {
        return String.format(DELETE_SELECTION, TextUtils.join(",", ids));
    }

}
