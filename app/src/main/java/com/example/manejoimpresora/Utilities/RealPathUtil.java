package com.example.manejoimpresora.Utilities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.loader.content.CursorLoader;

public class RealPathUtil {
    static final String TAG = "GalleryPhoto";

    RealPathUtil() {
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String str;
        Uri uri2 = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String[] split = DocumentsContract.getDocumentId(uri).split(":");
                if (!"primary".equalsIgnoreCase(split[0])) {
                    return "";
                }
                str = Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                str = getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), (String) null, (String[]) null);
            } else if (!isMediaDocument(uri)) {
                return "";
            } else {
                String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                String str2 = split2[0];
                if ("image".equals(str2)) {
                    uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(str2)) {
                    uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(str2)) {
                    uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                str = getDataColumn(context, uri2, "_id=?", new String[]{split2[1]});
            }
            return str;
        }  else {
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return "";
        }
    }

    public static String getRealPathFromURI_API11to18(Context context, Uri uri) {
        Log.d(TAG, "API 11 to 18");
        String str = null;
        Cursor loadInBackground = new CursorLoader(context, uri, new String[]{"_data"}, str, (String[]) null, str).loadInBackground();
        if (loadInBackground == null) {
            return null;
        }
        int columnIndexOrThrow = loadInBackground.getColumnIndexOrThrow("_data");
        loadInBackground.moveToFirst();
        String string = loadInBackground.getString(columnIndexOrThrow);
        loadInBackground.close();
        return string;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri uri) {
        Log.d(TAG, "API Below 11");
        String str = null;
        Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, str, (String[]) null, str);
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return string;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0038  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getDataColumn(android.content.Context r8, android.net.Uri r9, java.lang.String r10, java.lang.String[] r11) {
        /*
            java.lang.String r0 = "_data"
            java.lang.String[] r3 = new java.lang.String[]{r0}
            r7 = 0
            android.content.ContentResolver r1 = r8.getContentResolver()     // Catch:{ all -> 0x0035 }
            r6 = r7
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x0035 }
            r2 = r9
            r4 = r10
            r5 = r11
            android.database.Cursor r8 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0035 }
            if (r8 == 0) goto L_0x002f
            boolean r9 = r8.moveToFirst()     // Catch:{ all -> 0x002c }
            if (r9 != 0) goto L_0x001e
            goto L_0x002f
        L_0x001e:
            int r9 = r8.getColumnIndexOrThrow(r0)     // Catch:{ all -> 0x002c }
            java.lang.String r9 = r8.getString(r9)     // Catch:{ all -> 0x002c }
            if (r8 == 0) goto L_0x002b
            r8.close()
        L_0x002b:
            return r9
        L_0x002c:
            r9 = move-exception
            r7 = r8
            goto L_0x0036
        L_0x002f:
            if (r8 == 0) goto L_0x0034
            r8.close()
        L_0x0034:
            return r7
        L_0x0035:
            r9 = move-exception
        L_0x0036:
            if (r7 == 0) goto L_0x003b
            r7.close()
        L_0x003b:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: apps.james1.Probadorimpresorabluetooth.Utilities.RealPathUtil.getDataColumn(android.content.Context, android.net.Uri, java.lang.String, java.lang.String[]):java.lang.String");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
