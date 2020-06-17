package com.app.kimiscanner.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.app.kimiscanner.BaseView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.app.kimiscanner.R;

import java.util.Arrays;

public class AccountActivity extends BaseView.BaseActivity implements AccountFragment.IFragmentInteractionListener {
    private static final int RC_SIGN_IN = 9001;
    static final String SIGN_UP_CODE = "sign-up";
    static final String SIGN_IN_CODE = "sign-in";
    static final String SIGN_OUT_CODE = "sign-out";
    static final String RESET_PASSWORD_CODE = "reset-pass";
    static final String SIGN_GOOGLE_CODE = "sign-google";
    static final String REQUEST_SIGN_GOOGLE_CODE = "request-sign-google";
    static final String GET_CURRENT_USER_CODE = "current-user";


    private SyncDataFragment mSyncDataFragment;

    public AccountActivity() {
        setPresenter(new AccountPresenter(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Backup and Restore");
        }

        presenter.setUp(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            presenter.process(SIGN_GOOGLE_CODE, data);
        }
    }


    private void updateUser(UserAccount user) {
        if (null != user) {
            mSyncDataFragment = new SyncDataFragment(user);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            transaction.replace(R.id.account_fragment, mSyncDataFragment);
            transaction.commit();
        }
    }

    private void backToAuth() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.remove(mSyncDataFragment);
        transaction.commit();

        mSyncDataFragment = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.process(GET_CURRENT_USER_CODE, null);
    }

    // <=== Fragment Interaction ===>
    @Override
    public void onGoogleSignInFragmentInteraction() {
        hideKeyboard();
        presenter.process(REQUEST_SIGN_GOOGLE_CODE, RC_SIGN_IN);
    }

    @Override
    public void onRegisterFragmentInteraction(String email, String password) {
        hideKeyboard();
        presenter.process(SIGN_UP_CODE, Arrays.asList(email, password));
    }

    @Override
    public void onSignInFragmentInteraction(String email, String password) {
        hideKeyboard();
        presenter.process(SIGN_IN_CODE, Arrays.asList(email, password));
    }

    @Override
    public void onForgotFragmentInteraction(String email) {
        hideKeyboard();
        presenter.process(RESET_PASSWORD_CODE, email);
    }

    @Override
    public void onLogoutFragmentInteraction() {
        presenter.process(SIGN_OUT_CODE, null);
    }
    // </== Fragment Interaction ==/>

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void changeView(String codeState, Object output) {
        if (codeState.equals(GET_CURRENT_USER_CODE)) {
            updateUser((UserAccount) output);

        } else if (codeState.equals(SIGN_OUT_CODE)) {
            backToAuth();
        }
    }

}
