package com.app.widget;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public abstract class LoadingRunner {

    private WeakReference<ProgressBar> progressBarRef;
    private LoadingCallback callback;

    private Handler handler;
    private Runnable runnable;

    protected Object returnValue;

    protected LoadingRunner(ProgressBar progressBar, LoadingCallback callback) {
        this.progressBarRef = new WeakReference<>(progressBar);
        this.callback = callback;

        this.handler = new LoadingHandler(this);
        this.runnable = () -> {
            doInBackground();
            handler.sendMessage(new Message());
        };
    }

    protected void onPostExecute() {
        progressBarRef.get().setVisibility(View.GONE);
        callback.onDone(returnValue);
    }

    protected void onPreExecute() {
        progressBarRef.get().setVisibility(View.VISIBLE);
    }

    protected abstract void doInBackground();

    public void execute() {
        onPreExecute();
        new Thread(runnable).start();
    }

    private static class LoadingHandler extends Handler {
        LoadingRunner runner;

        public LoadingHandler(LoadingRunner runner) {
            this.runner = runner;
        }

        @Override
        public void handleMessage(Message message) {
            runner.onPostExecute();
        }
    }

    public interface LoadingCallback {
        void onDone(Object object);
    }

}
