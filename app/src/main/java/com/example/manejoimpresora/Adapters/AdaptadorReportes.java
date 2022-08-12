package com.example.manejoimpresora.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.ReportesVo;

import java.util.ArrayList;


public class AdaptadorReportes extends RecyclerView.Adapter<AdaptadorReportes.ViewHolderProductos> implements View.OnClickListener {
    ArrayList<ReportesVo> listaVen;
    private View.OnClickListener listener;

    public AdaptadorReportes(ArrayList<ReportesVo> arrayList) {
        this.listaVen = arrayList;
    }

    public ViewHolderProductos onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_reportes, (ViewGroup) null, false);
        inflate.setOnClickListener(this);
        return new ViewHolderProductos(inflate);
    }

    public void onBindViewHolder(ViewHolderProductos viewHolderProductos, int i) {
        viewHolderProductos.eProducto.setText(this.listaVen.get(i).getProducto());
        viewHolderProductos.eCantidad.setText(this.listaVen.get(i).getCantidad());
        viewHolderProductos.ePrecio.setText(this.listaVen.get(i).getPrecio());
    }

    public int getItemCount() {
        return this.listaVen.size();
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
        TextView ePrecio;
        TextView eProducto;

        public ViewHolderProductos(View view) {
            super(view);
            this.eProducto = (TextView) view.findViewById(R.id.nProducto);
            this.eCantidad = (TextView) view.findViewById(R.id.nCantidad);
            this.ePrecio = (TextView) view.findViewById(R.id.nValorUni);
        }
    }
}
