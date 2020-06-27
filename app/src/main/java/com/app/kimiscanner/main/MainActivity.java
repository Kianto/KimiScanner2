package com.app.kimiscanner.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.app.kimiscanner.BaseView;
import com.app.kimiscanner.PermissionHelper;
import com.app.kimiscanner.R;
import com.app.kimiscanner.account.AccountActivity;
import com.app.kimiscanner.scanner.camera.CameraActivity;
import com.app.kimiscanner.folder.FolderActivity;
import com.app.kimiscanner.folder.FolderStore;
import com.app.kimiscanner.model.FolderInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseView.BaseActivity
        implements ItemFragment.OnListFragmentInteractionListener {
    public final static int REQUEST_CODE_GALLERY = 42;
    public final static int REQUEST_CODE_CAMERA = 15;
    public final static int REQUEST_CODE_FOLDER = 300;

    final static String LONG_OPEN_FOLDER_OPTION_CODE = "open-option";
    final static String UPDATE_LIST_CODE = "update-list";

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    public MainActivity() {
        super();
        setPresenter(new MainPresenter(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ItemFragment.newInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), CameraActivity.class), REQUEST_CODE_CAMERA);
            }
        });

        new PermissionHelper(this).requestPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_gallery) {
            // TODO: check case out of memory if select too many images
            // allow select one image only
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            this.startActivityForResult(intent, REQUEST_CODE_GALLERY);
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(FolderInfo item) {
        FolderStore.setInstance(item);
        startActivityForResult(new Intent(this, FolderActivity.class), REQUEST_CODE_FOLDER);
    }

    @Override
    public void onLongListFragmentInteraction(FolderInfo item) {
        presenter.process(LONG_OPEN_FOLDER_OPTION_CODE, Arrays.asList(this, item));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (null != resultData) {
                if (null != resultData.getClipData()) {
                    // Process multi selected photos
                    List<Uri> uriList = new ArrayList<>();
                    for (int i = 0; i < resultData.getClipData().getItemCount(); i++) {
                        uriList.add(resultData.getClipData().getItemAt(i).getUri());
                    }

                    GalleryRequester.startGalleryActivity(this, uriList);

                } else {
                    // Process a single selected photo
                    Uri uri = resultData.getData();

                    GalleryRequester.startGalleryActivity(this, uri);
                }
            }
            return;
        }
        this.updateListItemsView();

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        this.updateListItemsView();
    }

    private void updateListItemsView() {
        Fragment itemFragment = getSupportFragmentManager().getFragments().get(0);
        if (itemFragment instanceof ItemFragment) {
            ((ItemFragment) itemFragment).changedNotify();
        }
    }


    @Override
    public void changeView(String codeState, Object output) {
        if (codeState.equals(UPDATE_LIST_CODE)) {
            updateListItemsView();
        }
        // do nothing else
    }

    @Override
    public void onDone(String folderName, String action) {
        super.onDone(folderName, action);
        updateListItemsView();
    }

}
