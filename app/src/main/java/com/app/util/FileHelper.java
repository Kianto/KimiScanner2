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
import java.text.SimpleDateFormat;
import java.util.List;

import static com.app.kimiscanner.LocalPath.ROOT_PATH;

public class FileHelper {

    private static void saveBitmapFile(Bitmap file, int id, String parentFolder) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(
                            new File(parentFolder + "/"
                                    + getNewRandomFileName() + "_" + id + ".jpeg")
                    )
            );
            file.compress(Bitmap.CompressFormat.JPEG, 85, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
