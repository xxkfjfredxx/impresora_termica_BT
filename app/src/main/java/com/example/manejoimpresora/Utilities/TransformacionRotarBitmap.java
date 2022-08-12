package com.example.manejoimpresora.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class TransformacionRotarBitmap extends BitmapTransformation {
    private float anguloRotar = 0.0f;

    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }

    public TransformacionRotarBitmap(Context context, float f) {
        this.anguloRotar = f;
    }

    public Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i2) {
        Matrix matrix = new Matrix();
        matrix.postRotate(this.anguloRotar);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public String getId() {
        return "rotar" + this.anguloRotar;
    }
}
