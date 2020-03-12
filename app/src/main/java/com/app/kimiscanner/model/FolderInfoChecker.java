package com.app.kimiscanner.model;

public class FolderInfoChecker {

    private FolderInfo folder;
    public boolean isChecked;
    public boolean isExisted;

    public FolderInfoChecker(FolderInfo folder) {
        this.folder = folder;
    }

    public FolderInfoChecker(String name, int page) {
        this.folder = FolderInfo.createTempInfo(name, page);
    }

    public String getName() {
        return folder.folderName;
    }

    public String getPage() {
        return String.valueOf(folder.pageNumber);
    }

    public String getPath() {
        return folder.folderPath;
    }

}
