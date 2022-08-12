package com.example.manejoimpresora.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosBills extends SQLiteOpenHelper {
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public BaseDatosBills(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table datosbill(numero text primary key, formatofecha txt, direccion txt, telefono txt, taxes txt, mensaje1 txt, mensaje2 txt, mensaje3 txt, mensaje4 txt, mensaje5 txt)");
    }
}
