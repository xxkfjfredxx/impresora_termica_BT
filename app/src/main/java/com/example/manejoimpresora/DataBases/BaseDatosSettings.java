package com.example.manejoimpresora.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosSettings extends SQLiteOpenHelper {
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public BaseDatosSettings(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table configuraciones(numero text primary key, numPrints txt, otro1 txt, otro2 txt, otra3 txt)");
    }
}
