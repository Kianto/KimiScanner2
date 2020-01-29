package com.app.widget.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.kimiscanner.R;

public class DeleteDialog extends Dialog {

    private int warnId;

    public DeleteDialog(Context context, Callback callback) {
        super(context, callback);
    }

    public void setWarningId(int id) {
        this.warnId = id;
    }

    @Override
    public void show() {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        TextView textView = inflate.findViewById(R.id.delete_warning);
        textView.setText(warnId);

        new AlertDialog
                .Builder(context)
                .setTitle(context.getString(R.string.action_remove))
                .setView(inflate)
                .setPositiveButton(context.getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onSucceed("");
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
