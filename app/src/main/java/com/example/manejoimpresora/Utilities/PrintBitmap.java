package com.example.manejoimpresora.Utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class PrintBitmap {
    public static byte[] POS_PrintBMP(Bitmap bitmap, int i, int i2) {
        int i3 = ((i + 7) / 8) * 8;
        int height = ((((bitmap.getHeight() * i3) / bitmap.getWidth()) + 7) / 8) * 8;
        if (bitmap.getWidth() != i3) {
            bitmap = Other.resizeImage(bitmap, i3, height);
        }
        return Other.eachLinePixToCmd(Other.thresholdToBWPic(Other.toGrayscale(bitmap)), i3, i2);
    }

    public Bitmap reSize(Bitmap bitmap, int i, int i2) {
        int i3 = ((i + 7) / 8) * 8;
        int height = (bitmap.getHeight() * i3) / bitmap.getWidth();
        if (bitmap.getWidth() != i3) {
            bitmap = Other.resizeImage(bitmap, i3, height);
        }
        int i4 = (height + 7) / 8;
        return bitmap;
    }

    public byte[] bitmapTobyte(Bitmap bitmap) {
        return Other.eachLinePixToCmd(Other.thresholdToBWPic(bitmap), bitmap.getWidth(), 0);
    }

    public Bitmap toGrayscale(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    public static byte[] Print_1D2A(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] bArr = new byte[102400];
        bArr[0] = PrinterCommands.GS;
        bArr[1] = 42;
        bArr[2] = (byte) (((width - 1) / 8) + 1);
        bArr[3] = (byte) (((height - 1) / 8) + 1);
        int i = 4;
        byte b = 0;
        byte b2 = 0;
        for (int i2 = 0; i2 < width; i2++) {
            for (int i3 = 0; i3 < height; i3++) {
                if (bitmap.getPixel(i2, i3) != -1) {
                    b2 = (byte) (b2 | (128 >> b));
                }
                b = (byte) (b + 1);
                if (b == 8) {
                    bArr[i] = b2;
                    i++;
                    b = 0;
                    b2 = 0;
                }
            }
            if (b % 8 != 0) {
                bArr[i] = b2;
                i++;
                b = 0;
                b2 = 0;
            }
        }
        int i4 = width % 8;
        if (i4 != 0) {
            int i5 = height / 8;
            if (height % 8 != 0) {
                i5++;
            }
            int i6 = 8 - i4;
            byte b3 = 0;
            while (b3 < i5 * i6) {
                bArr[i] = 0;
                b3 = (byte) (b3 + 1);
                i++;
            }
        }
        return bArr;
    }
}
