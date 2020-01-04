package com.app.widget.Dialog;

import android.content.Context;

public abstract class Dialog {
    protected Context context;
    protected FolderNameDialog.Callback callback;

    public Dialog(Context context, FolderNameDialog.Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    protected abstract void show();

    public interface Callback {

        void onSucceed(String message);
        void onFailure(String error);
    }

}
