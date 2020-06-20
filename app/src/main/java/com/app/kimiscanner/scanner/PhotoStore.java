package com.app.kimiscanner.scanner;

import android.graphics.Bitmap;

import com.app.util.Corners;

import java.util.ArrayList;
import java.util.List;

public class PhotoStore {
    private List<Bitmap> processedPhotos;

    private List<Bitmap> capturedPhotos;
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
        capturedPhotos.add(newPhoto);
        processingIndex = capturedPhotos.size() - 1;
    }

    public List<Bitmap> getCapturedPhotos() {
        return capturedPhotos;
    }

    public boolean hasPhoto() {
        return null != capturedPhotos && !capturedPhotos.isEmpty();
    }

    public int size() {
        return capturedPhotos.size();
    }

    public Bitmap getProcessingBitmap() {
        if (capturedPhotos.isEmpty())
            return null;
        return capturedPhotos.get(processingIndex);
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
            processedPhotos.add(newest);
        else // updating the old one:
            processedPhotos.set(processingIndex, newest);
    }

    public void deleteProcessing() {
        capturedPhotos.remove(processingIndex);
        cornersList.remove(cornersList.size() - 1);
        processingIndex--;
    }

    public void updateProcessing(Bitmap newBitmap, int[] points) {
        capturedPhotos.set(processingIndex, newBitmap);
        Corners corners = cornersList.get(cornersList.size() - 1);
        corners.setCornersByArray(points);
    }

    public void clear() {
        resetBitmap();

        if(processedPhotos != null) {
            processedPhotos.clear();
        }
        if(capturedPhotos != null) {
            capturedPhotos.clear();
        }
        if(cornersList != null) {
            cornersList.clear();
        }
    }

    public void resetBitmap(){
        if(processedPhotos != null){
            for(int i=0;i<processedPhotos.size();i++){
                processedPhotos.get(i).recycle();
            }
        }

        if(capturedPhotos != null){
            for(int i=0; i<capturedPhotos.size(); i++){
                capturedPhotos.get(i).recycle();
            }
        }
    }

    public List<Bitmap> getProcessedPhotos() {
        return processedPhotos;
    }

    public void setProcessingIndex(int index) {
        processingIndex = index;
    }
}
