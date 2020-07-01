package com.app.kimiscanner.account;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.kimiscanner.LocalPath;
import com.app.kimiscanner.R;
import com.app.kimiscanner.account.cloudview.SyncFragment;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;
import com.app.kimiscanner.model.FolderInfoChecker;
import com.app.widget.dialog.Dialog;
import com.app.widget.dialog.ListFileDialog;
import com.app.widget.dialog.ListImageDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SyncDataFragment extends Fragment implements SyncFragment.OnListFragmentInteractionListener {

    private AccountFragment.IFragmentInteractionListener activityListener;

    private UserAccount account;

    public SyncDataFragment(UserAccount account) {
        this.account = account;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync_data, container, false);

        EditText usernameEdt = view.findViewById(R.id.acc_email);
        usernameEdt.setText(account.getEmail());

        Button actionBtn = view.findViewById(R.id.auth_logout);
        actionBtn.setOnClickListener(view13 -> activityListener.onLogoutFragmentInteraction());

        // Setup Tabs
        SectionsSyncPagerAdapter sectionsPagerAdapter = new SectionsSyncPagerAdapter(this, getChildFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

//        Button backupBtn = view.findViewById(R.id.acc_backup);
//        backupBtn.setOnClickListener(view12 -> backupData());
//
//        Button restoreBtn = view.findViewById(R.id.acc_restore);
//        restoreBtn.setOnClickListener(view1 -> restoreData());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AccountFragment.IFragmentInteractionListener) {
            activityListener = (AccountFragment.IFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IFragmentInteractionListener");
        }
    }

/*
    private void backupData() {
        StorageConnector.getInstance().getList(account, new StorageConnector.OnListSuccessListener() {
            @Override
            public void onSuccess(List<String> folderNames) {
                List<FolderInfoChecker> folderCheckers = new ArrayList<>();
                for (FolderInfo folder : FolderCollector.getLocalFolders()) {
                    folderCheckers.add(new FolderInfoChecker(folder));
                }
                folderCheckers = checkExistList(folderCheckers, folderNames);

                new ListFileDialog(
                        getContext(),
                        new Dialog.Callback() {
                            @Override
                            public void onSucceed(Object... messages) {
                                for (Object obj : messages) {
                                    Log.d("Backup", obj.toString());
                                    StorageConnector.getInstance().upload(account, new FolderInfo(obj.toString()));
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                // do nothing
                            }
                        },
                        folderCheckers
                ).show();
            }
        });
    }

    private void restoreData() {
        StorageConnector.getInstance().getList(account, new StorageConnector.OnListSuccessListener() {
            @Override
            public void onSuccess(List<String> folderNames) {
                List<FolderInfoChecker> folderCheckers = new ArrayList<>();
                for (String name : folderNames) {
                    folderCheckers.add(new FolderInfoChecker(name, -1));
                }
                List<String> localList = new ArrayList<>();
                for (FolderInfo folder : FolderCollector.getLocalFolders()) {
                    localList.add(folder.folderName);
                }
                folderCheckers = checkExistList(folderCheckers, localList);

                new ListFileDialog(
                        getContext(),
                        new Dialog.Callback() {
                            @Override
                            public void onSucceed(Object... messages) {
                                for (Object obj : messages) {
                                    Log.d("Restore", obj.toString());
                                    StorageConnector.getInstance().download(account, obj.toString(), LocalPath.ROOT_PATH);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                // do nothing
                            }
                        },
                        folderCheckers
                ).show();
            }
        });
    }
*/

    private List<FolderInfoChecker> checkExistList(List<FolderInfoChecker> checkerList, List<String> localList) {
        for (FolderInfoChecker checker : checkerList) {
            for (String local : localList) {
                if (checker.getName().equals(local)) {
                    checker.isExisted = true;
                    break;
                }
            }
        }
        return checkerList;
    }

    @Override
    public void onListFragmentInteractionBackup(FolderInfo item) {
        backup(account, item);
    }

    @Override
    public void onListFragmentInteractionRestore(CloudFolderInfo item) {
        restore(account, item.folderName);
    }

    @Override
    public void onListFragmentInteraction(FolderInfo item) {
        new ListImageDialog(getContext(), item, new ListImageDialog.OnBackupAction() {
            @Override
            public void backupFolder(FolderInfo folder) {
                backup(account, folder);
            }
        }).show();
    }

    @Override
    public void onListFragmentInteraction(CloudFolderInfo item) {
        new ListImageDialog(getContext(), item, new ListImageDialog.OnRestoreAction() {
            @Override
            public void restoreCloud(CloudFolderInfo folder) {
                restore(account, folder.folderName);
            }
        }).show();
    }

    @Override
    public void onGetListInteraction(StorageConnector.OnListSuccessListener callback) {
        StorageConnector.getInstance().getList(account, callback);
    }

    //--- Action ---//
    protected void backup(UserAccount account, FolderInfo folder) {
        Log.i("ProSync", "Uploading " + folder.folderName);
        Toast.makeText(getContext(), R.string.processing, Toast.LENGTH_LONG).show();
        StorageConnector.getInstance().upload(account, folder);
    }

    protected void restore(UserAccount account, String folderName) {
        Log.i("ProSync", "Downloading " + folderName);
        Toast.makeText(getContext(), R.string.processing, Toast.LENGTH_LONG).show();
        StorageConnector.getInstance().download(account, folderName, LocalPath.ROOT_PATH);
    }
    //--- Action ---//
}
