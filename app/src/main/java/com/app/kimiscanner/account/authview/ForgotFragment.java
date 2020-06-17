package com.app.kimiscanner.account.authview;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.AccountFragment;


public class ForgotFragment extends AuthFragment {

    private EditText usernameEdt;

    public static ForgotFragment newInstance(AccountFragment fatherFragment, int index) {
        ForgotFragment fragment = new ForgotFragment();
        setInstance(fragment, fatherFragment, index);
        return fragment;
    }

    @Override
    protected int getNameId() {
        return R.string.auth_reset;
    }

    @Override
    protected void setView(View view) {
        view.findViewById(R.id.auth_password_layout).setVisibility(View.GONE);
        view.findViewById(R.id.auth_repassword_layout).setVisibility(View.GONE);

        usernameEdt = view.findViewById(R.id.auth_username);
    }

    @Override
    protected View.OnClickListener getActionListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEdt.getText().toString();

                if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(getContext(), "Please fill your email!", Toast.LENGTH_SHORT).show();
                }

                mFatherFragment.forgotPassword(email);
            }
        };
    }

}