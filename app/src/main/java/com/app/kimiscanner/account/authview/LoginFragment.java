package com.app.kimiscanner.account.authview;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.AccountFragment;


public class LoginFragment extends AuthFragment {

    private EditText usernameEdt, passwordEdt;

    public static LoginFragment newInstance(AccountFragment fatherFragment, int index) {
        LoginFragment fragment = new LoginFragment();
        setInstance(fragment, fatherFragment, index);
        return fragment;
    }

    @Override
    protected int getNameId() {
        return R.string.auth_login;
    }

    @Override
    protected void setView(View view) {
        view.findViewById(R.id.auth_repassword_layout).setVisibility(View.GONE);

        usernameEdt = view.findViewById(R.id.auth_username);
        passwordEdt = view.findViewById(R.id.auth_password);
    }

    @Override
    protected View.OnClickListener getActionListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();

                if (!email.contains("@") || !email.contains(".") || password.length() < 6) {
                    Toast.makeText(getContext(), "Invalid account!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mFatherFragment.loginAccount(email, password);
            }
        };
    }

}