package com.example.android.pets.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.pets.Data.petContract.petsEntry;

/**
 * Created by khali on 2/10/2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    //SQLite command to create table inside the Database
    private static final String CREATE_TABLE = "CREATE TABLE" + petsEntry.TABLE_NAME
            + '(' + petsEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT ," +
            petsEntry.COLUMN_NAME_NAME + "TEXT NOT NULL ," +
            petsEntry.COLUMN_NAME_BREED + "TEXT ," +
            petsEntry.COLUMN_NAME_GENDER + "INTEGER NOT NULL," +
            petsEntry.COLUMN_NAME_WEIGHT + "INTEGER NOT NULL DEFAULT 0 );";


    static int Version = 1;
    static String DbName = "pets.dp";

    public PetDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
