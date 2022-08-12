package com.example.manejoimpresora.Utilities;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import androidx.core.view.ViewCompat;
import java.io.UnsupportedEncodingException;

public class Other {
    private static final int WIDTH_58 = 384;
    private static final int WIDTH_80 = 576;
    private static final byte[] chartobyte = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 10, 11, PrinterCommands.CLR, PrinterCommands.CR, 14, 15};
    private static int[] p0 = {0, 128};
    private static int[] p1 = {0, 64};
    private static int[] p2 = {0, 32};
    private static int[] p3 = {0, 16};
    private static int[] p4 = {0, 8};
    private static int[] p5 = {0, 4};
    private static int[] p6 = {0, 2};
    public byte[] buf;
    public int index = 0;

    public static boolean IsHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    public Other(int i) {
        this.buf = new byte[i];
    }

    public static StringBuilder RemoveChar(String str, char c) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt != c) {
                sb.append(charAt);
            }
        }
        return sb;
    }

    public static byte HexCharsToByte(char c, char c2) {
        byte[] bArr = chartobyte;
        return (byte) (((bArr[c - '0'] << 4) & 240) | (bArr[c2 - '0'] & 15));
    }

    public static byte[] HexStringToBytes(String str) {
        int length = str.length();
        if (length % 2 != 0) {
            return null;
        }
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            char charAt = str.charAt(i);
            char charAt2 = str.charAt(i + 1);
            if (!IsHexChar(charAt) || !IsHexChar(charAt2)) {
                return null;
            }
            if (charAt >= 'a') {
                charAt = (char) (charAt - ' ');
            }
            if (charAt2 >= 'a') {
                charAt2 = (char) (charAt2 - ' ');
            }
            bArr[i / 2] = HexCharsToByte(charAt, charAt2);
        }
        return bArr;
    }

    public void UTF8ToGBK(String str) {
        try {
            for (byte b : str.getBytes("GBK")) {
                byte[] bArr = this.buf;
                int i = this.index;
                this.index = i + 1;
                bArr[i] = b;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static byte[] StringTOGBK(String str) {
        try {
            return str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createAppIconText(Bitmap bitmap, String str, float f, boolean z, int i) {
        float f2 = f;
        int i2 = i;
        if (z) {
            Bitmap createBitmap = Bitmap.createBitmap(WIDTH_58, i2, Bitmap.Config.ARGB_8888);
            int width = createBitmap.getWidth();
            Canvas canvas = new Canvas(createBitmap);
            canvas.setBitmap(createBitmap);
            canvas.drawColor(-1);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
            textPaint.setTextSize(f2);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setFakeBoldText(false);
            Canvas canvas2 = canvas;
            StaticLayout staticLayout = new StaticLayout(str, 0, str.length(), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0.0f, true, TextUtils.TruncateAt.END, width);
            canvas2.translate(0.0f, 5.0f);
            staticLayout.draw(canvas2);
            canvas2.save();
            canvas2.restore();
            return createBitmap;
        }
        Bitmap createBitmap2 = Bitmap.createBitmap(WIDTH_80, i2, Bitmap.Config.ARGB_8888);
        int width2 = createBitmap2.getWidth();
        Canvas canvas3 = new Canvas(createBitmap2);
        canvas3.setBitmap(createBitmap2);
        canvas3.drawColor(-1);
        TextPaint textPaint2 = new TextPaint();
        textPaint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        textPaint2.setTextSize(f2);
        textPaint2.setAntiAlias(true);
        textPaint2.setStyle(Paint.Style.FILL);
        textPaint2.setFakeBoldText(false);
        Canvas canvas4 = canvas3;
        StaticLayout staticLayout2 = new StaticLayout(str, 0, str.length(), textPaint2, width2, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0.0f, true, TextUtils.TruncateAt.END, width2);
        canvas4.translate(0.0f, 5.0f);
        staticLayout2.draw(canvas4);
        canvas4.save();
        canvas4.restore();
        return createBitmap2;
    }

    public static byte[] byteArraysToBytes(byte[][] bArr) {
        int i = 0;
        for (byte[] length : bArr) {
            i += length.length;
        }
        byte[] bArr2 = new byte[i];
        int i2 = 0;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            int i4 = 0;
            while (i4 < bArr[i3].length) {
                bArr2[i2] = bArr[i3][i4];
                i4++;
                i2++;
            }
        }
        return bArr2;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int i, int i2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(((float) i) / ((float) width), ((float) i2) / ((float) height));
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap toGrayscale(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
    /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0010 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveMyBitmap(android.graphics.Bitmap r2, java.lang.String r3) {
        /*
            java.io.File r0 = new java.io.File
            java.io.File r1 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r1 = r1.getPath()
            r0.<init>(r1, r3)
            r0.createNewFile()     // Catch:{ IOException -> 0x0010 }
        L_0x0010:
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException | IOException -> 0x0022 }
            r3.<init>(r0)     // Catch:{ FileNotFoundException | IOException -> 0x0022 }
            android.graphics.Bitmap$CompressFormat r0 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ FileNotFoundException | IOException -> 0x0022 }
            r1 = 100
            r2.compress(r0, r1, r3)     // Catch:{ FileNotFoundException | IOException -> 0x0022 }
            r3.flush()     // Catch:{ FileNotFoundException | IOException -> 0x0022 }
            r3.close()     // Catch:{ FileNotFoundException | IOException -> 0x0022 }
        L_0x0022:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: zj.com.customize.sdk.Other.saveMyBitmap(android.graphics.Bitmap, java.lang.String):void");
    }

    public static byte[] thresholdToBWPic(Bitmap bitmap) {
        int[] iArr = new int[(bitmap.getWidth() * bitmap.getHeight())];
        byte[] bArr = new byte[(bitmap.getWidth() * bitmap.getHeight())];
        bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        format_K_threshold(iArr, bitmap.getWidth(), bitmap.getHeight(), bArr);
        return bArr;
    }

    private static void format_K_threshold(int[] iArr, int i, int i2, byte[] bArr) {
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < i2; i5++) {
            for (int i6 = 0; i6 < i; i6++) {
                i3 += iArr[i4] & 255;
                i4++;
            }
        }
        int i7 = (i3 / i2) / i;
        int i8 = 0;
        for (int i9 = 0; i9 < i2; i9++) {
            for (int i10 = 0; i10 < i; i10++) {
                if ((iArr[i8] & 255) > i7) {
                    bArr[i8] = 0;
                } else {
                    bArr[i8] = 1;
                }
                i8++;
            }
        }
    }

    public static void overWriteBitmap(Bitmap bitmap, byte[] bArr) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int i = 0;
        for (int i2 = 0; i2 < height; i2++) {
            for (int i3 = 0; i3 < width; i3++) {
                if (bArr[i] == 0) {
                    bitmap.setPixel(i3, i2, -1);
                } else {
                    bitmap.setPixel(i3, i2, ViewCompat.MEASURED_STATE_MASK);
                }
                i++;
            }
        }
    }

    public static byte[] eachLinePixToCmd(byte[] bArr, int i, int i2) {
        int length = bArr.length / i;
        int i3 = i / 8;
        int i4 = i3 + 8;
        byte[] bArr2 = new byte[(length * i4)];
        int i5 = 0;
        for (int i6 = 0; i6 < length; i6++) {
            int i7 = i6 * i4;
            bArr2[i7 + 0] = PrinterCommands.GS;
            bArr2[i7 + 1] = 118;
            bArr2[i7 + 2] = 48;
            bArr2[i7 + 3] = (byte) (i2 & 1);
            bArr2[i7 + 4] = (byte) (i3 % 256);
            bArr2[i7 + 5] = (byte) (i3 / 256);
            bArr2[i7 + 6] = 1;
            bArr2[i7 + 7] = 0;
            for (int i8 = 0; i8 < i3; i8++) {
                bArr2[i7 + 8 + i8] = (byte) (p0[bArr[i5]] + p1[bArr[i5 + 1]] + p2[bArr[i5 + 2]] + p3[bArr[i5 + 3]] + p4[bArr[i5 + 4]] + p5[bArr[i5 + 5]] + p6[bArr[i5 + 6]] + bArr[i5 + 7]);
                i5 += 8;
            }
        }
        return bArr2;
    }
}
