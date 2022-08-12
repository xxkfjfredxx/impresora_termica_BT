package com.example.manejoimpresora.FragmentsUI.Others;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.manejoimpresora.Adapters.AdaptadorReportes;
import com.example.manejoimpresora.DataBases.BaseDatosBills;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones2;
import com.example.manejoimpresora.DataBases.BaseDatosProductos;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.DataBases.BaseDeDatosVentas;
import com.example.manejoimpresora.DataBases.RegistroClientes;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Impresion;
import com.example.manejoimpresora.Utilities.ReportesVo;

public class ReportsFragment extends Fragment {
    private String BUsinessName;
    /* access modifiers changed from: private */
    public String NomImpresora;
    private int ano;
    private Button buttonDate;
    private Button buttonPrint;
    private Button buttonSearh;
    public Bitmap currency;
    private int dia;
    private AutoCompleteTextView edtCliente;
    /* access modifiers changed from: private */
    public TextView edtFecha;
    private String estado = "No";
    private String formatoFecha;
    private TextView instruciones;
    private ArrayList<String> listaClientes;
    private ArrayList<String> listaProductos;
    ArrayList<ReportesVo> listaVen = new ArrayList<>();
    ArrayList<ReportesVo> listaVen2 = new ArrayList<>();
    private RecyclerView listaVentas;
    public Bitmap logoMoneda;
    private int mes;
    private String moneda;
    /* access modifiers changed from: private */
    public int printed;
    private ReportsViewModel reportsViewModel;
    private TextView signo;
    private String signoMoneda;
    private double total;
    /* access modifiers changed from: private */
    public TextView totalVenta;

    static /* synthetic */ int access$008(ReportsFragment reportsFragment) {
        int i = reportsFragment.printed;
        reportsFragment.printed = i + 1;
        return i;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_reports, viewGroup, false);
        this.edtFecha = (TextView) inflate.findViewById(R.id.editTextDate);
        this.listaVentas = (RecyclerView) inflate.findViewById(R.id.ListViewVentas);
        this.totalVenta = (TextView) inflate.findViewById(R.id.totalVenta);
        this.signo = (TextView) inflate.findViewById(R.id.textView8);
        this.buttonDate = (Button) inflate.findViewById(R.id.buttonDate);
        this.buttonSearh = (Button) inflate.findViewById(R.id.buttonSearh);
        this.buttonPrint = (Button) inflate.findViewById(R.id.printReport);
        this.instruciones = (TextView) inflate.findViewById(R.id.textView7);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) inflate.findViewById(R.id.editTextClientName);
        this.edtCliente = autoCompleteTextView;
        autoCompleteTextView.setText("");
        this.printed = 0;
        this.buttonDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportsFragment.this.cargarFecha();
            }
        });
        this.buttonSearh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReportsFragment.access$008(ReportsFragment.this);
                ReportsFragment.this.listaVen.clear();
                ReportsFragment.this.listaVen2.clear();
                ReportsFragment.this.cargarListaVentas2();
            }
        });
        this.buttonPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ReportsFragment.this.NomImpresora.equals("")) {
                    Toast makeText = Toast.makeText(ReportsFragment.this.getContext(), ReportsFragment.this.getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
                    makeText.setGravity(17, 0, 0);
                    makeText.show();
                } else if (ReportsFragment.this.NomImpresora.contains("...")) {
                    Toast makeText2 = Toast.makeText(ReportsFragment.this.getContext(), ReportsFragment.this.getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
                    makeText2.setGravity(17, 0, 0);
                    makeText2.show();
                } else if (!ReportsFragment.this.totalVenta.getText().toString().equals("")) {
                    try {
                        ReportsFragment.this.imprimirReporte();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        cargarSuscripcion();
        listaClientes();
        listaProductos();
        configurarAutcompletar();
        CargarBussinesName();
        selectMoneda();
        cargarDatosBill();
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
        if (verificarBluetooth()) {
            Impresion impresion = new Impresion();
            boolean conectar = impresion.conectar(this.NomImpresora);
            int size = this.listaVen2.size();
            String charSequence = this.totalVenta.getText().toString();
            String imprimirTotal = imprimirTotal();
            String charSequence2 = this.edtFecha.getText().toString();
            String string = getString(R.string.Qty);
            String string2 = getString(R.string.tPrice);
            String obj = this.edtCliente.getText().toString();
            if (conectar) {
                impresion.printCustom(this.BUsinessName, 2, 1);
                impresion.printNewLine();
                impresion.printCustom(getString(R.string.printReport), 2, 1);
                if (!obj.isEmpty()) {
                    impresion.printCustom(getString(R.string.Client) + obj, 0, 1);
                }
                impresion.printNewLine();
                impresion.printCustom(charSequence2, 1, 1);
                impresion.printCustom("dd-mm-aaaa", 1, 1);
                impresion.printNewLine();
                impresion.printCustom(String.format(" %1$12s %2$12s", new Object[]{string, string2}), 0, 2);
                impresion.printCustom("-------------------------------", 0, 1);
                for (int i = 0; i < size; i++) {
                    String producto = this.listaVen2.get(i).getProducto();
                    String cantidad = this.listaVen2.get(i).getCantidad();
                    String precio = this.listaVen2.get(i).getPrecio();
                    impresion.printCustom(producto, 1, 0);
                    impresion.printCustom(String.format(" %1$12s %2$12s", new Object[]{cantidad, precio}), 0, 2);
                }
                impresion.printCustom("-------------------------------", 0, 1);
                if (imprimirTotal.equals("si")) {
                    try {
                        impresion.imprimirLeft();
                        impresion.printPhotoPrueba(this.logoMoneda);
                        impresion.printNewLine();
                        impresion.printCustom(charSequence, 2, 2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    impresion.printCustom("TOTAL ", 2, 0);
                    impresion.printNewLine();
                    impresion.printCustom(this.moneda + charSequence, 2, 2);
                }
                impresion.printNewLine();
                impresion.printNewLine();
                impresion.desconectarImpresora();
                return;
            }
            Toast.makeText(getContext(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast makeText = Toast.makeText(getContext(), getString(R.string.turnBluetooth2), Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public String imprimirTotal() {
        if (!this.moneda.equals("$") && !this.moneda.contains("..") && !this.moneda.equals("")) {
            return "si";
        }
        if (this.moneda.equals("$")) {
            return "$";
        }
        if (!this.moneda.contains("..")) {
            boolean equals = this.moneda.equals("");
        }
        return "";
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

    public void CargarBussinesName() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getActivity(), "registroConfiguraciones", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select nombreImpresora, otra from configuraciones where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.BUsinessName = rawQuery.getString(0);
            this.moneda = rawQuery.getString(1);
            if (this.BUsinessName == null) {
                this.BUsinessName = "";
            }
            String str = this.moneda;
            if (str == null || str.contains("..")) {
                this.moneda = "";
            }
            writableDatabase.close();
            return;
        }
        this.BUsinessName = "";
        this.moneda = "";
    }

    private void listaClientes() {
        this.listaClientes = new ArrayList<>();
        Cursor rawQuery = new RegistroClientes(getActivity(), "administracion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase().rawQuery("select local from clientes ", (String[]) null);
        if (rawQuery.moveToFirst()) {
            do {
                this.listaClientes.add(rawQuery.getString(0));
            } while (rawQuery.moveToNext());
        }
    }

    public void configurarAutcompletar() {
        this.edtCliente.setAdapter(new ArrayAdapter(getContext(), 17367050, this.listaClientes));
        this.edtCliente.setThreshold(1);
        this.edtCliente.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("beforeTextChanged", String.valueOf(charSequence));
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("onTextChanged", String.valueOf(charSequence));
            }

            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", String.valueOf(editable));
            }
        });
    }

    public void cargarFecha() {
        Calendar instance = Calendar.getInstance();
        this.dia = instance.get(5);
        this.mes = instance.get(2);
        this.ano = instance.get(1);
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                if (i3 > 9 && i2 > 8) {
                    TextView access$300 = ReportsFragment.this.edtFecha;
                    access$300.setText(i3 + "-" + (i2 + 1) + "-" + i);
                } else if (i3 < 10 && i2 > 8) {
                    TextView access$3002 = ReportsFragment.this.edtFecha;
                    access$3002.setText("0" + i3 + "-" + (i2 + 1) + "-" + i);
                } else if (i3 > 9 && i2 < 9) {
                    TextView access$3003 = ReportsFragment.this.edtFecha;
                    access$3003.setText(i3 + "-0" + (i2 + 1) + "-" + i);
                } else if (i3 < 10 && i2 < 9) {
                    TextView access$3004 = ReportsFragment.this.edtFecha;
                    access$3004.setText("0" + i3 + "-0" + (i2 + 1) + "-" + i);
                }
            }
        }, this.ano, this.mes, this.dia).show();
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

    public void cargarListaVentas2() {
        Cursor cursor;
        int size = this.listaProductos.size();
        String charSequence = this.edtFecha.getText().toString();
        String obj = this.edtCliente.getText().toString();
        this.total = 0.0d;
        int i = 1;
        this.listaVentas.setLayoutManager(new GridLayoutManager(getContext(), 1));
        String[] strArr = null;
        SQLiteDatabase writableDatabase = new BaseDeDatosVentas(getContext(), "RegistroVentas", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        int i2 = 0;
        double d = 0.0d;
        int i3 = 0;
        while (i3 < size) {
            String str = this.listaProductos.get(i3);
            if (obj.isEmpty()) {
                cursor = writableDatabase.rawQuery("select cantidad, total from Ventas where fecha ='" + charSequence + "' AND producto ='" + str + "'", strArr);
            } else {
                cursor = writableDatabase.rawQuery("select cantidad, total from Ventas where fecha ='" + charSequence + "' AND producto ='" + str + "'AND cliente ='" + obj + "'", strArr);
            }
            if (cursor.moveToFirst()) {
                double d2 = 0.0d;
                double d3 = 0.0d;
                do {
                    d2 += Double.valueOf(Double.parseDouble(cursor.getString(i2))).doubleValue();
                    d3 += Double.valueOf(Double.parseDouble(cursor.getString(i))).doubleValue();
                } while (cursor.moveToNext());
                this.listaVen.add(new ReportesVo(str, Double.toString(d2), this.moneda + Double.toString(d3)));
                this.listaVen2.add(new ReportesVo(str, Double.toString(d2), Double.toString(d3)));
                d += d3;
            }
            i3++;
            i2 = 0;
            i = 1;
            strArr = null;
        }
        this.listaVentas.setAdapter(new AdaptadorReportes(this.listaVen));
        String format = String.format("%.2f", new Object[]{Double.valueOf(d)});
        this.signo.setText(this.moneda);
        this.totalVenta.setText(format.replace(',', '.'));
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void selectMoneda() {
        /*
            r5 = this;
            java.lang.String r0 = r5.moneda
            int r1 = r0.hashCode()
            r2 = 1
            switch(r1) {
                case 75: goto L_0x00c7;
                case 163: goto L_0x00bd;
                case 165: goto L_0x00b3;
                case 2051: goto L_0x00a9;
                case 2113: goto L_0x009f;
                case 2284: goto L_0x0095;
                case 2439: goto L_0x008a;
                case 2578: goto L_0x007f;
                case 2654: goto L_0x0074;
                case 2655: goto L_0x0069;
                case 2657: goto L_0x005d;
                case 4104: goto L_0x0051;
                case 8358: goto L_0x0045;
                case 8364: goto L_0x003a;
                case 8369: goto L_0x002f;
                case 8377: goto L_0x0023;
                case 8381: goto L_0x0018;
                case 71553: goto L_0x000c;
                default: goto L_0x000a;
            }
        L_0x000a:
            goto L_0x00d2
        L_0x000c:
            java.lang.String r1 = "HK$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 8
            goto L_0x00d3
        L_0x0018:
            java.lang.String r1 = "₽"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 4
            goto L_0x00d3
        L_0x0023:
            java.lang.String r1 = "₹"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 12
            goto L_0x00d3
        L_0x002f:
            java.lang.String r1 = "₱"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 1
            goto L_0x00d3
        L_0x003a:
            java.lang.String r1 = "€"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 0
            goto L_0x00d3
        L_0x0045:
            java.lang.String r1 = "₦"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 11
            goto L_0x00d3
        L_0x0051:
            java.lang.String r1 = "zł"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 15
            goto L_0x00d3
        L_0x005d:
            java.lang.String r1 = "Rs"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 13
            goto L_0x00d3
        L_0x0069:
            java.lang.String r1 = "SR"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 14
            goto L_0x00d3
        L_0x0074:
            java.lang.String r1 = "Rp"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 9
            goto L_0x00d3
        L_0x007f:
            java.lang.String r1 = "R$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 10
            goto L_0x00d3
        L_0x008a:
            java.lang.String r1 = "Kr"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 17
            goto L_0x00d3
        L_0x0095:
            java.lang.String r1 = "Fr"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 6
            goto L_0x00d3
        L_0x009f:
            java.lang.String r1 = "C$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 7
            goto L_0x00d3
        L_0x00a9:
            java.lang.String r1 = "A$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 5
            goto L_0x00d3
        L_0x00b3:
            java.lang.String r1 = "¥"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 2
            goto L_0x00d3
        L_0x00bd:
            java.lang.String r1 = "£"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 3
            goto L_0x00d3
        L_0x00c7:
            java.lang.String r1 = "K"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d2
            r0 = 16
            goto L_0x00d3
        L_0x00d2:
            r0 = -1
        L_0x00d3:
            r1 = 30
            r3 = 170(0xaa, float:2.38E-43)
            switch(r0) {
                case 0: goto L_0x0253;
                case 1: goto L_0x023f;
                case 2: goto L_0x022b;
                case 3: goto L_0x0217;
                case 4: goto L_0x0203;
                case 5: goto L_0x01ee;
                case 6: goto L_0x01d9;
                case 7: goto L_0x01c4;
                case 8: goto L_0x01af;
                case 9: goto L_0x019a;
                case 10: goto L_0x0185;
                case 11: goto L_0x0170;
                case 12: goto L_0x015b;
                case 13: goto L_0x0146;
                case 14: goto L_0x0131;
                case 15: goto L_0x011c;
                case 16: goto L_0x0107;
                case 17: goto L_0x00f2;
                default: goto L_0x00da;
            }
        L_0x00da:
            java.lang.String r0 = r5.moneda
            java.lang.String r1 = "$"
            boolean r0 = r0.equals(r1)
            java.lang.String r2 = ""
            java.lang.String r3 = ".."
            if (r0 != 0) goto L_0x026a
            java.lang.String r0 = r5.moneda
            boolean r0 = r0.contains(r3)
            if (r0 == 0) goto L_0x0267
            goto L_0x026a
        L_0x00f2:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230854(0x7f080086, float:1.8077773E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0107:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230906(0x7f0800ba, float:1.8077878E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x011c:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230956(0x7f0800ec, float:1.807798E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0131:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230941(0x7f0800dd, float:1.8077949E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0146:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230945(0x7f0800e1, float:1.8077957E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x015b:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230943(0x7f0800df, float:1.8077953E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0170:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230916(0x7f0800c4, float:1.8077898E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0185:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230939(0x7f0800db, float:1.8077945E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x019a:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230944(0x7f0800e0, float:1.8077955E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x01af:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230864(0x7f080090, float:1.8077793E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x01c4:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230862(0x7f08008e, float:1.8077789E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x01d9:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230877(0x7f08009d, float:1.807782E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x01ee:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230861(0x7f08008d, float:1.8077787E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0203:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230942(0x7f0800de, float:1.807795E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0217:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230868(0x7f080094, float:1.80778E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x022b:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230955(0x7f0800eb, float:1.8077977E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x023f:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230934(0x7f0800d6, float:1.8077935E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0253:
            android.content.res.Resources r0 = r5.getResources()
            r4 = 2131230869(0x7f080095, float:1.8077803E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r4)
            r5.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r3, r1, r2)
            r5.logoMoneda = r0
            goto L_0x027f
        L_0x0267:
            r5.signoMoneda = r2
            goto L_0x027f
        L_0x026a:
            java.lang.String r0 = r5.moneda
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0275
            r5.signoMoneda = r1
            goto L_0x027f
        L_0x0275:
            java.lang.String r0 = r5.moneda
            boolean r0 = r0.contains(r3)
            if (r0 == 0) goto L_0x027f
            r5.signoMoneda = r2
        L_0x027f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: apps.james1.Probadorimpresorabluetooth.FragmentsUI.Others.ReportsFragment.selectMoneda():void");
    }

    public void cargarDatosBill() {
        SQLiteDatabase writableDatabase = new BaseDatosBills(getActivity(), "registro", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select formatofecha, direccion, telefono, taxes, mensaje1, mensaje2 from datosbill where numero ='1'", (String[]) null);
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
}
