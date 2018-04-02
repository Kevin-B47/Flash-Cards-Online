package com.example.android.flashcards.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Kevin on 2/15/2018.
 */

public class FlashcardDBHelper extends DB {

    //Table name
    private final static String tbl_name = "flashcards";

    // Columns
    private final static String c_collectionid = "collectionid";
    private final static String c_cardnumber = "cardnumber";
    private final static String c_fonttext = "fonttext";
    private final static String c_backtext = "backtext";

    public Context context;

    public FlashcardDBHelper(Context context ) {
        super(context);
        this.context = context;
    }
    private final static String createTable = "CREATE TABLE IF NOT EXISTS " + tbl_name + "("
            + c_collectionid + " INTEGER"
            + c_cardnumber + " INTEGER PRIMARY KEY,"
            + c_fonttext + " VARCHAR(128),"
            + c_backtext + " VARCHAR(128))";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tbl_name);
        onCreate(sqLiteDatabase);
    }

    public void InsertCard(int collectionid, int cardNum, String frontText, String backText){
        if (DB.obj != null) {
            ContentValues queryData = new ContentValues();
            queryData.put(c_collectionid,collectionid);
            queryData.put(c_cardnumber,cardNum);
            queryData.put(c_fonttext,frontText);
            queryData.put(c_backtext,backText);

            DB.obj.insert(tbl_name,null,queryData);
        }else{
            //Log.d("DB Obj","DB was NULL when inserting cardnum");
        }
    }
}
