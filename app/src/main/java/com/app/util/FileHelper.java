package com.app.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.app.kimiscanner.model.FolderInfo;
import com.app.kimiscanner.model.ImageInfo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.app.kimiscanner.LocalPath.ROOT_PATH;
import static com.app.kimiscanner.LocalPath.ROOT_TEMP_PATH;

public class FileHelper {

    public static void saveBitmapFile(Bitmap file, String path) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(new File(path))
            );
            file.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveBitmapFile(Bitmap file, int id, String parentFolder) {
        saveBitmapFile(file, parentFolder + "/" + getNewRandomFileName() + "_" + id + ".jpeg");
    }

    private static void saveFile(String tmpPath, int id, String parentFolder) {
        File file = new File(tmpPath);
        file.renameTo(new File(parentFolder + "/" + getNewRandomFileName() + "_" + id + ".jpeg"));
    }

    public static boolean saveBitmapFiles(@NonNull List<Bitmap> fileList) {
        if (!existSDCard()) {
            // Message: SD Card is not ready
            return false;
        }
        if (fileList.isEmpty()) return false;

        try {
            String parentFolder = createNewFolder();
            for (int i = 0; i < fileList.size(); i++) {
                saveBitmapFile(fileList.get(i), i, parentFolder);
            }
            return true;

        } catch (Exception e) {
            // Message: Saving fatal error
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveAllPhotos(@NonNull List<String> pathList) {
        if (!existSDCard()) {
            // Message: SD Card is not ready
            return false;
        }
        if (pathList.isEmpty()) return false;

        try {
            String parentFolder = createNewFolder();
            for (int i = 0; i < pathList.size(); i++) {
                saveFile(pathList.get(i), i, parentFolder);
            }
            return true;

        } catch (Exception e) {
            // Message: Saving fatal error
            e.printStackTrace();
            return false;
        }
    }

    private static String createNewFolder() {
        File folder = new File(ROOT_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        folder = new File(ROOT_PATH
                + "Kimi_" + getNewRandomFileName() + "/");
        folder.mkdir();
        return folder.getAbsolutePath();
    }

    @SuppressLint("SimpleDateFormat")
    private static String getNewRandomFileName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(System.currentTimeMillis());
    }

    public static String exportPDF(String folderPath, String name) {
        FolderInfo folderInfo = new FolderInfo(folderPath);
        List<ImageInfo> imgList = folderInfo.getImageCollection();
        if (imgList.isEmpty()) return null;

        File rootFile = new File(folderPath);
        String outputFile = name + ".pdf";

        Document document = new Document();
        PdfWriter wPdf;
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(new File(rootFile, outputFile));
            wPdf = PdfWriter.getInstance(document, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        document.open();

        try {
            for (ImageInfo img : imgList) {
                FileInputStream fileInput = new FileInputStream(new File(img.filePath));
                Bitmap bitmap = BitmapFactory.decodeStream(fileInput);

                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArray);
                Image image = Image.getInstance(byteArray.toByteArray());

                image.scaleToFit(PageSize.A4);
                float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
                float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
                image.setAbsolutePosition(x, y);

                document.newPage();
                document.add(image);

                byteArray.close();
                fileInput.close();
            }
            document.close();
            wPdf.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return folderPath + "/" + name + ".pdf";
    }

    private static boolean existSDCard() {
        return Environment.getExternalStorageState().equals("mounted");
    }

}
