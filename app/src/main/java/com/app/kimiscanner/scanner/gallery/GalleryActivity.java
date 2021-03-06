package com.app.kimiscanner.scanner.gallery;

import android.os.Bundle;

import com.app.kimiscanner.PermissionHelper;
import com.app.kimiscanner.R;
import com.app.kimiscanner.scanner.CropFragment;
import com.app.kimiscanner.scanner.PhotoStore;
import com.app.kimiscanner.scanner.ProcessFragment;
import com.app.kimiscanner.scanner.ScanFragment;
import com.app.util.LanguageManager;
import com.app.widget.dialog.DeleteDialog;
import com.app.widget.dialog.Dialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

public class GalleryActivity extends AppCompatActivity
        implements GalleryFragment.OnListFragmentInteractionListener,
        ScanFragment.IFragmentInteractionListener {

    private PermissionHelper permissionHelper = new PermissionHelper(this);
    private Fragment mProcessFragment, mCropFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(LanguageManager.getInstance().getString(R.string.title_activity_gallery));
        }

        if (!permissionHelper.hasAllPermissionGranted()) {
            permissionHelper.requestPermission();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                backAction();
                break;
        }
        return true;
    }

    @Override
    public void onListFragmentInteraction(int index) {
        PhotoStore.getInstance().setProcessingIndex(index);
        onCameraFragmentInteraction();
    }

    @Override
    public void onBackPressed() {
        backAction();
    }

    private void backAction() {
        if (null != mCropFragment) {
            onCloseFragmentInteraction((ScanFragment) mCropFragment);
            return;
        } else if (null != mProcessFragment) {
            onCloseFragmentInteraction((ScanFragment) mProcessFragment);
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
            closingDialog.setWarning(LanguageManager.getInstance().getString(R.string.action_close_review_gallery));
        else
            closingDialog.setWarning(LanguageManager.getInstance().getString(R.string.action_close_gallery));
        closingDialog.show();
    }

    // <== IFragmentInteractionListener ==>
    @Override
    public void onCameraFragmentInteraction() {
        mProcessFragment = new ProcessFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        transaction.replace(R.id.gallery_fragment, mProcessFragment);
        transaction.commit();
    }

    @Override
    public void onProcessFragmentInteraction(boolean isFromCamera) {
        mCropFragment = new CropFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        transaction.replace(R.id.gallery_fragment, mCropFragment);
        transaction.commit();
    }

    @Override
    public void onCloseFragmentInteraction(ScanFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
        transaction.remove(fragment);
        transaction.commit();

        if (fragment instanceof ProcessFragment) {
            mProcessFragment = null;
            FragmentManager manager = getSupportFragmentManager();
            ((GalleryFragment) manager.getFragments().get(0)).update();
        }
        if (fragment instanceof CropFragment) {
            mCropFragment = null;
            this.onCameraFragmentInteraction();
        }

    }

    @Override
    public void onDoneAllWorkInteraction() {
        finish();
    }
    // </== IFragmentInteractionListener ==/>


}
