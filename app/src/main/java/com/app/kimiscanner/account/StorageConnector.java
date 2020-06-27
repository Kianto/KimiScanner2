package com.app.kimiscanner.account;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.app.kimiscanner.model.FolderInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageConnector {
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    // === Singleton === //
    private StorageConnector() {
    }

    private static StorageConnector singleStorageConnector = null;

    public static StorageConnector getInstance() {
        if (null == singleStorageConnector) singleStorageConnector = new StorageConnector();
        return singleStorageConnector;
    }
    // === Singleton === //

    public void getList(UserAccount account, OnListSuccessListener listener) {
        StorageReference riversRef = mStorageRef.child(account.getId() + "/");

        riversRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<String> folders = new ArrayList<>();
                for (StorageReference ref : listResult.getPrefixes()) {
                    folders.add(ref.getName());
                }
                listener.onSuccess(folders);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    interface OnListSuccessListener {
        void onSuccess(List<String> folders);
    }

    public void upload(UserAccount account, FolderInfo folder) {
        //___ Open progress plan ___\\
        DataTransferManager.getInstance().addProgress(folder.folderName, folder.folderName, folder.pageNumber, true);

        StringBuilder folderRef = new StringBuilder(
                account.getId()
                        + "/"
                        + folder.folderName
        );
        for (String filePath : folder.filePaths) {
            StorageReference riversRef = mStorageRef.child(
                    folderRef.toString() + "/" + new File(filePath).getName()
            );
            uploadSingleFile(folder.folderName, riversRef, filePath);
        }
    }

    private void uploadSingleFile(String folderId, StorageReference riversRef, String filePath) {
        Uri file = Uri.fromFile(new File(filePath));

        // Check file existence before upload
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // File exists
                // do nothing
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File does not exist
                if (exception instanceof StorageException &&
                        ((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND
                ) {
                    riversRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...

                                    //___ Update progress plan success ___\\
                                    DataTransferManager.getInstance().updateProgress(folderId, 1);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...

                                    //___ Update progress plan fail ___\\
                                    DataTransferManager.getInstance().updateProgress(folderId, -1612315);
                                }
                            });
                } else {
                    //___ Update progress plan fail ___\\
                    DataTransferManager.getInstance().updateProgress(folderId, -1612315);
                }
            }
        });

    }


    public void download(UserAccount account, String folderName, String rootPath) {
        // create folder if it does not exist
        File folder = new File(rootPath + folderName);
        if (!folder.exists()) folder.mkdirs();

        String folderRef = account.getId() + "/" + folderName;
        StorageReference riversRef = mStorageRef.child(folderRef);

        riversRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        //___ Open progress plan ___\\
                        DataTransferManager.getInstance().addProgress(folderName, folderName, listResult.getItems().size(), false);

                        for (StorageReference ref : listResult.getItems()) {
                            downloadSingleFile(folderName, ref, rootPath + folderName);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void downloadSingleFile(String folderId, StorageReference riversRef, String fullSavePath) {
        try {
            String preNameFile = riversRef.getName().substring(0, riversRef.getName().indexOf('.'));
            File localFile = File.createTempFile(
                    preNameFile,
                    ".jpeg",
                    new File(fullSavePath)
            );

            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...

                            //___ Update progress plan success ___\\
                            DataTransferManager.getInstance().updateProgress(folderId, 1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            // ...

                            //___ Update progress plan fail ___\\
                            DataTransferManager.getInstance().updateProgress(folderId, -1612315);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
            //___ Update progress plan fail ___\\
            DataTransferManager.getInstance().updateProgress(folderId, -1612315);
        }
    }

}
