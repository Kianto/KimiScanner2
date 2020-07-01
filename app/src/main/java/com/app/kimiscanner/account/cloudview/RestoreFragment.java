package com.app.kimiscanner.account.cloudview;


import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.account.CloudFolderInfo;
import com.app.kimiscanner.account.StorageConnector;
import com.app.kimiscanner.account.SyncDataFragment;

import java.util.List;

public class RestoreFragment extends SyncFragment {

    protected RestoreFragment(SyncDataFragment fatherFragment) {
        super(fatherFragment);
    }

    public static RestoreFragment newInstance(SyncDataFragment fatherFragment, int index) {
        RestoreFragment fragment = new RestoreFragment(fatherFragment);
        setInstance(fragment, index);
        return fragment;
    }

    @Override
    protected void setUpList(RecyclerView listLayout, ProgressBar progressBar) {
        mListener.onGetListInteraction(new StorageConnector.OnListSuccessListener() {
            @Override
            public void onSuccess(List<CloudFolderInfo> folders) {
                listLayout.setAdapter(new RestoreFolderAdapter(folders, mListener));
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}