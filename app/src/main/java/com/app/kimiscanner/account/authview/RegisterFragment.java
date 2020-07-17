package com.app.kimiscanner.account.authview;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.AccountFragment;
import com.app.util.LanguageManager;


public class RegisterFragment extends AuthFragment {

    private EditText usernameEdt, passwordEdt, repasswordEdt;

    public static RegisterFragment newInstance(AccountFragment fatherFragment, int index) {
        RegisterFragment fragment = new RegisterFragment();
        setInstance(fragment, fatherFragment, index);
        return fragment;
    }

    @Override
    protected int getNameId() {
        return R.string.auth_register;
    }

    @Override
    protected void setView(View view) {
        usernameEdt = view.findViewById(R.id.auth_username);
        passwordEdt = view.findViewById(R.id.auth_password);
        repasswordEdt = view.findViewById(R.id.auth_repassword);
    }

    @Override
    protected View.OnClickListener getActionListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();
                String repassword = repasswordEdt.getText().toString();

                if (!email.contains("@") || !email.contains(".") || password.length() < 6) {
                    Toast.makeText(getContext(), LanguageManager.getInstance().getString(R.string.auth_invalid), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(repassword)) {
                    Toast.makeText(getContext(), LanguageManager.getInstance().getString(R.string.auth_repass_wrond), Toast.LENGTH_SHORT).show();
                    return;
                }

                mFatherFragment.registerAccount(email, password);
            }
        };
    }

}