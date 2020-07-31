package com.app.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.app.kimiscanner.R;
import com.app.util.LanguageManager;

public class OCRTextDialog extends Dialog {

    private String content;

    public OCRTextDialog(Context context, Callback callback) {
        super(context, callback);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void show() {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_ocr_text, null);
        EditText editText = (EditText) inflate.findViewById(R.id.ocr_text);
//        editText.setSelectAllOnFocus(true);
        editText.setText(content);

        new AlertDialog
                .Builder(context)
//                .setTitle(LanguageManager.getInstance().getString(R.string.action_rename))
                .setView(inflate)
                .setPositiveButton(LanguageManager.getInstance().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}
