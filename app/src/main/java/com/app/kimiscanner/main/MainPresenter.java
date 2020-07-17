package com.app.kimiscanner.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.app.kimiscanner.BasePresenter;
import com.app.kimiscanner.BaseView;
import com.app.kimiscanner.R;
import com.app.kimiscanner.folder.FolderStore;
import com.app.kimiscanner.model.FolderInfo;
import com.app.util.LanguageManager;
import com.app.widget.dialog.DeleteDialog;
import com.app.widget.dialog.Dialog;
import com.app.widget.dialog.FolderNameDialog;
import com.app.widget.dialog.FolderOptionDialog;

import java.io.File;
import java.util.List;

public class MainPresenter extends BasePresenter {
    public MainPresenter(BaseView.BaseActivity activityView) {
        super(activityView);
    }

    @Override
    public void setUp(View view) {

    }

    @Override
    public Object getResult(String codeState, Object input) {
        if (codeState.equals(MainActivity.LONG_OPEN_FOLDER_OPTION_CODE)) {
            List<Object> obj = (List<Object>)input;
            openFolderOption((Context)obj.get(0), (FolderInfo)obj.get(1));
        }
        return null;
    }

    private void openFolderOption(final Context context, FolderInfo item) {
        FolderOptionDialog optionDialog = new FolderOptionDialog(context, new Dialog.Callback() {
            @Override
            public void onSucceed(Object... messages) {
                FolderStore.setInstance(item);
                switch (messages[0].toString()) {
                    case FolderOptionDialog.DIALOG_FOLDER_SHARE:
                        sharePDFMethod(context);
                        break;
                    case FolderOptionDialog.DIALOG_FOLDER_RENAME:
                        renameMethod(context);
                        break;
                    case FolderOptionDialog.DIALOG_FOLDER_DELETE:
                        deleteMethod(context);
                        break;
                    default:
                        FolderStore.clear();
                }
            }

            @Override
            public void onFailure(String error) {
                // do nothing
            }
        });

        optionDialog.setFolderName(item.folderName);
        optionDialog.show();
    }

    // === FOLDER PROCESS HANDLER ===
    private void sharePDFMethod(Context context) {
        String destPdfPath = FolderStore.getInstance().convertPDF();
        File imageFileToShare = new File(destPdfPath);
        Uri contentUri = FileProvider.getUriForFile(context, "com.app.kimiscanner.fileprovider", imageFileToShare);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, contentUri);

        context.startActivity(Intent.createChooser(share, LanguageManager.getInstance().getString(R.string.share_via)));
    }

    private void renameMethod(Context context) {
        FolderNameDialog renameDialog = new FolderNameDialog(context, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(Object... newName) {
                if (FolderStore.getInstance().renameFolder(newName[0].toString())) {
                    FolderStore.clear();
                    activityView.changeView(MainActivity.UPDATE_LIST_CODE, null);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });

        renameDialog.setDefaultName(FolderStore.getInstance().folder.folderName);
        renameDialog.show();
    }

    private void deleteMethod(Context context) {
        DeleteDialog deleteDialog = new DeleteDialog(context, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(Object... unused) {
                if (FolderStore.getInstance().deleteFolder()) {
                    FolderStore.clear();
                    activityView.changeView(MainActivity.UPDATE_LIST_CODE, null);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });

        deleteDialog.setWarning(LanguageManager.getInstance().getString(R.string.dialog_delete_folder));
        deleteDialog.show();
    }
}
