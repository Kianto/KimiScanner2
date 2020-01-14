package com.app.kimiscanner.model;

import android.graphics.BitmapFactory;

import com.app.util.FileSizeConvertor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FolderInfo {
    public String folderPath;
    public String folderName;
    public String lastModified;
    public String folderSize;
    public int pageNumber;

    public List<String> filePaths;

    public FolderInfo(String path) {
        this(new File(path));
    }

    public FolderInfo(File folder) {

        this.folderPath = folder.getPath();
        this.folderName = folder.getName();
        this.lastModified = formatTime(folder.lastModified());
        this.filePaths = getFilePaths(folder);
        this.pageNumber = filePaths.size();
        this.folderSize = FileSizeConvertor.formatFileSize(getSumSize());
    }

    private List<String> getFilePaths(File rootFile) {
        List<String> paths = new ArrayList<>();

        if (rootFile.isDirectory()) {

            List<String> fileList = new ArrayList<>();
            for (String fileName : rootFile.list()) {
                // ignore original image
                if (fileName.startsWith(".")) continue;

                // get cropped image only
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                    fileList.add(fileName);
                }
            }

            if (fileList.size() > 0) {
                try {
                    Collections.sort(fileList, new Comparator<String>() {
                        public int compare(String str1, String str2) {
                            return str1.substring(str1.length() - 7, str1.length() - 4).compareTo(str2.substring(str2.length() - 7, str2.length() - 4));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (String fileName : fileList) {
                paths.add(rootFile.getPath() + "/" + fileName);
            }
        }

        return sortByName(paths);
    }

    public ImageInfo getCoverImage() {
        ImageInfo coverImage = null;

        if (filePaths.size() > 0) {
            File file = new File(filePaths.get(0));

            coverImage = getImage(file);
            coverImage.fileName = this.folderName;
            coverImage.fileSize = String.valueOf(this.pageNumber);
            coverImage.lastModified = this.lastModified;
            coverImage.parentPath = this.folderPath;
        }

        return coverImage;
    }

    public String getCoverImagePath() {
        if (filePaths.size() > 0) {
            return filePaths.get(0);
        }
        return null;
    }

    private long getSumSize() {
        long res = 0;

        for (String path : filePaths) {
            File file = new File(path);
            res += file.length();
        }

        return res;
    }

    public List<ImageInfo> getImageCollection() {
        List<ImageInfo> collection = new ArrayList<>();

        List<File> fileList = new ArrayList<>();
        for (String path : filePaths) {
            fileList.add(new File(path));
        }

        for (File file : fileList) {
            collection.add(getImage(file));
        }

        return collection;
    }

    private ImageInfo getImage(File image) {
        ImageInfo imageInfo = new ImageInfo();
        // Set info
        imageInfo.fileName = image.getName();
        imageInfo.filePath = image.getPath();
        imageInfo.lastModified = formatTime(image.lastModified());
        imageInfo.fileSize = FileSizeConvertor.formatFileSize(image.length());

        // Set dimension
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), options);
        imageInfo.imageHeight = options.outHeight;
        imageInfo.imageWidth = options.outWidth;

        return imageInfo;
    }

    private String formatTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date lastModDate = new Date(time);
        return simpleDateFormat.format(lastModDate);
    }

    private List<String> sortByName(List<String> list) {
        Collections.sort(list, (s1, s2) -> {
            try {
                return Integer.parseInt(s1.substring(s1.lastIndexOf("_") + 1, s1.indexOf(".")))
                        -
                        Integer.parseInt(s2.substring(s2.lastIndexOf("_") + 1, s2.indexOf(".")));
            } catch (Exception e) {
                return s1.compareTo(s2);
            }
        });

        return list;
    }

    private List<File> sortByDate(List<File> list) {
        Collections.sort(list, (f1, f2) -> (int) (f2.lastModified() - f1.lastModified()));

        return list;
    }


}
