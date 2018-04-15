package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.pets.data.petContract.PetEntry;
import static com.example.android.pets.data.petContract.PetEntry._ID;

/**
 * Created by khalid-Elnagar on 2/18/2018.
 */

public class PetProvider extends ContentProvider {

    private static final String LOG = PetProvider.class.getSimpleName();
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(petContract.CONTENT_AUTHORITY, petContract.PATH_PETS, PETS);
        sUriMatcher.addURI(petContract.CONTENT_AUTHORITY + "/#", petContract.PATH_PETS, PET_ID);


    }

    private PetDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        //first check the sanity of the data
        checkSanity(values, false);
        //after the Sanity Checks if every thing is fine insert the new pet

        long id = mDbHelper.getWritableDatabase()
                .insert(PetEntry.TABLE_NAME, null, values);

        if (id != -1)
            // Once we know the ID of the new row in the table,
            // return the new URI with the ID appended to the end of it
            return ContentUris.withAppendedId(uri, id);
        else {
            Log.e(LOG, "error with inserting the pet for " + uri);
            return null;
        }

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //get the database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //delete the rows that match the selection and selection Args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);

            case PET_ID:
                //delete single row that given in the uri
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("error with deleting " + uri);
        }

    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        int effectedRows = 0;
        //first check the sanity of the data
        checkSanity(values, true);

        // TODO: Update the selected pets in the pets database table with the given ContentValues

        effectedRows = mDbHelper.getWritableDatabase().update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
        Log.v("provider", "the number of effected rows is " + effectedRows);
        // TODO: Return the number of rows that were affected

        return effectedRows;
    }

    //              Sanity Checks
    private void checkSanity(ContentValues values, boolean isUpdate) {


        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        //check if the pet name is null. if so throw Exception
        if (name == null)
            if (isUpdate && !values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            } else
                throw new IllegalArgumentException("the name should be not null");

        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        //check if the weight is null or negative. if so throw Exception
        if (weight == null || weight < 0)
            if (isUpdate && !values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            } else
                throw new IllegalArgumentException("weight should'nt be negative");

        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        //check if the gender is{unknown(0),male(1),femaleP(2)}if else throw Exception
        if (gender == null || !PetEntry.isValidGender(gender))
            //if is update do nothing
            if (isUpdate && !values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            } else
                throw new IllegalArgumentException("gender error ");

    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS://return the  mime type of a list of pet
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID://return the  mime type of single pet
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }


    }

}



