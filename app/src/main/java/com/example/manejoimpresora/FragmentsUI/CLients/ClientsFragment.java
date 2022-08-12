package com.example.manejoimpresora.FragmentsUI.CLients;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.Adapters.AdaptadorClientes;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.DataBases.RegistroClientes;
import com.example.manejoimpresora.FragmentsUI.CLients.ClientsViewModel;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.ClientesVo;

import java.util.ArrayList;

public class ClientsFragment extends Fragment {
    private AdaptadorClientes adapter;
    private Button agregar;
    private Button buscar;
    private int clientSaved;
    private ClientsViewModel clientsViewModel;
    private Button editar;
    private EditText edtCliente;
    private EditText edtDireccion;
    private EditText edtTelefono;
    /* access modifiers changed from: private */
    public EditText edtlocal;
    private AutoCompleteTextView edtlocalAuto;
    private Button eliminar;
    private String estado = "No";
    /* access modifiers changed from: private */
    public ArrayList<ClientesVo> listaClientes;
    private ArrayList<String> listaNombreLocal;
    /* access modifiers changed from: private */
    public RecyclerView recyclerClientes;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_clients, viewGroup, false);
        this.agregar = (Button) inflate.findViewById(R.id.buttonSave);
        this.buscar = (Button) inflate.findViewById(R.id.buttonSearh);
        this.editar = (Button) inflate.findViewById(R.id.buttonEdit);
        this.eliminar = (Button) inflate.findViewById(R.id.buttonDelete);
        this.edtlocal = (EditText) inflate.findViewById(R.id.txtComercial);
        this.edtlocalAuto = (AutoCompleteTextView) inflate.findViewById(R.id.editNombre3);
        this.edtDireccion = (EditText) inflate.findViewById(R.id.txtDireccion);
        this.edtCliente = (EditText) inflate.findViewById(R.id.txtCliente);
        this.edtTelefono = (EditText) inflate.findViewById(R.id.txtTel1);
        this.listaClientes = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.RecyclerClientes);
        this.recyclerClientes = recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        this.clientSaved = 0;
        cargarSuscripcion();
        this.agregar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClientsFragment.this.registrar();
            }
        });
        this.buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClientsFragment.this.buscar();
            }
        });
        this.editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClientsFragment.this.modificar();
            }
        });
        this.eliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClientsFragment.this.eliminar();
            }
        });
        cargarRecycler();
        listaClientes();
        configurarAutcompletar();
        return inflate;
    }

    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion", null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
    }

    public void configurarAutcompletar() {
        this.edtlocalAuto.setAdapter(new ArrayAdapter(getContext(), 17367050, this.listaNombreLocal));
        this.edtlocalAuto.setThreshold(1);
        this.edtlocalAuto.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("beforeTextChanged", String.valueOf(charSequence));
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("onTextChanged", String.valueOf(charSequence));
            }

            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", String.valueOf(editable));
                ClientsFragment.this.cargar();
            }
        });
    }

    public void cargar() {
        SQLiteDatabase writableDatabase = new RegistroClientes(getContext(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.edtlocalAuto.getText().toString();
        if (!obj.isEmpty()) {
            Cursor rawQuery = writableDatabase.rawQuery("select local, direccion, cliente, telefono from clientes where cliente ='" + obj + "'", (String[]) null);
            if (rawQuery.moveToFirst()) {
                this.edtlocal.setText(rawQuery.getString(0));
                this.edtDireccion.setText(rawQuery.getString(1));
                this.edtCliente.setText(rawQuery.getString(2));
                this.edtTelefono.setText(rawQuery.getString(3));
                writableDatabase.close();
                this.edtlocalAuto.setText("");
                return;
            }
            writableDatabase.close();
        }
    }

    private void listaClientes() {
        this.listaNombreLocal = new ArrayList<>();
        Cursor rawQuery = new RegistroClientes(getActivity(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select cliente from clientes ", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                this.listaNombreLocal.add(rawQuery.getString(0));
            } while (rawQuery.moveToNext());
        }
    }

    public void borrarData() {
        this.listaClientes.clear();
        this.adapter.notifyDataSetChanged();
    }

    public void cargarRecycler() {
        Cursor rawQuery = new RegistroClientes(getContext(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select local, direccion, cliente, telefono from clientes ", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                this.listaClientes.add(new ClientesVo(rawQuery.getString(0), rawQuery.getString(1), rawQuery.getString(2), rawQuery.getString(3), ""));
            } while (rawQuery.moveToNext());
        }
        AdaptadorClientes adaptadorClientes = new AdaptadorClientes(this.listaClientes);
        this.adapter = adaptadorClientes;
        adaptadorClientes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClientsFragment.this.edtlocal.setText(((ClientesVo) ClientsFragment.this.listaClientes.get(ClientsFragment.this.recyclerClientes.getChildAdapterPosition(view))).getLocal());
                ClientsFragment.this.buscar();
            }
        });
        this.recyclerClientes.setAdapter(this.adapter);
    }

    public void buscar() {
        SQLiteDatabase writableDatabase = new RegistroClientes(getContext(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.edtlocal.getText().toString();
        if (!obj.isEmpty()) {
            Cursor rawQuery = writableDatabase.rawQuery("select direccion, cliente, telefono from clientes where local ='" + obj + "'", (String[]) null);
            if (rawQuery.moveToFirst()) {
                this.edtDireccion.setText(rawQuery.getString(0));
                this.edtCliente.setText(rawQuery.getString(1));
                this.edtTelefono.setText(rawQuery.getString(2));
                writableDatabase.close();
                return;
            }
            Toast.makeText(getContext(), getString(R.string.toastClientnotexist), Toast.LENGTH_SHORT).show();
            writableDatabase.close();
            return;
        }
        Toast.makeText(getContext(), getString(R.string.toastEnterBussName), Toast.LENGTH_SHORT).show();
    }

    public void eliminar() {
        SQLiteDatabase writableDatabase = new RegistroClientes(getContext(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.edtlocal.getText().toString();
        if (!obj.isEmpty()) {
            int delete = writableDatabase.delete("clientes", "local ='" + obj + "'", (String[]) null);
            writableDatabase.close();
            limpiarCampos();
            if (delete == 1) {
                borrarData();
                cargarRecycler();
                Toast.makeText(getContext(), getString(R.string.toastClientRemov), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), getString(R.string.toastClientnotexist), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), getString(R.string.toastEnterBussName), Toast.LENGTH_SHORT).show();
    }

    public void limpiarCampos() {
        this.edtlocal.setText("");
        this.edtDireccion.setText("");
        this.edtCliente.setText("");
        this.edtTelefono.setText("");
    }

    public void registrar() {
        SQLiteDatabase writableDatabase = new RegistroClientes(getContext(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.edtlocal.getText().toString();
        String obj2 = this.edtDireccion.getText().toString();
        String obj3 = this.edtCliente.getText().toString();
        String obj4 = this.edtTelefono.getText().toString();
        if (obj.isEmpty() || obj2.isEmpty() || obj3.isEmpty() || obj4.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.toastCompletField), 0).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("Local", obj);
        contentValues.put("direccion", obj2);
        contentValues.put("cliente", obj3);
        contentValues.put("telefono", obj4);
        writableDatabase.insert("clientes", (String) null, contentValues);
        writableDatabase.close();
        limpiarCampos();
        borrarData();
        cargarRecycler();
        listaClientes();
        configurarAutcompletar();
        this.clientSaved++;
    }

    public void modificar() {
        SQLiteDatabase writableDatabase = new RegistroClientes(getContext(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.edtlocal.getText().toString();
        String obj2 = this.edtDireccion.getText().toString();
        String obj3 = this.edtCliente.getText().toString();
        String obj4 = this.edtTelefono.getText().toString();
        if (obj.isEmpty() || obj2.isEmpty() || obj3.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.toastCompletField), Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("direccion", obj2);
        contentValues.put("cliente", obj3);
        contentValues.put("telefono", obj4);
        int update = writableDatabase.update("clientes", contentValues, "local='" + obj + "'", (String[]) null);
        writableDatabase.close();
        limpiarCampos();
        if (update == 1) {
            borrarData();
            cargarRecycler();
            Toast.makeText(getContext(), getString(R.string.toastModifiedCorrec), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.toastClientnotexist), Toast.LENGTH_SHORT).show();
        }
        this.clientSaved++;
    }
}
