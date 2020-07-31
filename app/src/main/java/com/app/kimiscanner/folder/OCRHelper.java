package com.app.kimiscanner.folder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;

public abstract class OCRHelper {
    public static void runTextRecognition(String path, OnTextRecognizedListener listener) {
        InputImage image = InputImage.fromBitmap(getImageBitmap(path), 0);
        TextRecognizer recognizer = TextRecognition.getClient();

        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                listener.onResult(texts.getText());
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private static void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Log.e("OCR", "No text found");
            return;
        }

        for (int i = 0; i < blocks.size(); i++) {
            Log.e("OCR_" + i, blocks.get(i).getText());

//            List<Text.Line> lines = blocks.get(i).getLines();
//            for (int j = 0; j < lines.size(); j++) {
//                Log.e("OCR", lines.get(j).getText());
//
//            }
        }
    }

    private static Bitmap getImageBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    interface OnTextRecognizedListener {
        void onResult(String content);
    }

}
