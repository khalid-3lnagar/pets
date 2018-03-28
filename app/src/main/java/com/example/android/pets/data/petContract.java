package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * this is contract for Data Pets in the app
 * this's schema of the database
 * Created by khalid on 2/10/2018.
 */

public final class petContract {
    private petContract() {
    }


    public static abstract class petsEntry implements BaseColumns {
        public final static String TABLE_NAME = "pets";
        // columns names
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME_NAME = "name";
        public final static String COLUMN_NAME_BREED = "breed";
        public final static String COLUMN_NAME_WEIGHT = "weight";
        public final static String COLUMN_NAME_GENDER = "gender";
        public final static String[] TABLE_COLUMNS = {_ID, COLUMN_NAME_NAME, COLUMN_NAME_BREED,
                COLUMN_NAME_WEIGHT, COLUMN_NAME_GENDER};
        //GENDER VALUES (0,1,2)
        public final static int GENDER_UNKNOWN = 0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;


    }
}