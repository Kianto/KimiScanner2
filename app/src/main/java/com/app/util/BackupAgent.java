package com.app.util;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.util.Log;

import com.app.kimiscanner.LocalPath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static com.app.kimiscanner.LocalPath.ROOT_PATH;

public class BackupAgent extends BackupAgentHelper {
    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "kimiscanner";


    @Override
    public void onCreate() {
        super.onCreate();

        List<String> fileNames = this.getImageName(retrieveImageFile());
        for (String file : fileNames) {
            Log.e("BACKUP", file);
            FileBackupHelper helper = new FileBackupHelper(this, file);
            this.addHelper(PREFS_BACKUP_KEY, helper);
        }
    }

    @Override
    public File getFilesDir() {
        // return super.getFilesDir();

        // override this method to change the file root directory
        // all the files above must be relative to this path!!
        return new File(ROOT_PATH);
    }

//    @Override
//    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
//                         ParcelFileDescriptor newState) throws IOException {
//        // Hold the lock while the FileBackupHelper performs backup
//        synchronized (MyActivity.sDataLock) {
//            super.onBackup(oldState, data, newState);
//        }
//    }

//    @Override
//    public void onRestore(BackupDataInput data, int appVersionCode,
//                          ParcelFileDescriptor newState) throws IOException {
//        // Hold the lock while the FileBackupHelper restores the file
//        synchronized (MyActivity.sDataLock) {
//            super.onRestore(data, appVersionCode, newState);
//        }
//    }

    private ArrayList<String> getImageName(File[] files) {
        ArrayList<String> fileNameList = new ArrayList<>();
        for (File file : files) {
            fileNameList.add(file.getName());
        }
        return fileNameList;
    }

    private File[] retrieveImageFile() {
        File folder = new File(LocalPath.ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
            }
        });
    }

}
