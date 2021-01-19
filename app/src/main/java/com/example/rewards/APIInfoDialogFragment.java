package com.example.rewards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class APIInfoDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.activity_api_key, null))
                .setIcon(R.drawable.logo)
                .setTitle("API Key Needed")
                .setMessage("You need to request an API Key:")
                // Add action buttons
                .setPositiveButton("OK", (dialog, id) -> {
                    // sign in the user ...
                })
                .setNegativeButton("CANCEL", (dialog, id) -> Objects.requireNonNull(APIInfoDialogFragment.this.getDialog()).cancel());
        return builder.create();
    }
}
