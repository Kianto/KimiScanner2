package com.app.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.kimiscanner.R;
import com.app.util.LanguageManager;

public class DeleteDialog extends Dialog {

    private String warn;

    public DeleteDialog(Context context, Callback callback) {
        super(context, callback);
    }

    public void setWarning(String w) {
        this.warn = w;
    }

    @Override
    public void show() {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        TextView textView = inflate.findViewById(R.id.delete_warning);
        textView.setText(warn);

        new AlertDialog
                .Builder(context)
                .setTitle(LanguageManager.getInstance().getString(R.string.action_remove))
                .setView(inflate)
                .setPositiveButton(LanguageManager.getInstance().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onSucceed("");
                    }
                })
                .setNegativeButton(LanguageManager.getInstance().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}
