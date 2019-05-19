package com.example.triptracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DeleteDialog extends AppCompatDialogFragment {

    // Tutorial for dialog https://www.youtube.com/watch?v=Bsm-BlXo2SI
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Delete")
        .setMessage("Are you sure you want to delete this memory")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getContext().getApplicationContext());
                databaseHelper.deleteName(getArguments().getInt("id"));
                Intent openDashBoard = new Intent(getContext().getApplicationContext(),DashboardActivity.class);
                openDashBoard.addFlags(FLAG_ACTIVITY_NEW_TASK);
                openDashBoard.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getContext().startActivity(openDashBoard);
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
