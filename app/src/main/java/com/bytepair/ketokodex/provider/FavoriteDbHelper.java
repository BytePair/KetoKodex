package com.bytepair.ketokodex.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.bytepair.ketokodex.provider.FavoriteContract.FavoriteEntry.FAVORITE_ID;
import static com.bytepair.ketokodex.provider.FavoriteContract.FavoriteEntry.FAVORITE_NAME;
import static com.bytepair.ketokodex.provider.FavoriteContract.FavoriteEntry.TABLE_NAME;

public class FavoriteDbHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "ketokodex.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the plants data
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FAVORITE_ID + " STRING NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                FAVORITE_NAME + " STRING NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
