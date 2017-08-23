package com.letstellastory.android.letstellastory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.letstellastory.android.letstellastory.DBHelper.TABLE2_NAME;

/**
 * Created by dozie on 2017-08-22.
 */

public class DBUserHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "story1.db";
    public static String TABLE_NAME = "user_table";
    //public static String TABLE2_NAME = "post_table";
    //public static String TABLE3_NAME = "pass_table";

    public static String COL_ID = "ID";
    //public static String COL_PASS = "PASS";
    //public static String COL_POS = "POSITION";

    public static String COL_USER = "USER";
    public static String COL_PASSWORD = "PASSWORD";
    public static final int DATABASE_VERSION = 1;




    public DBUserHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER TEXT, PASSWORD TEXT)");
        /*sqLiteDatabase.execSQL("create table " + TABLE2_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "POSITION TEXT)");
        sqLiteDatabase.execSQL("create table " + TABLE3_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PASS TEXT)");*/








        /*String createTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                DBStory.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_GENRE + " TEXT NOT NULL);";

        String createTable2 = "CREATE TABLE " + TABLE2_NAME + " ( " +
                DBState.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POS + " TEXT NOT NULL);";

        String createTable3 = "CREATE TABLE " + TABLE3_NAME + " ( " +
                DBStoryState.DBMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PASS + " TEXT NOT NULL);";*/

        //onUpgrade(sqLiteDatabase, 1, DATABASE_VERSION);
    }

    public Integer deleteUser(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE2_NAME, "ID = ?", new String[] {id});
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
            //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);
        }

        onCreate(sqLiteDatabase);
    }

    public boolean insertUser(String user, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER, user);
        contentValues.put(COL_PASSWORD, password);
        //contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    /*public boolean insertPostedStory (String dialogID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_STATE, state);
        contentValues.put(COL_POS, dialogID);
        //contentValues.put(COL_STORY, story);
        long result = sqLiteDatabase.insert(TABLE2_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertPassedStory (String dialogID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_STATE, state);
        contentValues.put(COL_PASS, dialogID);
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
    }*/

    public Cursor getAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getUserInfo(SQLiteDatabase db){

        String[] projections = {COL_ID, COL_USER, COL_PASSWORD};
        Cursor cursor = db.query(TABLE_NAME, projections,null, null, null, null, null);

        return cursor;
    }

    /*public Cursor getPostedStoryInfo(SQLiteDatabase db){

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
    }*/
}


