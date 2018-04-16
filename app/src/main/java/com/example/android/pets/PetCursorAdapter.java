package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pets.data.petContract;

/**
 * {@link PetCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class PetCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link PetCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /*flags*/);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false);

    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //find the views
        TextView vName = view.findViewById(R.id.pet_name);
        TextView vSummary = view.findViewById(R.id.pet_summary);
        //get the data from the cursor
        String name = cursor.getString(cursor.getColumnIndex(petContract.PetEntry.COLUMN_PET_NAME));


        //get the rest of the data and build
        StringBuilder summary = new StringBuilder();
        summary.append(cursor.getString(cursor.getColumnIndex(petContract.PetEntry.COLUMN_PET_BREED)) + " ");
        summary.append(cursor.getInt(cursor.getColumnIndex(petContract.PetEntry.COLUMN_PET_WEIGHT)) + "kg ");
        //get the gender
        int gender = cursor.getInt(cursor.getColumnIndex(petContract.PetEntry.COLUMN_PET_GENDER));
        switch (gender) {
            case petContract.PetEntry.GENDER_UNKNOWN:
                summary.append(" unknown gender");
                break;
            case petContract.PetEntry.GENDER_MALE:
                summary.append(" male");
                break;
            case petContract.PetEntry.GENDER_FEMALE:
                summary.append(" female");
        }


        //update the ui
        vName.setText(name);
        vSummary.setText(summary.toString());

    }
}
