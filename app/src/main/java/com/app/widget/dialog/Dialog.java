package com.app.widget.dialog;

import android.content.Context;

public abstract class Dialog {
    protected Context context;
    protected Callback callback;

    public Dialog(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    protected abstract void show();

    public interface Callback {

        void onSucceed(Object... messages);
        void onFailure(String error);
    }

}
