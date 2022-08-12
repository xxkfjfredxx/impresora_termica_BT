package com.example.manejoimpresora.FragmentsUI.Others;

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

import com.example.manejoimpresora.Adapters.AdaptadorProductos;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.DataBases.BaseDatosProductos;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.ProductosVo;
import java.util.ArrayList;

public class ProductsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AdaptadorProductos adapter;
    private Button butDelet;
    private Button butEdit;
    private Button butSave;
    private Button butSearh;
    /* access modifiers changed from: private */
    public EditText codigo;
    private String estado = "No";
    /* access modifiers changed from: private */
    public ArrayList<ProductosVo> listaProductos;
    private ArrayList<String> listaProductos2;
    private String moneda = "";
    private EditText nombre;
    private AutoCompleteTextView nombreSearch;
    private int prodSaved;
    /* access modifiers changed from: private */
    public RecyclerView recyclerProducts;
    /* access modifiers changed from: private */
    public String seleccion;
    private int totalProductos;
    private EditText valor;

    public static ProductsFragment newInstance(String str, String str2) {
        ProductsFragment productsFragment = new ProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        productsFragment.setArguments(bundle);
        return productsFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_products, viewGroup, false);
        this.codigo = (EditText) inflate.findViewById(R.id.editCodigo);
        this.nombre = (EditText) inflate.findViewById(R.id.editNombreSearch);
        this.valor = (EditText) inflate.findViewById(R.id.editValor);
        this.nombreSearch = (AutoCompleteTextView) inflate.findViewById(R.id.editNombre2);
        this.butSearh = (Button) inflate.findViewById(R.id.buttonSearh);
        this.butSave = (Button) inflate.findViewById(R.id.buttonSave);
        this.butEdit = (Button) inflate.findViewById(R.id.buttonEdit);
        this.butDelet = (Button) inflate.findViewById(R.id.buttonDelete);
        this.recyclerProducts = (RecyclerView) inflate.findViewById(R.id.recyclerProducts);
        this.listaProductos = new ArrayList<>();
        this.listaProductos2 = new ArrayList<>();
        this.recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 1));
        this.prodSaved = 0;
        this.butSearh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ProductsFragment.this.buscar();
            }
        });
        this.butSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ProductsFragment.this.registrar();
            }
        });
        this.butEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ProductsFragment.this.modificar();
            }
        });
        this.butDelet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ProductsFragment.this.eliminar();
            }
        });
        cargarSuscripcion();
        cargarMoneda();
        cargarRecycler();
        listaProductosAuto();
        configurarAutcompletarPro();
        return inflate;
    }

    private void listaProductosAuto() {
        this.listaProductos2 = new ArrayList<>();
        Cursor rawQuery = new BaseDatosProductos(getActivity(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select nombre from productos ", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                this.listaProductos2.add(rawQuery.getString(0));
            } while (rawQuery.moveToNext());
        }
    }

    public void configurarAutcompletarPro() {
        this.nombreSearch.setAdapter(new ArrayAdapter(getContext(), 17367050, this.listaProductos2));
        this.nombreSearch.setThreshold(1);
        this.nombreSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("beforeTextChanged", String.valueOf(charSequence));
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("onTextChanged", String.valueOf(charSequence));
            }

            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", String.valueOf(editable));
                ProductsFragment.this.cargarPrecio();
            }
        });
    }

    public void cargarPrecio() {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getActivity(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.nombreSearch.getText().toString();
        if (!obj.isEmpty()) {
            Cursor rawQuery = writableDatabase.rawQuery("select codigo, nombre, valor from productos where nombre ='" + obj + "'", (String[]) null);
            if (rawQuery.moveToFirst()) {
                this.codigo.setText(rawQuery.getString(0));
                this.nombre.setText(rawQuery.getString(1));
                this.valor.setText(rawQuery.getString(2));
                writableDatabase.close();
                this.nombreSearch.setText("");
                return;
            }
            return;
        }
        writableDatabase.close();
    }

    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
    }

    public void ContarRegistroProductos() {
        if (this.estado.equals("No")) {
            this.totalProductos = 0;
            SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
            Cursor rawQuery = writableDatabase.rawQuery("select codigo from productos", (String[]) null);
            if (rawQuery.moveToFirst()) {
                do {
                    this.totalProductos++;
                } while (rawQuery.moveToNext());
                writableDatabase.close();
            } else {
                writableDatabase.close();
            }
            if (this.totalProductos >= 18) {
                Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastMaximunProducts), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
                this.butSave.setEnabled(false);
                return;
            }
            this.butSave.setEnabled(true);
        }
    }

    public void limpiarCampos() {
        this.nombre.setText("");
        this.codigo.setText("");
        this.valor.setText("");
    }

    public void registrar() {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.codigo.getText().toString();
        String obj2 = this.nombre.getText().toString();
        String obj3 = this.valor.getText().toString();
        if (obj2.isEmpty() || obj.isEmpty() || obj3.isEmpty()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastCompletarCampos), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("codigo", obj);
        contentValues.put("nombre", obj2);
        contentValues.put("valor", obj3);
        writableDatabase.insert("productos", (String) null, contentValues);
        writableDatabase.close();
        limpiarCampos();
        borrarData();
        cargarRecycler();
        listaProductosAuto();
        configurarAutcompletarPro();
        ContarRegistroProductos();
        this.prodSaved++;
    }

    public void buscar() {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.codigo.getText().toString();
        if (!obj.isEmpty()) {
            Cursor rawQuery = writableDatabase.rawQuery("select codigo, nombre, valor from productos where codigo ='" + obj + "'", (String[]) null);
            if (rawQuery.moveToFirst()) {
                this.valor.setText(rawQuery.getString(2));
                this.codigo.setText(rawQuery.getString(0));
                this.nombre.setText(rawQuery.getString(1));
                writableDatabase.close();
                return;
            }
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastNoExist), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            writableDatabase.close();
            return;
        }
        Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastNeedCode), Toast.LENGTH_SHORT);
        makeText2.setGravity(17, 0, 0);
        makeText2.show();
    }

    public void eliminar() {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.codigo.getText().toString();
        if (!obj.isEmpty()) {
            int delete = writableDatabase.delete("productos", "codigo ='" + obj + "'", (String[]) null);
            writableDatabase.close();
            limpiarCampos();
            if (delete == 1) {
                borrarData();
                cargarRecycler();
                Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastProductDeleted), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
                ContarRegistroProductos();
                return;
            }
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastNoExist), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
            return;
        }
        Toast makeText3 = Toast.makeText(getContext(), getString(R.string.ToastNeedCode), Toast.LENGTH_SHORT);
        makeText3.setGravity(17, 0, 0);
        makeText3.show();
    }

    public void modificar() {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.codigo.getText().toString();
        String obj2 = this.nombre.getText().toString();
        String obj3 = this.valor.getText().toString();
        if (obj.isEmpty() || obj2.isEmpty() || obj3.isEmpty()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastCompletarCampos), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("codigo", obj);
        contentValues.put("nombre", obj2);
        contentValues.put("valor", obj3);
        int update = writableDatabase.update("productos", contentValues, "codigo='" + obj + "'", (String[]) null);
        writableDatabase.close();
        limpiarCampos();
        if (update == 1) {
            borrarData();
            cargarRecycler();
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastEditadoCorr), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
            return;
        }
        Toast makeText3 = Toast.makeText(getContext(), getString(R.string.ToastNoExist), Toast.LENGTH_SHORT);
        makeText3.setGravity(17, 0, 0);
        makeText3.show();
    }

    public void cargarMoneda() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getActivity(), "registroConfiguraciones", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select otra from configuraciones where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            String string = rawQuery.getString(0);
            this.moneda = string;
            if (string == null) {
                this.moneda = "";
            }
            writableDatabase.close();
            return;
        }
        this.moneda = "";
    }

    public void cargarRecycler() {
        Cursor rawQuery = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select codigo,nombre,valor from productos", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                ArrayList<ProductosVo> arrayList = this.listaProductos;
                String string = rawQuery.getString(0);
                String string2 = rawQuery.getString(1);
                arrayList.add(new ProductosVo(string, string2, this.moneda + " " + rawQuery.getString(2)));
            } while (rawQuery.moveToNext());
        }
        AdaptadorProductos adaptadorProductos = new AdaptadorProductos(this.listaProductos);
        this.adapter = adaptadorProductos;
        adaptadorProductos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ProductsFragment productsFragment = ProductsFragment.this;
                String unused = productsFragment.seleccion = ((ProductosVo) productsFragment.listaProductos.get(ProductsFragment.this.recyclerProducts.getChildAdapterPosition(view))).getCodigo();
                ProductsFragment.this.limpiarCampos();
                ProductsFragment.this.codigo.setText(ProductsFragment.this.seleccion);
                ProductsFragment.this.buscar();
            }
        });
        this.recyclerProducts.setAdapter(this.adapter);
    }

    public void borrarData() {
        this.listaProductos.clear();
        this.adapter.notifyDataSetChanged();
    }
}
