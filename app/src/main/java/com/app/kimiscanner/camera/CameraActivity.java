package com.app.kimiscanner.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.app.kimiscanner.PermissionHelper;
import com.app.kimiscanner.R;
import com.app.kimiscanner.main.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;

public class CameraActivity extends AppCompatActivity
        implements ScanFragment.IFragmentInteractionListener {

    private PermissionHelper permissionHelper = new PermissionHelper(this);

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

    }

    @Override
    public void onStart() {
        super.onStart();

        permissionHelper.requestPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case android.R.id.home:
                finish();
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
    public void onProcessFragmentInteraction() {
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
