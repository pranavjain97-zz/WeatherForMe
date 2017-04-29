package com.example.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pranav on 27/04/17.
 */

public class WeatherDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME="weather.db";
    public static final int DATABASE_VERSION=1;

    public WeatherDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /** NOT NULL -> To make sure no data is empty.
         *  UNIQUE -> So there is no duplication of data. New data will be replaced by old.
         */

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +


                        WeatherContract.WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherContract.WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL UNIQUE,"                 +

                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "                 +

                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherContract.WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL" + ");";
        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    /** This database is only a cache for online data. Only used if we change the version number*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS '"+ WeatherContract.WeatherEntry.TABLE_NAME+"'");
        onCreate(db);
    }
}
