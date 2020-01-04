package com.app.kimiscanner.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.app.kimiscanner.R;
import com.app.kimiscanner.camera.CameraActivity;
import com.app.kimiscanner.folder.FolderActivity;
import com.app.kimiscanner.folder.FolderStore;
import com.app.kimiscanner.model.FolderInfo;
import com.app.kimiscanner.setting.SettingActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener {
    public final static int REQUEST_CODE_GALLERY = 42;
    public final static int REQUEST_CODE_CAMERA = 15;
    public final static int REQUEST_CODE_FOLDER = 300;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
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
            // TODO:
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (null != resultData) {
                if (null != resultData.getClipData()) {
                    // Process multi selected photos
                    for (int i = 0; i < resultData.getClipData().getItemCount(); i++) {
                        Uri uri = resultData.getClipData().getItemAt(i).getUri();
                    }

                    // TODO: handle many images

                } else {
                    // Process a single selected photo
                    Uri uri = resultData.getData();

//                    try {
//                        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
//
//                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                        parcelFileDescriptor.close();
//
//                        StoreHelper.saveBitmap = image;
//                        Intent intent = new Intent(this, DetectActivity.class);
//                        intent.putExtra("isFromCamera", false);
//                        startActivityForResult(intent, 0);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
            return;
        }

        Fragment itemFragment = getSupportFragmentManager().getFragments().get(0);
        if (itemFragment instanceof ItemFragment) {
            ((ItemFragment) itemFragment).changedNotify();
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

}
