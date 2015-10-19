/*
 * Copyright (c) 2015 Fabio Berta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.berta.fabio.popularmovies.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ch.berta.fabio.popularmovies.R;

/**
 * Shows the available sort options and lets the user choose one.
 */
public class SortMoviesDialogFragment extends DialogFragment {

    private static final String BUNDLE_OPTIONS = "bundle_options";
    private static final String BUNDLE_CHECKED = "bundle_selected";
    private DialogInteractionListener mListener;
    private String[] mSortOptions;
    private int mSortChecked;
    private int mSortSelected;

    public SortMoviesDialogFragment() {
        // required empty constructor
    }

    /**
     * Returns a new instance of a {@link SortMoviesDialogFragment} with list of
     * sort options and the currently selected sort option.
     *
     * @param sortOptions the available sort options
     * @param sortChecked the currently selected sort option
     * @return a new instance of a {@link SortMoviesDialogFragment}
     */
    public static SortMoviesDialogFragment newInstance(String[] sortOptions, int sortChecked) {
        SortMoviesDialogFragment dialog = new SortMoviesDialogFragment();

        Bundle args = new Bundle();
        args.putStringArray(BUNDLE_OPTIONS, sortOptions);
        args.putInt(BUNDLE_CHECKED, sortChecked);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (DialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mSortOptions = args.getStringArray(BUNDLE_OPTIONS);
            mSortChecked = args.getInt(BUNDLE_CHECKED);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.dialog_sort_title)
                .setSingleChoiceItems(mSortOptions, mSortChecked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSortSelected = which;
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSortOptionSelected(mSortSelected);
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return dialogBuilder.create();
    }

    public interface DialogInteractionListener {
        /**
         * Persist the chosen sort option to SharedPreferences and loads the appropriate fragment.
         * @param optionIndex the index of the chosen sort option
         */
        void onSortOptionSelected(int optionIndex);
    }
}
