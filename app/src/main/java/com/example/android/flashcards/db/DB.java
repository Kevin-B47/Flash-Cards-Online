package com.example.android.flashcards.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kevin on 2/14/2018.
 */

class DB extends SQLiteOpenHelper {

    private final static String db_name = "flashcards.db";
    private final static int db_ver = 1;

    static SQLiteDatabase obj;

    DB(Context context) {
        super(context, db_name, null, db_ver);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (obj == null){
            obj = sqLiteDatabase;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (obj == null) {
            obj = sqLiteDatabase;
        }
    }

    public void freeDB(){
        if(obj != null && obj.isOpen()){
            obj.close();
        }
    }
}
