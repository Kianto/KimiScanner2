package com.app.kimiscanner.folder;

import android.content.ActivityNotFoundException;
import android.os.Bundle;

import com.app.kimiscanner.BaseView;
import com.app.kimiscanner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class FolderActivity extends BaseView.BaseActivity implements FolderFragment.OnListFragmentInteractionListener<String> {
    final static String SHARE_PDF_CODE = "share-pdf";
    final static String OPEN_PDF_CODE = "open-pdf";
    final static String SHARE_IMAGE_CODE = "share-image";
    final static String RENAME_FOLDER_CODE = "rename-folder";
    final static String DELETE_FOLDER_CODE = "delete-folder";

    public FolderActivity() {
        super();
        setPresenter(new FolderPresenter(this));
    }

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
        fab.setOnClickListener(view -> sharePDF());
    }

    @Override
    public void onDestroy() {
        FolderStore.clear();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_folder, menu);

        return true;
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
                presenter.process(OPEN_PDF_CODE, FolderStore.getInstance().convertPDF());
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Can not open pdf file", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.action_rename) {
            presenter.process(RENAME_FOLDER_CODE, null);
            return true;

        } else if (id == R.id.action_delete) {
            presenter.process(DELETE_FOLDER_CODE, null);
            return true;

        } else if (id == R.id.action_type_view) {
            changeViewMethod();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeView(String codeState, Object output) {
        if (codeState.equals(RENAME_FOLDER_CODE)) {
            getSupportActionBar().setTitle(output.toString());
        } else if (codeState.equals(DELETE_FOLDER_CODE)) {
            finish();
        }
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
        presenter.process(SHARE_IMAGE_CODE, item);
    }
    // </== OnListFragmentListener ==/>

    private void sharePDF() {
        presenter.process(SHARE_IMAGE_CODE, FolderStore.getInstance().convertPDF());
    }


}
