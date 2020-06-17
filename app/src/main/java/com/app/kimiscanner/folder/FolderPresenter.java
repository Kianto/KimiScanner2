package com.app.kimiscanner.folder;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.app.kimiscanner.BasePresenter;
import com.app.kimiscanner.BaseView;
import com.app.kimiscanner.R;
import com.app.widget.dialog.DeleteDialog;
import com.app.widget.dialog.FolderNameDialog;

import java.io.File;

public class FolderPresenter extends BasePresenter {
    public FolderPresenter(BaseView.BaseActivity activityView) {
        super(activityView);
    }

    @Override
    public void setUp(View view) {

    }

    @Override
    public Object getResult(String codeState, Object input) {
        switch (codeState) {
            case FolderActivity.SHARE_PDF_CODE: {
                activityView.startActivity(
                        sharePDFMethod(input.toString())
                );
                break;
            }
            case FolderActivity.OPEN_PDF_CODE: {
                activityView.startActivity(
                        openPDFMethod(input.toString())
                );
                break;
            }
            case FolderActivity.SHARE_IMAGE_CODE: {
                activityView.startActivity(
                        shareImageMethod(input.toString())
                );
                break;
            }
            case FolderActivity.RENAME_FOLDER_CODE:
                renameMethod();
                break;
            case FolderActivity.DELETE_FOLDER_CODE:
                deleteMethod();
                break;
        }
        return null;
    }

    private Intent sharePDFMethod(String destPdfPath) {
        File imageFileToShare = new File(destPdfPath);
        Uri contentUri = FileProvider.getUriForFile(activityView, "com.app.kimiscanner.fileprovider", imageFileToShare);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, contentUri);

        return Intent.createChooser(share, "Share via");
    }

    private Intent openPDFMethod(String destPdfPath) {
        File file = new File(destPdfPath);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = FileProvider.getUriForFile(activityView, "com.app.kimiscanner.fileprovider", file);

        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        target.setDataAndType(contentUri,"application/pdf");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return Intent.createChooser(target, "Open file");
    }

    private void renameMethod() {
        FolderNameDialog renameDialog = new FolderNameDialog(activityView, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(Object... newName) {
                if (FolderStore.getInstance().renameFolder(newName[0].toString())) {
                    activityView.changeView(FolderActivity.RENAME_FOLDER_CODE, newName[0].toString());
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(activityView, error, Toast.LENGTH_SHORT).show();
            }
        });

        renameDialog.setDefaultName(FolderStore.getInstance().folder.folderName);
        renameDialog.show();
    }

    private void deleteMethod() {
        DeleteDialog deleteDialog = new DeleteDialog(activityView, new FolderNameDialog.Callback() {
            @Override
            public void onSucceed(Object... newName) {
                if (FolderStore.getInstance().deleteFolder()) {
                    activityView.changeView(FolderActivity.DELETE_FOLDER_CODE, null);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(activityView, error, Toast.LENGTH_SHORT).show();
            }
        });

        deleteDialog.setWarningId(R.string.dialog_delete_folder);
        deleteDialog.show();
    }

    private Intent shareImageMethod(String path) {
        File imageFileToShare = new File(path);
        Uri contentUri = FileProvider.getUriForFile(activityView, "com.app.kimiscanner.fileprovider", imageFileToShare);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, contentUri);

        return Intent.createChooser(share, "Share via");
    }


}
