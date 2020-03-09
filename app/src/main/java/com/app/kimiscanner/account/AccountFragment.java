package com.app.kimiscanner.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.kimiscanner.R;
import com.google.android.gms.common.SignInButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class AccountFragment extends Fragment {

    private ViewHolder viewHolder;

    private IFragmentInteractionListener activityListener;

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
        viewHolder.turnLoggedOut(false);
        activityListener.onLogoutFragmentInteraction();
    }

    public void updateUser(UserAccount account) {
        if (null == account) return;

        viewHolder.turnLoggedIn(account);
    }

    private void backupData() {

    }

    private void restoreData() {

    }

    public interface IFragmentInteractionListener {
        void onSignInFragmentInteraction();

        void onLogoutFragmentInteraction();
    }

}
