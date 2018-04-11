package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * this is contract for Data Pets in the app
 * this's schema of the database
 * Created by khalid on 2/10/2018.
 */

public final class petContract {
    private petContract() {
    }

    //authority for the content provider which is the app package name
    public final static String CONTENT_AUTHORITY = "com.example.android.pets";
    //base content uri for the entire provider
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //path string for the pets table
    public final static String PATH_PETS = "pets";

    public static abstract class PetEntry implements BaseColumns {
        //the content uri for the pets table
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        public final static String TABLE_NAME = "pets";
        // columns names
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_WEIGHT = "weight";
        public final static String COLUMN_PET_GENDER = "gender";
        public final static String[] TABLE_COLUMNS = {_ID, COLUMN_PET_NAME, COLUMN_PET_BREED,
                COLUMN_PET_WEIGHT, COLUMN_PET_GENDER};
        //GENDER VALUES (0,1,2)
        public final static int GENDER_UNKNOWN = 0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */

        public static boolean isValidGender(int gender) {
            return gender == GENDER_UNKNOWN ||
                    gender == GENDER_MALE ||
                    gender == GENDER_FEMALE;
        }
    }
}