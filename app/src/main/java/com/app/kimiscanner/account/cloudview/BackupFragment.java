package com.app.kimiscanner.account.cloudview;


import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.account.CloudFolderInfo;
import com.app.kimiscanner.account.StorageConnector;
import com.app.kimiscanner.account.SyncDataFragment;
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
        // Get cloud folders
        mListener.onGetListInteraction(new StorageConnector.OnListSuccessListener() {
            @Override
            public void onSuccess(List<CloudFolderInfo> folders) {
                // Get local folders
                LoadingRunner runner = new ListLocalLoadingTask(progressBar, list -> {
                    if (null != list) {
                        listLayout.setAdapter(new BackupFolderAdapter((List<FolderInfo>) list, folders, mListener));
                    }
                });
                runner.execute();
            }
        });
    }




}