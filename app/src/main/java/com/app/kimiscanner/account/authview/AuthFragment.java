package com.app.kimiscanner.account.authview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.AccountFragment;
import com.app.util.LanguageManager;
import com.google.android.gms.common.SignInButton;

public abstract class AuthFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_auth_number";

    protected AccountFragment mFatherFragment;

    protected static void setInstance(AuthFragment fragment, AccountFragment fatherFragment, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        fragment.mFatherFragment = fatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_authenticate, container, false);

        TextView title = root.findViewById(R.id.auth_title);
        title.setText(LanguageManager.getInstance().getString(getNameId()));

        setView(root);

        Button button = root.findViewById(R.id.auth_log);
        button.setText(LanguageManager.getInstance().getString(getNameId()));
        button.setOnClickListener(getActionListener());

        SignInButton googleBtn = root.findViewById(R.id.auth_google_login);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFatherFragment.loginWithGoogleAccount();
            }
        });

        return root;
    }

    protected abstract int getNameId();
    protected abstract void setView(View view);
    protected abstract View.OnClickListener getActionListener();

}
