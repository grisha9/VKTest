package ru.rzn.myasoedov.vktest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ru.rzn.myasoedov.vktest.BuildConfig;


public class VKTestDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_DIALOG = "Dialog";
    public static final String TABLE_MESSAGE = "Message";
    public static final String TABLE_PARTICIPANT = "Participant";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_CHAT_ID = "chatId";
    public static final String COLUMN_CHAT_USERS = "chatUsers";
    public static final String COLUMN_COLLAGE_USERS = "collageUsers";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_PREVIEW = "preview";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_OUT = "out";
    public static final String COLUMN_IS_FIRST = "first";
    public static final String COLUMN_CUSTOM_PHOTO_URL = "customPhotoUrl";
    public static final String COLUMN_PHOTO_URL = "photoUrl";
    public static final String COLUMN_PHOTO_URL1 = "photoUrl1";
    public static final String COLUMN_PHOTO_URL2 = "photoUrl2";
    public static final String COLUMN_PHOTO_URL3 = "photoUrl3";
    public static final String COLUMN_PHOTO_URL4 = "photoUrl4";
    public static final String COLUMN_PHOTO_URL5 = "photoUrl5";
    public static final String COLUMN_PHOTO_URL6 = "photoUrl6";
    public static final String COLUMN_PHOTO_URL7 = "photoUrl7";
    public static final String COLUMN_PHOTO_URL8 = "photoUrl8";
    public static final String COLUMN_PHOTO_URL9 = "photoUrl9";
    public static final String COLUMN_PHOTO_URL10 = "photoUrl10";

    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_MESSAGE = "message";


    public VKTestDBHelper(Context context) {
        super(context, BuildConfig.DB_NAME, null, BuildConfig.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_DIALOG + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CHAT_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_DATE + " INTEGER NOT NULL, " +
                COLUMN_PREVIEW + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CHAT_USERS + " TEXT, " +
                COLUMN_COLLAGE_USERS + " TEXT, " +
                COLUMN_PHOTO_URL + " TEXT, " +
                COLUMN_CUSTOM_PHOTO_URL + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_MESSAGE + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CHAT_ID + " INTEGER REFERENCES " + TABLE_DIALOG + "(" + COLUMN_CHAT_ID + ") ON DELETE CASCADE, " +
                COLUMN_USER_ID + " INTEGER NOT NULL , " +
                COLUMN_MESSAGE_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_OUT + " INTEGER, " +
                COLUMN_IS_FIRST + " INTEGER, " +
                COLUMN_PHOTO_URL1 + " TEXT, " +
                COLUMN_PHOTO_URL2 + " TEXT, " +
                COLUMN_PHOTO_URL3 + " TEXT, " +
                COLUMN_PHOTO_URL4 + " TEXT, " +
                COLUMN_PHOTO_URL5 + " TEXT, " +
                COLUMN_PHOTO_URL6 + " TEXT, " +
                COLUMN_PHOTO_URL7 + " TEXT, " +
                COLUMN_PHOTO_URL8 + " TEXT, " +
                COLUMN_PHOTO_URL9 + " TEXT, " +
                COLUMN_PHOTO_URL10 + " TEXT, " +
                COLUMN_DATE + " INTEGER NOT NULL);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PARTICIPANT + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_PHOTO_URL + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DIALOG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANT);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
