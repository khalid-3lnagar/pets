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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static com.example.android.pets.data.petContract.PetEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //id for cursor
    private final int CURSOR_LOADER_ID = 0;
    /**
     * TAG for LOG
     */
    private static String TAG;
    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;

    /**
     * pet uri in case updating pet
     */
    private Uri mCurrentPetUri;
// OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mPetHasChanged boolean to true.


    /**
     * the current state of the pet
     * changed or not
     */
    private boolean mPetHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
        //if not in insert mode don't show the dialog and return back
             if (mCurrentPetUri!=null)  mPetHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getLocalClassName();
        setContentView(R.layout.activity_editor);
        mCurrentPetUri = getIntent().getData();

        // Find all relevant views that we will need to read user input from
        //and add to them mTouchListener
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mNameEditText.setOnTouchListener(mTouchListener);

        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mBreedEditText.setOnTouchListener(mTouchListener);

        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mWeightEditText.setOnTouchListener(mTouchListener);

        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mGenderSpinner.setOnTouchListener(mTouchListener);



        if (mCurrentPetUri != null) {
            setTitle("Edit Pet");
            Log.v(TAG, mCurrentPetUri.toString());
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        } else {
            invalidateOptionsMenu();
        }
        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentPetUri == null) {
            //if the activity in insert mode no need for the delete option menu
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void savePet() {
        String mName, mBreed;
        int mWeight;

        if (!TextUtils.isEmpty(mNameEditText.getText()))
            mName = mNameEditText.getText().toString().trim();
        else {
            Toast.makeText(this, R.string.error_with_saving, Toast.LENGTH_SHORT).show();
            return;

        }

        if (!TextUtils.isEmpty(mBreedEditText.getText()))
            mBreed = mBreedEditText.getText().toString().trim();
        else {
            Toast.makeText(this, R.string.error_with_saving, Toast.LENGTH_SHORT).show();

            return;
        }


        if (!TextUtils.isEmpty(mWeightEditText.getText()))
            mWeight = Integer.parseInt(mWeightEditText.getText().toString().trim());
        else mWeight = 0;


        //get the data and put it in the ContentValues
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, mName);
        values.put(PetEntry.COLUMN_PET_BREED, mBreed);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, mWeight);
        //insert the values in the database using the resolver
        //and make toast say to the user that the pet is saved
        //also finish the Activity and return to the CatalogActivity

        //update case
        if (mCurrentPetUri != null) {
            int updatedPets = getContentResolver().update(mCurrentPetUri, values, null, null);
            if (updatedPets > 0)
                Toast.makeText(this, R.string.pet_updated, Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, R.string.error_with_saving, Toast.LENGTH_SHORT).show();
        } else {//insert case
            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            if (newUri == null)
                Toast.makeText(this, R.string.error_with_saving, Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, R.string.pet_saved, Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //delete the pet after get the user confirm
                showDeleteConfirmationDialog();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, mCurrentPetUri, PetEntry.TABLE_COLUMNS,
                null, null, null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //go to the pet position
        //and return if no pet data
        if (!data.moveToFirst()) return;

        //extract data from the cursor and update ui
        mNameEditText.setText(data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_NAME)));
        mBreedEditText.setText(data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_BREED)));
        mWeightEditText.setText(data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT)));
        mGenderSpinner.setSelection(data.getInt(data.getColumnIndex(PetEntry.COLUMN_PET_GENDER)));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(PetEntry.GENDER_UNKNOWN);

    }

    @Override
    public void onBackPressed() {
        //if the pet not changed continue with handling back button press
        if (!mPetHasChanged) super.onBackPressed();

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener mDialogOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard" button, navigate to parent activity.
                finish();
            }

        };
        //show the dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(mDialogOnClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
                Toast.makeText(EditorActivity.this, R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {

        int deletedRow = getContentResolver().delete(mCurrentPetUri, null, null);
        if (deletedRow == 0)
            Toast.makeText(EditorActivity.this, R.string.editor_delete_pet_failed, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(EditorActivity.this, R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();

        finish();
    }
}