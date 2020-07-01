package com.app.kimiscanner.account;

import android.graphics.BitmapFactory;

import com.app.util.FileSizeConvertor;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CloudFolderInfo {
    public StorageReference folderRef;
    public String folderName;
    public int pageNumber;
    public List<String> imageLinks;
    public List<StorageReference> imageRefs;

    public CloudFolderInfo() {
    }


    public String getCoverImage() {
        return imageLinks.get(0);
    }

}
