package com.app.kimiscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionHelper {
    public static final int REQUEST_CODE_PERMISSION = 300;

    private Activity activity;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public void requestPermission() {
        String[] PERMISSION = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        ActivityCompat.requestPermissions(activity, PERMISSION, REQUEST_CODE_PERMISSION);
    }

    public void showCancelPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.dialog_are_you_sure);

        builder.setNegativeButton(R.string.dialog_deny, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });

        builder.setPositiveButton(R.string.dialog_try_again, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermission();
            }
        });

        builder.setCancelable(false);
        if (!activity.isFinishing()) {
            builder.show();
        }
    }

    public void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.dialog_help_text);

        builder.setNegativeButton(R.string.dialog_not_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });

        builder.setPositiveButton(R.string.dialog_goto_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            }
        });

        builder.setCancelable(false);
        if (!activity.isFinishing()) {
            builder.show();
        }
    }

    public boolean hasAllPermissionGranted(int[] grantResults) {
        for (int res : grantResults) {
            if (res == -1) {
                return false;
            }
        }
        return true;
    }

    public boolean handleResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ( /* if permission is still not granted then ask again or simply warn */
                requestCode != PermissionHelper.REQUEST_CODE_PERMISSION
                || !this.hasAllPermissionGranted(grantResults)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (
                        activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        || activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    this.showCancelPermissionDialog();
                } else {
                    this.showMissingPermissionDialog();
                }
            }
            return false;
        } else {
            Log.d("Permissions", "All permissions have been granted. Go for it!");
            return true;
        }
    }
}
