package com.example.manejoimpresora.Utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class GalleryPhoto {
    final String TAG = getClass().getSimpleName();
    private Context context;
    private Uri photoUri;

    public String getChooserTitle() {
        return "Select Pictures";
    }

    public void setPhotoUri(Uri uri) {
        this.photoUri = uri;
    }

    public GalleryPhoto(Context context2) {
        this.context = context2;
    }

    public Intent openGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        return Intent.createChooser(intent, getChooserTitle());
    }

    public String getPath() {
        if (Build.VERSION.SDK_INT < 11) {
            return RealPathUtil.getRealPathFromURI_BelowAPI11(this.context, this.photoUri);
        }
        if (Build.VERSION.SDK_INT < 19) {
            return RealPathUtil.getRealPathFromURI_API11to18(this.context, this.photoUri);
        }
        return RealPathUtil.getRealPathFromURI_API19(this.context, this.photoUri);
    }
}
