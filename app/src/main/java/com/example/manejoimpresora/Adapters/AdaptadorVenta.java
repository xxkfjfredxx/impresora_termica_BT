package com.example.manejoimpresora.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.VentaVo;

import java.util.ArrayList;

public class AdaptadorVenta extends RecyclerView.Adapter<AdaptadorVenta.ViewHolderProductos> implements View.OnClickListener {
    ArrayList<VentaVo> listDatosMostrar;
    private View.OnClickListener listener;

    public AdaptadorVenta(ArrayList<VentaVo> arrayList) {
        this.listDatosMostrar = arrayList;
    }

    public ViewHolderProductos onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_ventas, (ViewGroup) null, false);
        inflate.setOnClickListener(this);
        return new ViewHolderProductos(inflate);
    }

    public void onBindViewHolder(ViewHolderProductos viewHolderProductos, int i) {
        viewHolderProductos.eProducto.setText(this.listDatosMostrar.get(i).getProducto());
        viewHolderProductos.eCantidad.setText(this.listDatosMostrar.get(i).getCantidad());
        viewHolderProductos.ePrecioUni.setText(this.listDatosMostrar.get(i).getPrecioUni());
        viewHolderProductos.ePrecioTo.setText(this.listDatosMostrar.get(i).getPrecioTo());
    }

    public int getItemCount() {
        return this.listDatosMostrar.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    public void onClick(View view) {
        View.OnClickListener onClickListener = this.listener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder {
        TextView eCantidad;
        TextView ePrecioTo;
        TextView ePrecioUni;
        TextView eProducto;

        public ViewHolderProductos(View view) {
            super(view);
            this.eProducto = (TextView) view.findViewById(R.id.producto);
            this.eCantidad = (TextView) view.findViewById(R.id.cantidad);
            this.ePrecioUni = (TextView) view.findViewById(R.id.valorUni);
            this.ePrecioTo = (TextView) view.findViewById(R.id.valorTo);
        }
    }
}
