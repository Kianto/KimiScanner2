package com.app.kimiscanner.account.cloudview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.DataTransferManager;
import com.app.kimiscanner.account.StorageConnector;
import com.app.kimiscanner.account.SyncDataFragment;
import com.app.kimiscanner.account.CloudFolderInfo;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;
import com.app.widget.LoadingRunner;

public abstract class SyncFragment extends Fragment implements DataTransferManager.DataListener {
    private static final String ARG_SECTION_NUMBER = "section_sync_number";

    private final SyncDataFragment mFatherFragment;
    protected OnListFragmentInteractionListener mListener;

    private static final int GRID_COLUMN = 2;
    private RecyclerView listLayout;
    private ProgressBar progressBar;

    protected SyncFragment(SyncDataFragment fatherFragment) {
        mFatherFragment = fatherFragment;
        DataTransferManager.getInstance().registerListen(this);
    }

    protected static void setInstance(SyncFragment fragment, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mFatherFragment != null) {
            mListener = (OnListFragmentInteractionListener) mFatherFragment;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sync_tab, container, false);

        listLayout = root.findViewById(R.id.list);
        progressBar = root.findViewById(R.id.cloud_loading);
        setListManager();
        setUpList(listLayout, progressBar);

        return root;
    }

    private void setListManager() {
        Context context = getContext();
        listLayout.setLayoutManager(new GridLayoutManager(context, GRID_COLUMN));
    }

    protected abstract void setUpList(RecyclerView listLayout, ProgressBar progressBar);

    // ListLocalLoadingTask for get local folder list
    protected static class ListLocalLoadingTask extends LoadingRunner {
        ListLocalLoadingTask(ProgressBar progressBar, LoadingRunner.LoadingCallback callback) {
            super(progressBar, callback);
        }

        @Override
        protected void doInBackground() {
            returnValue = FolderCollector.getLocalFolders();
        }

    } // end class ListLocalLoadingTask

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionBackup(FolderInfo item);
        void onListFragmentInteractionRestore(CloudFolderInfo item);

        void onListFragmentInteraction(FolderInfo item);
        void onListFragmentInteraction(CloudFolderInfo item);

        void onGetListInteraction(StorageConnector.OnListSuccessListener callback);
    }

    // === Data Listener === //
    @Override
    public void onUpdateProgress(String folderName, String action, double percent) {

    }
    @Override
    public void onDone(String folderName, String action) {
        setUpList(listLayout, progressBar);
    }
    @Override
    public void onFail(String folderName, String action) {

    }
    // === Data Listener === //
}
