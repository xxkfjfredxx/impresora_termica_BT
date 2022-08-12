package com.example.manejoimpresora.FragmentsUI.Home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;

public class Home extends Fragment {
    private Button bLink;
    private Button bLink2;
    private String estado = "No";
    private HomeViewModel homeViewModel;
    private ImageView imageViewInicio;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_slideshow, viewGroup, false);

        cargarSuscripcion();
        this.imageViewInicio = (ImageView) inflate.findViewById(R.id.imageViewInicio);
        cargarImagen();
        return inflate;
    }

    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Navigation.findNavController(view);
    }

    public void cargarImagen() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select foto from configuraciones where numero ='" + "1" + "'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            byte[] blob = rawQuery.getBlob(0);
            if (blob == null) {
                return;
            }
            if (blob.length != 0) {
                this.imageViewInicio.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                writableDatabase.close();
                return;
            }
            writableDatabase.close();
            return;
        }
        writableDatabase.close();
    }
}
