package com.app.kimiscanner.folder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.app.kimiscanner.R;
import com.app.widget.dialog.DeleteDialog;
import com.app.widget.dialog.FolderNameDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class FolderActivity extends AppCompatActivity {

    MenuItem linearOption, gridOption;

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
                sharePDFMethod();
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
            openPDFMethod();
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

    private void sharePDFMethod() {
        String destPdfPath = FolderStore.getInstance().convertPDF();

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");

        File imageFileToShare = new File(destPdfPath);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share via"));
    }

    private void openPDFMethod() {
        String destPdfPath = FolderStore.getInstance().convertPDF();

        File file = new File(destPdfPath);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open file");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can not open pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    private void renameMethod() {
        FolderNameDialog renameDialog = new FolderNameDialog(this, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(String newName) {
                if (FolderStore.getInstance().renameFolder(newName)) {
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
            public void onSucceed(String newName) {
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
        if (fragment instanceof FolderActivityFragment) {
            ((FolderActivityFragment) fragment).changeView();
        }
    }



}
