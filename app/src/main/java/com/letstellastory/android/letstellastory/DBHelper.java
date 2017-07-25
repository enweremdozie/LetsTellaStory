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
    public static String DATABASE_NAME = "mystories.db";
    public static String TABLE_NAME = "mystory_table";
    public static String TABLE2_NAME = "post_table";
    public static String TABLE3_NAME = "pass_table";

    public static String COL_ID = "ID";
    public static String COL_PASS = "PASS";
    public static String COL_POS = "POSITION";

    public static String COL_TITLE = "TITLE";
    public static String COL_GENRE = "GENRE";




    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT, GENRE TEXT)");
        sqLiteDatabase.execSQL("create table " + TABLE2_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "POSITION INTEGER)");

        sqLiteDatabase.execSQL("create table " + TABLE2_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PASS INTEGER)");








        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                DBStory.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_GENRE + " TEXT NOT NULL);";

        String createTable2 = "CREATE TABLE " + TABLE2_NAME + " ( " +
                DBState.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POS + " INTEGER NOT NULL);";

        String createTable3 = "CREATE TABLE " + TABLE3_NAME + " ( " +
                DBStoryState.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PASS + " INTEGER NOT NULL);";

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);


        onCreate(sqLiteDatabase);
    }

    public boolean insertData_my_stories(String story, String genre){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, story);
        contentValues.put(COL_GENRE, genre);
        //contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
        return false;
        else
            return true;
    }

    public boolean insertPostedStory (int position){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_STATE, state);
        contentValues.put(COL_POS, position);
        //contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE2_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertPassedStory (int pass){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_STATE, state);
        contentValues.put(COL_PASS, pass);
        //contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE3_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertData_local_stories(String story, String genre){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, story);
        contentValues.put(COL_GENRE, genre);
        //contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE3_NAME, null, contentValues);

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

    public Cursor getMyStoriesInformations(SQLiteDatabase db){

        String[] projections = {COL_ID, COL_TITLE, COL_GENRE};
        Cursor cursor = db.query(TABLE_NAME, projections,null, null, null, null, null);

        return cursor;
    }

    public Cursor getPostedStoryInfo(SQLiteDatabase db){

        String[] projections = {COL_ID, COL_POS};
        Cursor cursor = db.query(TABLE2_NAME, projections,null, null, null, null, null);

        return cursor;
    }

    public Cursor getPassedStoryInfo(SQLiteDatabase db){

        String[] projections = {COL_ID, COL_PASS};
        Cursor cursor = db.query(TABLE3_NAME, projections,null, null, null, null, null);

        return cursor;
    }

    public Cursor getLocalStoriesInformations(SQLiteDatabase db){

        String[] projections = {COL_ID, COL_TITLE, COL_GENRE};
        Cursor cursor = db.query(TABLE3_NAME, projections,null, null, null, null, null);

        return cursor;
    }
}

