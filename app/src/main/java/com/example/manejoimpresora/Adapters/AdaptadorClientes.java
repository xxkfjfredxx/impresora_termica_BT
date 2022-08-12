package com.example.manejoimpresora.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.ClientesVo;

import java.util.ArrayList;

public class AdaptadorClientes extends RecyclerView.Adapter<AdaptadorClientes.ViewHolderClientes> implements View.OnClickListener {
    ArrayList<ClientesVo> listaClientes;
    private View.OnClickListener listener;

    public AdaptadorClientes(ArrayList<ClientesVo> arrayList) {
        this.listaClientes = arrayList;
    }

    public ViewHolderClientes onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_clientes, (ViewGroup) null, false);
        inflate.setOnClickListener(this);
        return new ViewHolderClientes(inflate);
    }

    public void onBindViewHolder(ViewHolderClientes viewHolderClientes, int i) {
        viewHolderClientes.eLocal.setText(this.listaClientes.get(i).getLocal());
        viewHolderClientes.eDireccion.setText(this.listaClientes.get(i).getDireccion());
        viewHolderClientes.eNombre.setText(this.listaClientes.get(i).getNombre());
        viewHolderClientes.eTelefono.setText(this.listaClientes.get(i).getTelefono());
        viewHolderClientes.eRuta.setText(this.listaClientes.get(i).getRuta());
    }

    public int getItemCount() {
        return this.listaClientes.size();
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

    public class ViewHolderClientes extends RecyclerView.ViewHolder {
        TextView eDireccion;
        TextView eLocal;
        TextView eNombre;
        TextView eRuta;
        TextView eTelefono;

        public ViewHolderClientes(View view) {
            super(view);
            this.eLocal = (TextView) view.findViewById(R.id.Rcodigo);
            this.eDireccion = (TextView) view.findViewById(R.id.Rproducto);
            this.eNombre = (TextView) view.findViewById(R.id.Rprecio);
            this.eTelefono = (TextView) view.findViewById(R.id.RTelefono);
            this.eRuta = (TextView) view.findViewById(R.id.RRuta);
        }
    }
}
