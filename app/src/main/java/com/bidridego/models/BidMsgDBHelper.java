package com.bidridego.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BidMsgDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "BidMessages.db";

    public static final String TABLE_NAME = "bid_messages";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_NUMERIC_VALUE = "numeric_value";
    public static final String COLUMN_TRIP_ID = "trip_id";
    public static final String COLUMN_DRIVER_ID = "driver_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_SENDER_ID = "sender_id";

    public BidMsgDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DRIVER_ID + " TEXT, " +
                COLUMN_USER_ID + " TEXT, "+
                COLUMN_TRIP_ID + " TEXT, "+
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_NUMERIC_VALUE + " REAL)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
