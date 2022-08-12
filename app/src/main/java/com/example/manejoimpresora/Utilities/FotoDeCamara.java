package com.example.manejoimpresora.Utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FotoDeCamara {
    final String TAG = getClass().getSimpleName();
    private Context context;
    private String photoPath;

    public String getPhotoPath() {
        return this.photoPath;
    }

    public FotoDeCamara(Context context2) {
        this.context = context2;
    }

    public Intent takePhotoIntent() throws IOException {
        File createImageFile;
        Uri uri;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (!(intent.resolveActivity(this.context.getPackageManager()) == null || (createImageFile = createImageFile()) == null)) {
            if (Build.VERSION.SDK_INT < 21) {
                uri = Uri.fromFile(createImageFile);
            } else {
                uri = FileProvider.getUriForFile(this.context, "miercoles.dsl.bluetoothprintprueba.provider", createImageFile);
            }
            intent.putExtra("output", uri);
        }
        return intent;
    }

    private File createImageFile() throws IOException {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        externalStoragePublicDirectory.mkdirs();
        File createTempFile = File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_", ".jpg", externalStoragePublicDirectory);
        this.photoPath = createTempFile.getAbsolutePath();
        return createTempFile;
    }

    public void addToGallery() {
        Uri uri;
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File file = new File(this.photoPath);
        if (Build.VERSION.SDK_INT < 21) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(this.context, "miercoles.dsl.bluetoothprintprueba.provider", file);
        }
        intent.setData(uri);
        this.context.sendBroadcast(intent);
    }
}
