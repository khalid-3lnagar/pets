/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.petContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    PetDbHelper mdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mdbHelper = new PetDbHelper(this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        //get the data from the provider
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, PetEntry.TABLE_COLUMNS,
                null, null, null);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount() + "\n ");
            int[] columnsIndexes;

            columnsIndexes = new int[PetEntry.TABLE_COLUMNS.length];
            //display the column names in the next line of ther number

            int i = 0;

            for (String s : PetEntry.TABLE_COLUMNS) {
                columnsIndexes[i] = cursor.getColumnIndex(s);
                if (s != PetEntry.TABLE_COLUMNS[PetEntry.TABLE_COLUMNS.length - 1])
                    displayView.append(s + " - ");
                else displayView.append(s + "\n\n");
                i++;
            }
            while (cursor.moveToNext()) {
                for (int r : columnsIndexes) {
                    if (r != columnsIndexes[columnsIndexes.length - 1])
                        displayView.append(cursor.getString(r) + " - ");
                    else displayView.append(cursor.getString(r) + "\n");
                }
            }


        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deletePets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //to delete all pets data in the database
    private void deletePets() {

        SQLiteDatabase database = mdbHelper.getWritableDatabase();
        database.delete(PetEntry.TABLE_NAME, "1", null);
        displayDatabaseInfo();
    }

    //to insert dummy data in the database
    private void insertPet() {
        //put dummy data in a ContentValues
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, 1);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        //insert the data inside the database
        try {
            Uri newPetUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            displayDatabaseInfo();
            Log.d("catalogActivity", "uri is " + newPetUri);
            Toast.makeText(this, "dummy data inserted ", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("catalogActivity", e.getMessage());
        }
    }
}
