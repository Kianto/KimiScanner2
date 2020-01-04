package com.app.kimiscanner.folder;

import com.app.kimiscanner.model.FolderInfo;
import com.app.util.FileHelper;

import java.io.File;

import static com.app.kimiscanner.LocalPath.ROOT_PATH;

public class FolderStore {

    public FolderInfo folder;

    private static FolderStore store = null;
    private FolderStore(FolderInfo folder) {
        this.folder = folder;
    }

    public static FolderStore setInstance(FolderInfo folder) {
        if (null == store) {
            store = new FolderStore(folder);
        } else {
            store.folder = folder;
        }
        return store;
    }

    public static FolderStore getInstance() {
        if (null == store) {
           throw new NullPointerException("FileStore must be set first!");
        }
        return store;
    }

    public static void clear() {
        store = null;
    }

    public String convertPDF() {
        return FileHelper.exportPDF(folder.folderPath, folder.folderName);
    }

    public boolean renameFolder(String newName) {
        if (folder.folderName.equals(newName)) return true;

        File file = new File(folder.folderPath);
        File updateFile = new File(ROOT_PATH, newName);

        int index = 1;
        StringBuffer buffer;
        while (updateFile.exists()) {
            buffer = new StringBuffer(newName).append("(").append(index).append(")");
            if (folder.folderName.equals(buffer.toString())) return true;

            updateFile = new File(ROOT_PATH, buffer.toString());
            index++;
        }

        if (file.renameTo(updateFile)) {
            folder.folderName = updateFile.getName();
            folder.folderPath = updateFile.getAbsolutePath();
            return true;
        }
        return false;
    }

    public boolean deleteFolder() {
        for (String file : folder.filePaths) {
            new File(file).delete();
        }

        File file = new File(folder.folderPath);
        return file.delete();
    }

}
