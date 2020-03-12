package com.app.kimiscanner.folder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.app.kimiscanner.R;
import com.app.widget.dialog.DeleteDialog;
import com.app.widget.dialog.FolderNameDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class FolderActivity extends AppCompatActivity implements FolderFragment.OnListFragmentInteractionListener<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(FolderStore.getInstance().folder.folderName);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        sharePDFMethod(FolderStore.getInstance().convertPDF())
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_folder, menu);

        return true;
    }

    @Override
    public void onDestroy() {
        FolderStore.clear();
        super.onDestroy();
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

        } else if (id == R.id.action_open_pdf) {
            try {
                startActivity(
                        openPDFMethod(FolderStore.getInstance().convertPDF())
                );
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Can not open pdf file", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.action_rename) {
            renameMethod();
            return true;

        } else if (id == R.id.action_delete) {
            deleteMethod();
            return true;

        } else if (id == R.id.action_type_view) {
            changeViewMethod();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent sharePDFMethod(String destPdfPath) {
        File imageFileToShare = new File(destPdfPath);
        Uri contentUri = FileProvider.getUriForFile(getBaseContext(), "com.app.kimiscanner.fileprovider", imageFileToShare);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, contentUri);

        return Intent.createChooser(share, "Share via");
    }

    private Intent openPDFMethod(String destPdfPath) {
        File file = new File(destPdfPath);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = FileProvider.getUriForFile(getBaseContext(), "com.app.kimiscanner.fileprovider", file);

        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        target.setDataAndType(contentUri,"application/pdf");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return Intent.createChooser(target, "Open file");
    }

    private void renameMethod() {
        FolderNameDialog renameDialog = new FolderNameDialog(this, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(Object... newName) {
                if (FolderStore.getInstance().renameFolder(newName[0].toString())) {
                    getSupportActionBar().setTitle(FolderStore.getInstance().folder.folderName);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        renameDialog.setDefaultName(getSupportActionBar().getTitle().toString());
        renameDialog.show();
    }

    private void deleteMethod() {
        DeleteDialog deleteDialog = new DeleteDialog(this, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(Object... newName) {
                if (FolderStore.getInstance().deleteFolder()) {
                    finish();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        deleteDialog.setWarningId(R.string.dialog_delete_folder);
        deleteDialog.show();
    }

    private void changeViewMethod() {
        Fragment fragment = getSupportFragmentManager().getFragments().get(0);
        if (fragment instanceof FolderFragment) {
            ((FolderFragment) fragment).changeView();
        }
    }

    // <=== OnListFragmentListener ===>
    @Override
    public void onListFragmentInteraction(String item) {
        // do nothing
    }

    @Override
    public void onLongListFragmentInteraction(String item) {
        startActivity(shareImageMethod(item));
    }
    // </== OnListFragmentListener ==/>

    private Intent shareImageMethod(String path) {
        File imageFileToShare = new File(path);
        Uri contentUri = FileProvider.getUriForFile(getBaseContext(), "com.app.kimiscanner.fileprovider", imageFileToShare);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, contentUri);

        return Intent.createChooser(share, "Share via");
    }
}
