package com.example.manejoimpresora.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.ProductosVo;

import java.util.ArrayList;


public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolderProductos> implements View.OnClickListener {
    ArrayList<ProductosVo> listaProductos;
    private View.OnClickListener listener;

    public AdaptadorProductos(ArrayList<ProductosVo> arrayList) {
        this.listaProductos = arrayList;
    }

    public ViewHolderProductos onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_productos, (ViewGroup) null, false);
        inflate.setOnClickListener(this);
        return new ViewHolderProductos(inflate);
    }

    public void onBindViewHolder(ViewHolderProductos viewHolderProductos, int i) {
        viewHolderProductos.eCodigo.setText(this.listaProductos.get(i).getCodigo());
        viewHolderProductos.eProducto.setText(this.listaProductos.get(i).getProducto());
        viewHolderProductos.ePrecio.setText(this.listaProductos.get(i).getPrecio());
    }

    public int getItemCount() {
        return this.listaProductos.size();
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
        TextView eCodigo;
        TextView ePrecio;
        TextView eProducto;

        public ViewHolderProductos(View view) {
            super(view);
            this.eCodigo = (TextView) view.findViewById(R.id.Rcodigo);
            this.eProducto = (TextView) view.findViewById(R.id.Rproducto);
            this.ePrecio = (TextView) view.findViewById(R.id.Rprecio);
        }
    }
}
