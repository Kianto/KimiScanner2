package com.app.kimiscanner.account.cloudview;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.LocalPath;
import com.app.kimiscanner.R;
import com.app.kimiscanner.account.AccountFragment;
import com.app.kimiscanner.account.StorageConnector;
import com.app.kimiscanner.account.SyncDataFragment;
import com.app.kimiscanner.account.UserAccount;
import com.app.kimiscanner.account.CloudFolderInfo;
import com.app.kimiscanner.account.authview.AuthFragment;
import com.app.kimiscanner.folder.FolderFragment;
import com.app.kimiscanner.model.FolderInfo;

public abstract class SyncFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_sync_number";

    private final SyncDataFragment mFatherFragment;
    protected OnListFragmentInteractionListener mListener;

    private RecyclerView listLayout;
    private static final int GRID_COLUMN = 2;

    protected SyncFragment(SyncDataFragment fatherFragment) {
        mFatherFragment = fatherFragment;
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
        ProgressBar progressBar = root.findViewById(R.id.cloud_loading);
        setListManager();
        setUpList(listLayout, progressBar);

        return root;
    }

    private void setListManager() {
        Context context = getContext();
        listLayout.setLayoutManager(new GridLayoutManager(context, GRID_COLUMN));
    }

    protected abstract void setUpList(RecyclerView listLayout, ProgressBar progressBar);


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionBackup(FolderInfo item);
        void onListFragmentInteractionRestore(CloudFolderInfo item);

        void onListFragmentInteraction(FolderInfo item);
        void onListFragmentInteraction(CloudFolderInfo item);

        void onGetListInteraction(StorageConnector.OnListSuccessListener callback);
    }

}
