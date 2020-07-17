package com.app.kimiscanner.account;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.app.kimiscanner.BasePresenter;
import com.app.kimiscanner.BaseView;
import com.app.kimiscanner.R;
import com.app.util.LanguageManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;

public class AccountPresenter extends BasePresenter {
    private static final String TAG = "GoogleActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    
    private View view;

    public AccountPresenter(BaseView.BaseActivity activityView) {
        super(activityView);
    }

    @Override
    public void setUp(View view) {
        this.view = view;

        // Init Google Auth
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activityView.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activityView, gso);
    }

    @Override
    public Object getResult(String codeState, Object input) {
        if (codeState.equals(AccountActivity.REQUEST_SIGN_GOOGLE_CODE)) {
            requestSignInByGoogle((int)input);

        } else if (codeState.equals(AccountActivity.SIGN_GOOGLE_CODE)) {
            signInByGoogle((Intent) input);

        } else if (codeState.equals(AccountActivity.SIGN_IN_CODE)) {
            List<Object> objs = (List<Object>) input;
            signIn(objs.get(0).toString(), objs.get(1).toString());

        } else if (codeState.equals(AccountActivity.SIGN_UP_CODE)) {
            List<Object> objs = (List<Object>) input;
            registerAccount(objs.get(0).toString(), objs.get(1).toString());

        } else if (codeState.equals(AccountActivity.SIGN_OUT_CODE)) {
            signOut();

        } else if (codeState.equals(AccountActivity.RESET_PASSWORD_CODE)) {
            forgotPassword(input.toString());

        } else if (codeState.equals(AccountActivity.GET_CURRENT_USER_CODE)) {
            return getCurrentUser();
        }
        return null;
    }

    private UserAccount getCurrentUser() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (null != currentUser) {
            return new UserAccount(currentUser.getUid(), currentUser.getEmail());
        }
        return null;
    }

    private void requestSignInByGoogle(int reqCode) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activityView.startActivityForResult(signInIntent, reqCode);
    }

    private void signInByGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
            // ...
        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activityView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            process(AccountActivity.GET_CURRENT_USER_CODE, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar.make(view, LanguageManager.getInstance().getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void registerAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activityView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            process(AccountActivity.GET_CURRENT_USER_CODE, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(view, LanguageManager.getInstance().getString(R.string.register_failed), Snackbar.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void forgotPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Snackbar.make(view, "Message sent. Please check your email!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(view, LanguageManager.getInstance().getString(R.string.reset_failed), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activityView, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "signOutWithCredential:success");
                        activityView.changeView(AccountActivity.SIGN_OUT_CODE, null);
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activityView, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            process(AccountActivity.GET_CURRENT_USER_CODE, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(view, LanguageManager.getInstance().getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

}
