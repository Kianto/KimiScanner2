package com.app.kimiscanner.account.cloudview;


import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.account.SyncDataFragment;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;
import com.app.widget.LoadingRunner;

import java.util.List;

public class BackupFragment extends SyncFragment {

    protected BackupFragment(SyncDataFragment fatherFragment) {
        super(fatherFragment);
    }

    public static BackupFragment newInstance(SyncDataFragment fatherFragment, int index) {
        BackupFragment fragment = new BackupFragment(fatherFragment);
        setInstance(fragment, index);
        return fragment;
    }

    @Override
    protected void setUpList(RecyclerView listLayout, ProgressBar progressBar) {
        LoadingRunner runner = new ListLoadingTask(progressBar, list -> {
            if (null != list)
                listLayout.setAdapter(new BackupFolderAdapter((List<FolderInfo>) list, mListener));
        });
        runner.execute();
    }

    private static class ListLoadingTask extends LoadingRunner {
        ListLoadingTask(ProgressBar progressBar, LoadingRunner.LoadingCallback callback) {
            super(progressBar, callback);
        }

        @Override
        protected void doInBackground() {
            returnValue = FolderCollector.getLocalFolders();
        }

    } // end class ListLoadingTask


}