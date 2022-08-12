package com.example.manejoimpresora.DataBases;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosConfiguraciones2 extends SQLiteOpenHelper {
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public BaseDatosConfiguraciones2(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table configuraciones(numero INTEGER primary key AUTOINCREMENT, printerName txt, foto BLOB, printSize txt, otra txt, otra1 txt, otra2 txt, otra3 txt, otra4 txt)");
    }
}
