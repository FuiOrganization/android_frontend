package br.com.fui.fuiapplication.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import br.com.fui.fuiapplication.R;

/**
 * Created by guilherme on 01/11/17.
 */

public class ConfirmationDialog {
    public static void create(String title, String message, Context mContext,
                              DialogInterface.OnClickListener confirmationAction,
                              DialogInterface.OnClickListener cancellationAction) {
        //if action is null
        if (confirmationAction == null) {
            confirmationAction = defaultOnClickListener();
        }

        //if action is null
        if (cancellationAction == null) {
            cancellationAction = defaultOnClickListener();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.confirm,
                confirmationAction);
        builder.setNegativeButton(android.R.string.cancel, cancellationAction);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static DialogInterface.OnClickListener defaultOnClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };
    }
}
