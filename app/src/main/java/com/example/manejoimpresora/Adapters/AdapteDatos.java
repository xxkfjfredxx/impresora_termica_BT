package com.example.manejoimpresora.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.R;

import java.util.ArrayList;

public class AdapteDatos extends RecyclerView.Adapter<AdapteDatos.ViewHolderDatos> {
    ArrayList<String> listDatos;

    public AdapteDatos(ArrayList<String> arrayList) {
        this.listDatos = arrayList;
    }

    public ViewHolderDatos onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderDatos(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, (ViewGroup) null, false));
    }

    public void onBindViewHolder(ViewHolderDatos viewHolderDatos, int i) {
        viewHolderDatos.asignarDatos(this.listDatos.get(i));
    }

    public int getItemCount() {
        return this.listDatos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView dato;

        public ViewHolderDatos(View view) {
            super(view);
            this.dato = (TextView) view.findViewById(R.id.idDato);
        }

        public void asignarDatos(String str) {
            this.dato.setText(str);
        }
    }
}
