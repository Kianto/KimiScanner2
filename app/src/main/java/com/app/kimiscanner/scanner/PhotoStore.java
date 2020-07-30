package com.app.kimiscanner.scanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.app.util.Corners;
import com.app.util.FileHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.app.kimiscanner.LocalPath.ROOT_PATH;
import static com.app.kimiscanner.LocalPath.ROOT_TEMP_PATH;

public class PhotoStore {
//    private List<Bitmap> processedPhotos;
    private List<String> processedPhotos;

//    private List<Bitmap> capturedPhotos;
    private List<String> capturedPhotos;
    private List<Corners> cornersList;

    private int processingIndex = 0;

    private PhotoStore() {
        processedPhotos = new ArrayList<>();
        capturedPhotos = new ArrayList<>();
        cornersList = new ArrayList<>();
    }

    private static PhotoStore store = null;
    public static PhotoStore getInstance() {
        if (null == store) {
            store = new PhotoStore();
        }
        return store;
    }

    public void addBitmap(Bitmap newPhoto) {
        processingIndex = capturedPhotos.size();
        capturedPhotos.add(saveImageTemporarily(newPhoto, processingIndex, true));
    }

    public List<String> getCapturedPhotos() {
        return capturedPhotos;
    }

    public boolean hasPhoto() {
        return null != capturedPhotos && !capturedPhotos.isEmpty();
    }

    public int size() {
        return capturedPhotos.size();
    }

    public Bitmap getBitmap(int index) {
        if (capturedPhotos.isEmpty() || 0 > index || index >= capturedPhotos.size())
            return null;
        return getImageBitmap(capturedPhotos.get(index));
    }

    public Bitmap getProcessingBitmap() {
        if (capturedPhotos.isEmpty())
            return null;
        return getImageBitmap(capturedPhotos.get(processingIndex));
    }

    public void addCorners(Corners corners) {
        cornersList.add(corners);
    }

    public Corners getProcessingCorners() {
        if (cornersList.isEmpty())
            return null;
        return cornersList.get(processingIndex);
    }

    public void saveProcessing(Bitmap newest) {
        if (processedPhotos.size() < capturedPhotos.size())
            processedPhotos.add(saveImageTemporarily(newest, processedPhotos.size(), false));
        else // updating the old one:
            processedPhotos.set(processingIndex, saveImageTemporarily(newest, processingIndex, false));
    }

    public void deleteProcessing() {
        capturedPhotos.remove(processingIndex);
        cornersList.remove(cornersList.size() - 1);
        processingIndex--;
    }

    public void updateProcessing(Bitmap newBitmap, int[] points) {
        capturedPhotos.set(processingIndex, saveImageTemporarily(newBitmap, processingIndex, true));
        Corners corners = cornersList.get(cornersList.size() - 1);
        corners.setCornersByArray(points);
    }

    public void clear() {
//        resetBitmap();

        deleteTmpFolder();

        if (processedPhotos != null) {
            processedPhotos.clear();
        }
        if (capturedPhotos != null) {
            capturedPhotos.clear();
        }
        if (cornersList != null) {
            cornersList.clear();
        }
    }

//    public void resetBitmap() {
//        if (processedPhotos != null){
//            for(int i = 0; i < processedPhotos.size(); i++){
//                processedPhotos.get(i).recycle();
//            }
//        }
//
//        if (capturedPhotos != null) {
//            for(int i = 0; i < capturedPhotos.size(); i++){
//                capturedPhotos.get(i).recycle();
//            }
//        }
//    }

    public List<String> getProcessedPhotos() {
        return processedPhotos;
    }

    public void setProcessingIndex(int index) {
        processingIndex = index;
    }

    private String saveImageTemporarily(Bitmap bitmap, int index, boolean isOriginal) {
        File folder = new File(ROOT_TEMP_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String path = ROOT_TEMP_PATH + (isOriginal ? ".img_" : ".img_o_") + String.valueOf(index);

        FileHelper.saveBitmapFile(bitmap, path);
        bitmap.recycle();

        return path;
    }

    private Bitmap getImageBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }


    private void deleteTmpFolder() {
        File folderRoot = new File(ROOT_TEMP_PATH);
        if (null != folderRoot.listFiles()) {
            for (File file : folderRoot.listFiles()) {
                file.delete();
            }
        }
        folderRoot.delete();
    }

}
