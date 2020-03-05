package com.app.kimiscanner.account;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.kimiscanner.R;
import com.google.android.material.snackbar.Snackbar;


/**
 * A placeholder fragment containing a simple view.
 */
public class AccountFragment extends Fragment {

    private ViewHolder viewHolder;
    private UserAccount account;
    private boolean isLogged = false;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        viewHolder = new ViewHolder();
        viewHolder.load(view);

        loginLocal();

        return view;
    }

    protected class ViewHolder {
        Button loginBtn, backupBtn, restoreBtn;
        LinearLayout syncLayout;
        EditText usernameEdt, passwordEdt;

        public void load(View view) {
            loginBtn = view.findViewById(R.id.auth_login);
            backupBtn = view.findViewById(R.id.acc_backup);
            restoreBtn = view.findViewById(R.id.acc_restore);
            syncLayout = view.findViewById(R.id.acc_sync_layout);
            usernameEdt = view.findViewById(R.id.auth_username);
            passwordEdt = view.findViewById(R.id.auth_password);

            setListener();
        }

        private void setListener() {
            View.OnClickListener listener = getViewListener();
            loginBtn.setOnClickListener(listener);
            backupBtn.setOnClickListener(listener);
            restoreBtn.setOnClickListener(listener);
        }

        public void turnLoggedMode(boolean turnOn) {
            if (turnOn) {
                syncLayout.setVisibility(View.VISIBLE);
                usernameEdt.setEnabled(false);
                passwordEdt.setEnabled(false);
            } else {
                syncLayout.setVisibility(View.GONE);
                usernameEdt.setEnabled(true);
                passwordEdt.setEnabled(true);
                passwordEdt.setText("");
            }
        }
    }

    private View.OnClickListener getViewListener() {
        return view -> {
            int id = view.getId();
            switch (id) {
                case R.id.auth_login:
                    if (!isLogged)
                        loginAccount();
                    else
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

    private void loginLocal() {
        // todo: get local then load to edittext

        account = new UserAccount(
                viewHolder.usernameEdt.getText().toString(),
                viewHolder.passwordEdt.getText().toString()
        );

        if (account.isValid()) {
            isLogged = true;
            viewHolder.turnLoggedMode(true);
        }
    }

    private void loginAccount() {
        account = new UserAccount(
                viewHolder.usernameEdt.getText().toString(),
                viewHolder.passwordEdt.getText().toString()
        );

        if (!account.isValid()) {
            Snackbar.make(getView(), "Username and Password are required!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
            return;
        }

        isLogged = login();
        if (isLogged) {
            // todo: save login info
            viewHolder.turnLoggedMode(true);
        } else {
            Snackbar.make(getView(), "Login fail!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }

    private boolean login() {
        // todo: connect database
        return true;
    }

    private void logoutAccount() {
        // todo: logout
        isLogged = false;
        viewHolder.turnLoggedMode(false);
    }

    private void backupData() {

    }

    private void restoreData() {

    }

}
