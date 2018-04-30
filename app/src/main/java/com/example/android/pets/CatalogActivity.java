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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.petContract.PetEntry;

import static com.example.android.pets.data.petContract.PetEntry.CONTENT_URI;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int CURSOR_LOADER_ID = 0;
    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    PetDbHelper mdbHelper;
    PetCursorAdapter petsAdapter;

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

        petsAdapter = new PetCursorAdapter(this, null);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(petsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //add the empty view to the list
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        petsAdapter.notifyDataSetChanged();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int deletedRows = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
                if (deletedRows > 0) {
                    Log.v("CatalogActivity", deletedRows + " rows deleted from pet database ");
                    Toast.makeText(CatalogActivity.this, "All pets have been deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CatalogActivity.this, R.string.no_pets_to_delete, Toast.LENGTH_SHORT).show();
                }


            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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
            Uri newPetUri = getContentResolver().insert(CONTENT_URI, values);


            Log.d("catalogActivity", "uri is " + newPetUri);
            Toast.makeText(this, "dummy data inserted ", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("catalogActivity", e.getMessage());
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CONTENT_URI, PetEntry.TABLE_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        petsAdapter.swapCursor(data);
        petsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader loader) {
        petsAdapter.swapCursor(null);

    }
}
