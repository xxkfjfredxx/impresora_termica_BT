package com.example.manejoimpresora.Utilities;

import android.graphics.pdf.PdfRenderer;

import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fJ \u0010\r\u001a\u00020\u000e2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\bR\u0012\u0010\u0003\u001a\u00060\u0004R\u00020\u0005X.¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X.¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lapps/james1/Probadorimpresorabluetooth/Utilities/OpenPdf;", "", "()V", "currentPage", "Landroid/graphics/pdf/PdfRenderer$Page;", "Landroid/graphics/pdf/PdfRenderer;", "pdfRenderer", "numberPages", "", "context", "Landroid/content/Context;", "documentUri", "Landroid/net/Uri;", "openRenderer", "Landroid/graphics/Bitmap;", "index", "app_release"}, k = 1, mv = {1, 4, 1})
/* compiled from: OpenPdf.kt */
public final class OpenPdf {
    private PdfRenderer.Page currentPage;
    private PdfRenderer pdfRenderer;

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r4 = r4.getContentResolver();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final android.graphics.Bitmap openRenderer(android.content.Context r4, android.net.Uri r5, int r6) throws java.io.IOException {
        /*
            r3 = this;
            java.lang.String r0 = "documentUri"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r5, r0)
            r0 = 0
            if (r4 == 0) goto L_0x0015
            android.content.ContentResolver r4 = r4.getContentResolver()
            if (r4 == 0) goto L_0x0015
            java.lang.String r1 = "r"
            android.os.ParcelFileDescriptor r4 = r4.openFileDescriptor(r5, r1)
            goto L_0x0016
        L_0x0015:
            r4 = r0
        L_0x0016:
            if (r4 == 0) goto L_0x001e
            android.graphics.pdf.PdfRenderer r5 = new android.graphics.pdf.PdfRenderer
            r5.<init>(r4)
            goto L_0x001f
        L_0x001e:
            r5 = r0
        L_0x001f:
            kotlin.jvm.internal.Intrinsics.checkNotNull(r5)
            r3.pdfRenderer = r5
            java.lang.String r4 = "pdfRenderer"
            if (r5 != 0) goto L_0x002b
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
        L_0x002b:
            android.graphics.pdf.PdfRenderer$Page r5 = r5.openPage(r6)
            java.lang.String r6 = "pdfRenderer.openPage(index)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r6)
            r3.currentPage = r5
            java.lang.String r6 = "currentPage"
            if (r5 != 0) goto L_0x003d
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
        L_0x003d:
            int r5 = r5.getWidth()
            android.graphics.pdf.PdfRenderer$Page r1 = r3.currentPage
            if (r1 != 0) goto L_0x0048
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
        L_0x0048:
            int r1 = r1.getHeight()
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r5 = android.graphics.Bitmap.createBitmap(r5, r1, r2)
            android.graphics.pdf.PdfRenderer$Page r1 = r3.currentPage
            if (r1 != 0) goto L_0x0059
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
        L_0x0059:
            r6 = 1
            r1.render(r5, r0, r0, r6)
            android.graphics.pdf.PdfRenderer r6 = r3.pdfRenderer
            if (r6 != 0) goto L_0x0064
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r4)
        L_0x0064:
            r6.getPageCount()
            java.lang.String r4 = "bitmap"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r4)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: apps.james1.Probadorimpresorabluetooth.Utilities.OpenPdf.openRenderer(android.content.Context, android.net.Uri, int):android.graphics.Bitmap");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r3 = r3.getContentResolver();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final int numberPages(android.content.Context r3, android.net.Uri r4) throws java.io.IOException {
        /*
            r2 = this;
            java.lang.String r0 = "documentUri"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)
            r0 = 0
            if (r3 == 0) goto L_0x0015
            android.content.ContentResolver r3 = r3.getContentResolver()
            if (r3 == 0) goto L_0x0015
            java.lang.String r1 = "r"
            android.os.ParcelFileDescriptor r3 = r3.openFileDescriptor(r4, r1)
            goto L_0x0016
        L_0x0015:
            r3 = r0
        L_0x0016:
            if (r3 == 0) goto L_0x001d
            android.graphics.pdf.PdfRenderer r0 = new android.graphics.pdf.PdfRenderer
            r0.<init>(r3)
        L_0x001d:
            kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
            r2.pdfRenderer = r0
            if (r0 != 0) goto L_0x0029
            java.lang.String r3 = "pdfRenderer"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r3)
        L_0x0029:
            int r3 = r0.getPageCount()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: apps.james1.Probadorimpresorabluetooth.Utilities.OpenPdf.numberPages(android.content.Context, android.net.Uri):int");
    }
}
