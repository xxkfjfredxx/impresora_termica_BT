package com.example.manejoimpresora.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosProductos extends SQLiteOpenHelper {
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public BaseDatosProductos(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table productos(codigo text primary key, nombre text, valor double, cantidad text)");
    }
}
