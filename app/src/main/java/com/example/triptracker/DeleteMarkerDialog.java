package com.example.triptracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DeleteMarkerDialog extends AppCompatDialogFragment {

    // Tutorial for dialog https://www.youtube.com/watch?v=Bsm-BlXo2SI
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.delete)
        .setMessage(R.string.delete_marker_sure)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getContext().getApplicationContext());
                databaseHelper.deleteCustomMarker(getArguments().getInt("id")+1);
                Intent openSettings = new Intent(getContext().getApplicationContext(), SettingsActivity.class);
                openSettings.addFlags(FLAG_ACTIVITY_NEW_TASK);
                openSettings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                openSettings.addFlags(FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(openSettings);
            }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
