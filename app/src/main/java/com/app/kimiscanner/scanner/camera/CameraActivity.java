package com.app.kimiscanner.scanner.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.app.kimiscanner.PermissionHelper;
import com.app.kimiscanner.R;
import com.app.kimiscanner.main.MainActivity;
import com.app.kimiscanner.scanner.CropFragment;
import com.app.kimiscanner.scanner.PhotoStore;
import com.app.kimiscanner.scanner.ProcessFragment;
import com.app.kimiscanner.scanner.ScanFragment;
import com.app.widget.dialog.DeleteDialog;
import com.app.widget.dialog.Dialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity
        implements ScanFragment.IFragmentInteractionListener {

    private PermissionHelper permissionHelper = new PermissionHelper(this);

    private boolean isFromCamera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!permissionHelper.hasAllPermissionGranted()) {
            permissionHelper.requestPermission();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case android.R.id.home:
                backAction();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean res = permissionHelper.handleResult(requestCode, permissions, grantResults);
        if (!res) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        backAction();
    }

    private void backAction() {
        if (getSupportFragmentManager().getFragments().size() > 1) {
            ScanFragment fragment = (ScanFragment) getSupportFragmentManager().getFragments().get(1);
            if (fragment instanceof ProcessFragment) {
                PhotoStore.getInstance().deleteProcessing();
                Toast.makeText(this, R.string.action_cancel_process, Toast.LENGTH_SHORT).show();
            }

            if(fragment instanceof CropFragment && isFromCamera){
                goBackToCameraFragment(fragment);
                isFromCamera = false;
                return;
            }

            this.onCloseFragmentInteraction(fragment);
            return;
        }

        DeleteDialog closingDialog = new DeleteDialog(this, new Dialog.Callback() {
            @Override
            public void onSucceed(Object... unused) {
                finish();
            }

            @Override
            public void onFailure(String error) {
                // do nothing
            }
        });
        if (PhotoStore.getInstance().hasPhoto())
            closingDialog.setWarningId(R.string.action_close_captured_camera);
        else
            closingDialog.setWarningId(R.string.action_close_camera);
        closingDialog.show();
    }

    private void goBackToCameraFragment(ScanFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.remove(fragment);
        transaction.commit();

        FragmentManager manager = getSupportFragmentManager();
        ((CameraFragment)manager.getFragments().get(0)).reset();
    }

    // <@== IFragmentInteractionListener ==@>
    @Override
    public void onCameraFragmentInteraction() {
        Fragment fragment = new ProcessFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        transaction.replace(R.id.camera_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onProcessFragmentInteraction(boolean isFromCamera) {
        this.isFromCamera = isFromCamera;
        Fragment fragment = new CropFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        transaction.replace(R.id.camera_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onCloseFragmentInteraction(ScanFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.remove(fragment);
        transaction.commit();

        if (fragment instanceof ProcessFragment) {
            FragmentManager manager = getSupportFragmentManager();
            ((CameraFragment)manager.getFragments().get(0)).reset();
        }

        if (fragment instanceof CropFragment) {
            this.onCameraFragmentInteraction();
        }

        isFromCamera = false;
    }

    @Override
    public void onDoneAllWorkInteraction() {
        finish();
    }
    // </== IFragmentInteractionListener ==/>

    @Override
    public void onDestroy() {
        setResult(Activity.RESULT_OK, new Intent(this, MainActivity.class));
        PhotoStore.getInstance().clear();
        super.onDestroy();
    }

}
