package com.app.kimiscanner.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.kimiscanner.LocalPath;
import com.app.kimiscanner.R;
import com.app.kimiscanner.model.FolderInfo;
import com.app.widget.dialog.Dialog;
import com.app.widget.dialog.ListFileDialog;
import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class AccountFragment extends Fragment {

    private ViewHolder viewHolder;

    private IFragmentInteractionListener activityListener;

    private UserAccount account;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        viewHolder = new ViewHolder();
        viewHolder.load(view);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentInteractionListener) {
            activityListener = (IFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IFragmentInteractionListener");
        }
    }

    protected class ViewHolder {
        Button logoutBtn, backupBtn, restoreBtn;
        LinearLayout syncLayout;
        EditText usernameEdt;
        SignInButton googleSignBtn;

        public void load(View view) {
            googleSignBtn = view.findViewById(R.id.auth_login);
            logoutBtn = view.findViewById(R.id.auth_logout);
            backupBtn = view.findViewById(R.id.acc_backup);
            restoreBtn = view.findViewById(R.id.acc_restore);
            syncLayout = view.findViewById(R.id.acc_sync_layout);
            usernameEdt = view.findViewById(R.id.auth_username);

            setListener();
        }

        private void setListener() {
            View.OnClickListener listener = getViewListener();
            logoutBtn.setOnClickListener(listener);
            backupBtn.setOnClickListener(listener);
            restoreBtn.setOnClickListener(listener);
            googleSignBtn.setOnClickListener(listener);
        }

        public void turnLoggedOut(boolean turnOn) {
            syncLayout.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
            googleSignBtn.setVisibility(View.VISIBLE);
            usernameEdt.setText("");
        }

        public void turnLoggedIn(UserAccount account) {
            syncLayout.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            googleSignBtn.setVisibility(View.GONE);
            usernameEdt.setText(account.getEmail());
        }
    }

    private View.OnClickListener getViewListener() {
        return view -> {
            int id = view.getId();
            switch (id) {
                case R.id.auth_login:
                    activityListener.onSignInFragmentInteraction();
                    break;

                case R.id.auth_logout:
                    logoutAccount();
                    break;

                case R.id.acc_backup:
                    backupData();
                    break;

                case R.id.acc_restore:
                    restoreData();
                    break;

                default:
            }
        };
    }

    private void logoutAccount() {
        account = null;
        viewHolder.turnLoggedOut(false);
        activityListener.onLogoutFragmentInteraction();
    }

    public void updateUser(UserAccount account) {
        if (null == account) return;

        this.account = account;
        viewHolder.turnLoggedIn(account);
    }

    private void backupData() {
        new ListFileDialog(getContext(), new Dialog.Callback() {
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
        }).show();
    }

    private void restoreData() {
        StorageConnector.getInstance().getList(account, new StorageConnector.OnListSuccessListener() {
            @Override
            public void onSuccess(List<String> folderNames) {
                List<FolderInfo> folderInfos = new ArrayList<>();
                for (String name : folderNames) {
                    folderInfos.add(FolderInfo.createTempInfo(name, 1));
                }

                new ListFileDialog(getContext(), new Dialog.Callback() {
                    @Override
                    public void onSucceed(Object... messages) {
                        for (Object obj : messages) {
                            Log.d("Restore", obj.toString());
                            StorageConnector.getInstance().download(account, obj.toString(), LocalPath.ROOT_PATH);
                        }
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                }).addList(folderInfos).show();
            }
        });
    }

    public interface IFragmentInteractionListener {
        void onSignInFragmentInteraction();

        void onLogoutFragmentInteraction();
    }

}
