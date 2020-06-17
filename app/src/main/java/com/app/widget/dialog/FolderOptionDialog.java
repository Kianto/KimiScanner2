package com.app.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.app.kimiscanner.R;

public class FolderOptionDialog extends Dialog {
    public final static String DIALOG_FOLDER_SHARE = "share";
    public final static String DIALOG_FOLDER_RENAME = "rename";
    public final static String DIALOG_FOLDER_DELETE = "delete";

    private String folderName;

    public FolderOptionDialog(Context context, Callback callback) {
        super(context, callback);
    }

    @Override
    public void show() {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_folder_option, null);

        AlertDialog dialog = new AlertDialog
                .Builder(context)
                .setTitle(folderName)
                .setView(inflate)
                .setNegativeButton(context.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();


        inflate.findViewById(R.id.dialog_folder_share).setOnClickListener(view -> {
            callback.onSucceed(DIALOG_FOLDER_SHARE);
            dialog.dismiss();
        });
        inflate.findViewById(R.id.dialog_folder_rename).setOnClickListener(view -> {
            callback.onSucceed(DIALOG_FOLDER_RENAME);
            dialog.dismiss();
        });
        inflate.findViewById(R.id.dialog_folder_delete).setOnClickListener(view -> {
            callback.onSucceed(DIALOG_FOLDER_DELETE);
            dialog.dismiss();
        });

    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

}
