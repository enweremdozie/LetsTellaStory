package com.letstellastory.android.letstellastory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dozie on 2017-07-01.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "story.db";
    public static String TABLE_NAME = "story_table";
    public static String COL_ID = "ID";
    public static String COL_TITLE = "TITLE";
    public static String COL_GENRE = "GENRE";
    public static String COL_STORY = "STORY";




    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT, GENRE TEXT, STORY TEXT)");





        /*String createTable = "CREATE TABLE " + TABLE + " ( " +
                DBStory.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_GENRE + " TEXT NOT NULL, " +
                COL_STORY + " TEXT NOT NULL);";*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String title, String genre, String story){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, title);
        contentValues.put(COL_GENRE, genre);
        contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
        return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
}

