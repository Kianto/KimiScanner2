package com.app.kimiscanner.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.app.kimiscanner.scanner.PhotoStore;
import com.app.kimiscanner.scanner.ScanProcessor;
import com.app.kimiscanner.scanner.gallery.GalleryActivity;
import com.app.util.Corners;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

import static com.app.kimiscanner.main.MainActivity.REQUEST_CODE_CAMERA;

public abstract class GalleryRequester {

    public static void startGalleryActivity(Activity activity, List<Uri> uriList) {
        PhotoStore.getInstance().clear();
        boolean hasBitmap = false;

        for (Uri uri : uriList) {
            hasBitmap = hasBitmap | addBitmapToStore(activity, uri);
        }

        if (hasBitmap) {
            Intent intent = new Intent(activity, GalleryActivity.class);
            activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    public static void startGalleryActivity(Activity activity, Uri uri) {
        PhotoStore.getInstance().clear();

        if (addBitmapToStore(activity, uri)) {
            Intent intent = new Intent(activity, GalleryActivity.class);
            activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    private static boolean addBitmapToStore(Activity activity, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(uri, "r");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            image = rotate90(image);
            parcelFileDescriptor.close();

            PhotoStore.getInstance().addBitmap(image);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static Bitmap rotate90(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
