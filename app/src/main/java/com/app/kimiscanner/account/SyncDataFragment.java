package com.app.kimiscanner.account;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.kimiscanner.LocalPath;
import com.app.kimiscanner.R;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;
import com.app.kimiscanner.model.FolderInfoChecker;
import com.app.widget.dialog.Dialog;
import com.app.widget.dialog.ListFileDialog;

import java.util.ArrayList;
import java.util.List;

public class SyncDataFragment extends Fragment {

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

        Button backupBtn = view.findViewById(R.id.acc_backup);
        backupBtn.setOnClickListener(view12 -> backupData());

        Button restoreBtn = view.findViewById(R.id.acc_restore);
        restoreBtn.setOnClickListener(view1 -> restoreData());

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

}
