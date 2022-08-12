package com.example.manejoimpresora.FragmentsUI.Others;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.Adapters.AdapteDatos2;
import com.example.manejoimpresora.DataBases.BaseDatosBills;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones2;
import com.example.manejoimpresora.DataBases.BaseDatosProductos;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Impresion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class fragmentInventario extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String BUsinessName;
    /* access modifiers changed from: private */
    public String NomImpresora;
    /* access modifiers changed from: private */
    public AutoCompleteTextView SearchPro;
    /* access modifiers changed from: private */
    public EditText cantidad;
    private Boolean carga = false;
    private String estado = "No";
    private String formatoFecha;
    private ImageButton guardar;
    private ArrayList<String> lista = new ArrayList<>();
    private ArrayList<String> listaProductos;
    private Button print;
    private int printed;
    private RecyclerView recycler;
    private Button resetInv;
    private ImageButton restar;
    private ImageButton search;
    private Spinner spinnerProductos;

    public static fragmentInventario newInstance(String str, String str2) {
        fragmentInventario fragmentinventario = new fragmentInventario();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        fragmentinventario.setArguments(bundle);
        return fragmentinventario;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_inventario, viewGroup, false);
        this.cantidad = (EditText) inflate.findViewById(R.id.edtCantidad);
        this.guardar = (ImageButton) inflate.findViewById(R.id.buttSave);
        this.print = (Button) inflate.findViewById(R.id.butPrint2);
        this.restar = (ImageButton) inflate.findViewById(R.id.buttSave2);
        this.search = (ImageButton) inflate.findViewById(R.id.btnSearch);
        this.resetInv = (Button) inflate.findViewById(R.id.butRest);
        this.recycler = (RecyclerView) inflate.findViewById(R.id.recyclerInv);
        this.SearchPro = (AutoCompleteTextView) inflate.findViewById(R.id.txtProd);
        this.spinnerProductos = (Spinner) inflate.findViewById(R.id.spinner);
        this.printed = 0;
        this.guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String obj = fragmentInventario.this.cantidad.getText().toString();
                if (!obj.isEmpty()) {
                    fragmentInventario.this.ModificarCantidad(Integer.parseInt(obj));
                    return;
                }
                Toast makeText = Toast.makeText(fragmentInventario.this.getContext(), fragmentInventario.this.getString(R.string.ToastSelec), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
            }
        });
        this.restar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String obj = fragmentInventario.this.cantidad.getText().toString();
                if (!obj.isEmpty()) {
                    fragmentInventario.this.ModificarCantidad(Integer.parseInt(obj) * -1);
                    return;
                }
                Toast makeText = Toast.makeText(fragmentInventario.this.getContext(), fragmentInventario.this.getString(R.string.ToastSelec), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
            }
        });
        this.search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fragmentInventario.this.cargarLista2();
            }
        });
        this.resetInv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fragmentInventario.this.resetInventario();
            }
        });
        this.print.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (fragmentInventario.this.NomImpresora.equals("")) {
                    Toast makeText = Toast.makeText(fragmentInventario.this.getContext(), fragmentInventario.this.getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
                    makeText.setGravity(17, 0, 0);
                    makeText.show();
                } else if (!fragmentInventario.this.NomImpresora.contains("...")) {
                    try {
                        fragmentInventario.this.imprimirReporte();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast makeText2 = Toast.makeText(fragmentInventario.this.getContext(), fragmentInventario.this.getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
                    makeText2.setGravity(17, 0, 0);
                    makeText2.show();
                }
            }
        });
        cargarSuscripcion();
        cargarSpinner();
        cargarLista();
        listaProductos();
        configurarAutcompletarPro();
        cargarDatosBill();
        CargarBussinesName();
        CargarPrinterName();
        encenderBluetooth();
        return inflate;
    }

    public boolean verificarBluetooth() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            return true;
        }
        encenderBluetooth();
        return false;
    }

    public void encenderBluetooth() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
    }

    public void imprimirReporte() throws IOException {
        String str;
        if (verificarBluetooth()) {
            Impresion impresion = new Impresion();
            boolean booleanValue = impresion.conectarImpresora(this.NomImpresora).booleanValue();
            int size = this.lista.size() / 2;
            String string = getString(R.string.Product_name);
            String string2 = getString(R.string.Qty);
            if (this.formatoFecha.isEmpty() || this.formatoFecha.equals("24-01-2021")) {
                str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            } else {
                str = "";
            }
            if (this.formatoFecha.equals("01-24-2021")) {
                str = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
            }
            if (booleanValue) {
                impresion.printCustom(this.BUsinessName, 2, 1);
                impresion.printNewLine();
                impresion.printCustom(getString(R.string.Inventory), 2, 1);
                impresion.printNewLine();
                impresion.printCustom(str, 1, 1);
                impresion.printNewLine();
                impresion.printCustom(String.format(" %1$2s %2$12s", new Object[]{string, string2}), 0, 2);
                impresion.printCustom("-------------------------------", 0, 1);
                int i = 0;
                for (int i2 = 0; i2 < size; i2++) {
                    impresion.printCustom(this.lista.get(i), 1, 0);
                    impresion.printCustom(this.lista.get(i + 1), 0, 2);
                    i += 2;
                }
                impresion.printCustom("-------------------------------", 0, 1);
                impresion.printNewLine();
                impresion.printNewLine();
                impresion.desconectarImpresora();
                this.printed++;
                return;
            }
            Toast.makeText(getContext(), getString(R.string.ToastTurnOnPrinter), 0).show();
            return;
        }
        Toast makeText = Toast.makeText(getContext(), getString(R.string.turnBluetooth2), 0);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public void cargarDatosBill() {
        SQLiteDatabase writableDatabase = new BaseDatosBills(getActivity(), "registro", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select formatofecha from datosbill where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.formatoFecha = rawQuery.getString(0);
            writableDatabase.close();
            return;
        }
        crearConfiguracionBaseBill();
        cargarDatosBill();
    }

    public void crearConfiguracionBaseBill() {
        SQLiteDatabase writableDatabase = new BaseDatosBills(getContext(), "registro", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        contentValues.put("formatofecha", "");
        contentValues.put("direccion", "");
        contentValues.put("telefono", "");
        contentValues.put("taxes", "");
        contentValues.put("mensaje1", "");
        contentValues.put("mensaje2", "");
        writableDatabase.insert("datosbill", (String) null, contentValues);
        writableDatabase.close();
    }

    public void CargarBussinesName() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getActivity(), "registroConfiguraciones", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select nombreImpresora from configuraciones where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            String string = rawQuery.getString(0);
            this.BUsinessName = string;
            if (string == null) {
                this.BUsinessName = "";
            }
            writableDatabase.close();
            return;
        }
        this.BUsinessName = "";
    }

    public void CargarPrinterName() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones2(getActivity(), "registroConfiguraciones2", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select printerName from configuraciones where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.NomImpresora = rawQuery.getString(0);
            writableDatabase.close();
            if (this.NomImpresora == null) {
                this.NomImpresora = "";
                return;
            }
            return;
        }
        this.NomImpresora = "";
    }


    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
    }

    public void configurarAutcompletarPro() {
        this.SearchPro.setAdapter(new ArrayAdapter(getContext(), 17367050, this.listaProductos));
        this.SearchPro.setThreshold(1);
        this.SearchPro.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("beforeTextChanged", String.valueOf(charSequence));
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("onTextChanged", String.valueOf(charSequence));
            }

            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", String.valueOf(editable));
                fragmentInventario.this.cargarLista2();
            }
        });
    }

    private void listaProductos() {
        this.listaProductos = new ArrayList<>();
        Cursor rawQuery = new BaseDatosProductos(getActivity(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select nombre from productos ", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                this.listaProductos.add(rawQuery.getString(0));
            } while (rawQuery.moveToNext());
        }
    }

    /* access modifiers changed from: private */
    public void cargarLista() {
        this.lista.clear();
        this.recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        Cursor rawQuery = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select nombre,cantidad from productos", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                this.lista.add(rawQuery.getString(0));
                ArrayList<String> arrayList = this.lista;
                arrayList.add("       " + rawQuery.getString(1));
            } while (rawQuery.moveToNext());
        }
        this.recycler.setAdapter(new AdapteDatos2(this.lista));
    }

    /* access modifiers changed from: private */
    public void cargarLista2() {
        this.lista.clear();
        String obj = this.SearchPro.getText().toString();
        if (!obj.isEmpty()) {
            this.recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
            ArrayList arrayList = new ArrayList();
            SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
            Cursor rawQuery = writableDatabase.rawQuery("select nombre,cantidad from productos  where nombre ='" + obj + "'", (String[]) null);
            if (rawQuery.moveToFirst()) {
                do {
                    arrayList.add(rawQuery.getString(0));
                    arrayList.add("       " + rawQuery.getString(1));
                } while (rawQuery.moveToNext());
            }
            this.recycler.setAdapter(new AdapteDatos2(arrayList));
            if (!this.carga.booleanValue()) {
                this.carga = true;
            }
        }
    }

    private void cargarSpinner() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = new BaseDatosProductos(getActivity(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select nombre from productos ", (String[]) null);
        arrayList.add(getString(R.string.SelectProduct));
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(rawQuery.getString(0));
            } while (rawQuery.moveToNext());
        }
        this.spinnerProductos.setAdapter(new ArrayAdapter(getActivity(), 17367043, arrayList));
        this.spinnerProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!adapterView.getItemAtPosition(i).toString().contains("...")) {
                    fragmentInventario.this.SearchPro.setText(adapterView.getItemAtPosition(i).toString());
                }
            }
        });
    }

    public void ModificarCantidad(int i) {
        String obj = this.SearchPro.getText().toString();
        if (this.SearchPro.length() <= 0 || this.cantidad.length() <= 0 || obj.contains("...")) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastSelec), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            return;
        }
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select codigo, nombre, valor, cantidad from productos where nombre ='" + this.SearchPro.getText().toString() + "'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            String string = rawQuery.getString(0);
            String string2 = rawQuery.getString(1);
            String string3 = rawQuery.getString(2);
            String string4 = rawQuery.getString(3);
            if (string4 == null) {
                string4 = "0";
            }
            String num = Integer.toString(Integer.parseInt(string4) + i);
            ContentValues contentValues = new ContentValues();
            contentValues.put("codigo", string);
            contentValues.put("nombre", string2);
            contentValues.put("valor", string3);
            contentValues.put("cantidad", num);
            writableDatabase.update("productos", contentValues, "codigo='" + string + "'", (String[]) null);
            writableDatabase.close();
            this.printed++;
        } else {
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.wrongName), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
        }
        cargarLista2();
        cargarSpinner();
        this.SearchPro.setText("");
        this.cantidad.setText("");
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                fragmentInventario.this.cargarLista();
            }
        }, 2000);
    }

    public void resetInventario() {
        new AlertDialog.Builder(getContext()).setTitle((CharSequence) "").setMessage((CharSequence) getString(R.string.ResetInven)).setPositiveButton(17039379, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                fragmentInventario.this.InventariCero();
            }
        }).setNegativeButton(17039369, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();
    }

    public void InventariCero() {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select codigo, nombre, valor, cantidad from productos ", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                String string = rawQuery.getString(0);
                String string2 = rawQuery.getString(1);
                String string3 = rawQuery.getString(2);
                String string4 = rawQuery.getString(3);
                ContentValues contentValues = new ContentValues();
                contentValues.put("codigo", string);
                contentValues.put("nombre", string2);
                contentValues.put("valor", string3);
                contentValues.put("cantidad", "0");
                writableDatabase.update("productos", contentValues, "codigo='" + string + "'", (String[]) null);
            } while (rawQuery.moveToNext());
            writableDatabase.close();
            cargarLista();
            this.printed++;
        }
    }
}
