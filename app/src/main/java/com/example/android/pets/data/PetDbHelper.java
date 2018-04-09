package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.pets.data.petContract.petsEntry;

/**
 * Created by khali on 2/10/2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    //SQLite command to create table inside the Database
    private static final String CREATE_TABLE = "CREATE TABLE " + petsEntry.TABLE_NAME
            + '(' + petsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
            petsEntry.COLUMN_PET_NAME + " TEXT NOT NULL ," +
            petsEntry.COLUMN_PET_BREED + " TEXT ," +
            petsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL," +
            petsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0 );";


    static int Version = 1;
    static String DbName = "pets.dp";

    public PetDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public PetDbHelper(Context context) {
        super(context, DbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
