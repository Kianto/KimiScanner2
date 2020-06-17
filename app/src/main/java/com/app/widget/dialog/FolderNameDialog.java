package com.app.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.app.kimiscanner.R;

public class FolderNameDialog extends Dialog {

    private String oldName;

    public FolderNameDialog(Context context, Callback callback) {
        super(context, callback);
    }

    public void setDefaultName(String defaultName) {
        this.oldName = defaultName;
    }

    @Override
    public void show() {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_rename, null);
        EditText editText = (EditText) inflate.findViewById(R.id.rename_text);
        editText.setSelectAllOnFocus(true);
        editText.setText(oldName);

        new AlertDialog
                .Builder(context)
                .setTitle(context.getString(R.string.action_rename))
                .setView(inflate)
                .setPositiveButton(context.getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) inflate.findViewById(R.id.rename_text);
                        if (editText.getText().toString().trim().equals("")) {
                            callback.onFailure("Error: File name could not be empty !");
                            return;
                        }
                        callback.onSucceed(editText.getText().toString().trim());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}
