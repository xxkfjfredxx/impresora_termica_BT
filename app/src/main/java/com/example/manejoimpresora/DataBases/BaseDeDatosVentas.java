package com.example.manejoimpresora.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatosVentas extends SQLiteOpenHelper {
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public BaseDeDatosVentas(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table Ventas (fecha txt, producto txt, cantidad txt, total txt, cliente txt)");
    }
}
