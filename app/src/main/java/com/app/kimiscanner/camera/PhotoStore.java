package com.app.kimiscanner.camera;

import android.graphics.Bitmap;

import com.app.util.Corners;

import java.util.ArrayList;
import java.util.List;

public class PhotoStore {
    private List<Bitmap> processedPhotos;

    private List<Bitmap> capturedPhotos;
    private List<Corners> cornersList;

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

    public Bitmap getNewestBitmap() {
        if (capturedPhotos.isEmpty())
            return null;
        return capturedPhotos.get(capturedPhotos.size() - 1);
    }

    public void addCorners(Corners corners) {
        cornersList.add(corners);
    }

    public Corners getNewestCorners() {
        if (cornersList.isEmpty())
            return null;
        return cornersList.get(cornersList.size() - 1);
    }

    public void saveNewest(Bitmap newest) {
        if (processedPhotos.size() < capturedPhotos.size())
            processedPhotos.add(newest);
        else // updating the old one:
            processedPhotos.set(processedPhotos.size() - 1, newest);
    }

    public void deleteNewest() {
        capturedPhotos.remove(capturedPhotos.size() - 1);
        cornersList.remove(cornersList.size() - 1);
    }

    public void updateNewest(Bitmap newBitmap, int[] points) {
        capturedPhotos.set(capturedPhotos.size() - 1, newBitmap);
        Corners corners = cornersList.get(cornersList.size() - 1);
        corners.setCornersByArray(points);
    }

    public void clear() {
        processedPhotos.clear();
        capturedPhotos.clear();
        cornersList.clear();
    }

    public List<Bitmap> getProcessedPhotos() {
        return processedPhotos;
    }


}
