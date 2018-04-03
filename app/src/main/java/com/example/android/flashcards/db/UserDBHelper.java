package com.example.android.flashcards.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kevin on 2/15/2018.
 */

public class UserDBHelper extends DB {

    //Table name
    private final static String tbl_name = "userdata";

    // Columns
    private final static String c_userid = "userid";
    private final static String c_displayname = "displayname";
    private final static String c_username = "username";
    private final static String c_email = "email";
    private final static String c_token = "token";

    public UserDBHelper(Context context ) {
        super(context);
        Context context1 = context;
    }

    private final static String createTable = "CREATE TABLE IF NOT EXISTS " + tbl_name + "("
            + c_userid + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + c_token + " VARCHAR(80),"
            + c_email + " VARCHAR (64),"
            + c_displayname + " VARCHAR(32),"
            + c_username + " VARCHAR(24) UNIQUE)";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tbl_name);
        onCreate(sqLiteDatabase);
    }

    public void InsertUser(String username, String displayName, String token){
        if (DB.obj != null) {
            ContentValues queryData = new ContentValues();
            queryData.put(c_username,username);
            queryData.put(c_displayname,displayName);
            queryData.put(c_token,token);
            DB.obj.insert(tbl_name,null,queryData);
        }else{
            //Log.d("DB Obj","DB was NULL when inserting user");
        }
    }


}
