package com.example.manejoimpresora.FragmentsUI.PrintBill;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.FragmentsUI.PrintBill.PrintBillViewModel;
import com.example.manejoimpresora.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.example.manejoimpresora.Adapters.AdaptadorVenta;
import com.example.manejoimpresora.DataBases.BaseDatosBills;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones2;
import com.example.manejoimpresora.DataBases.BaseDatosProductos;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.DataBases.BaseDeDatosVentas;
import com.example.manejoimpresora.DataBases.RegistroClientes;
import com.example.manejoimpresora.Utilities.PrinterCommands;
import com.example.manejoimpresora.Utilities.Utils;
import com.example.manejoimpresora.Utilities.VentaVo;
import com.example.manejoimpresora.Utilities.comprobarInternet;

public class PrintBill extends Fragment {
    /* access modifiers changed from: private */
    public FloatingActionButton Add;
    private String BUsinessName;
    /* access modifiers changed from: private */
    public EditText Cantidad;
    private TextView Fecha;
    /* access modifiers changed from: private */
    public String NomImpresora;
    /* access modifiers changed from: private */
    public AutoCompleteTextView NomPro;
    /* access modifiers changed from: private */
    public Button Print;
    /* access modifiers changed from: private */
    public TextView Resultado;
    /* access modifiers changed from: private */
    public TextView TotProdu;
    private EditText ValPro;
    private int a = 0;
    /* access modifiers changed from: private */
    public AdaptadorVenta adapter;
    private int b = 0;
    private Button bLink;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    /* access modifiers changed from: private */
    public Button butBack;
    /* access modifiers changed from: private */
    public Button butNext;
    private Button butProducts;
    /* access modifiers changed from: private */
    public Button butSave;
    private Bitmap currency;
    private int d = 0;
    private String direccion;
    private Boolean e;
    private AutoCompleteTextView edtCliente;
    private String estado = "No";
    private FloatingActionButton fab;
    private FloatingActionButton fabClients;
    private FloatingActionButton fabInstruction;
    private FloatingActionButton fabInventory;
    private FloatingActionButton fabReports;
    private FloatingActionButton fabReset;
    private PrintBillViewModel facturacionViewModel;
    private String fecha4;
    private String formatoFecha;
    private Animation fromBotton;
    /* access modifiers changed from: private */
    public Boolean impresionOk;
    private InputStream inputStream;
    private Boolean isOpen = false;
    /* access modifiers changed from: private */
    public LinearLayout layout;
    /* access modifiers changed from: private */
    public LinearLayout layout2;
    public ArrayList<VentaVo> listDatosMostrar;
    private ArrayList<String> listaClientes;
    private ArrayList<String> listaProductos;
    private Bitmap logoEmpresa;
    private Bitmap logoMoneda;
    private double m = 0.0d;
    private String mensaje1;
    private String mensaje2;
    private String moneda;
    /* access modifiers changed from: private */
    public Boolean mostrado;
    private TextView nFacturas;
    /* access modifiers changed from: private */
    public Boolean natShow;
    private Boolean negativo;
    private Boolean noCurrency;
    private String number;
    private OutputStream outputStream;
    private Boolean p;
    /* access modifiers changed from: private */
    public RecyclerView recycler;
    private Animation rotateClose;
    private Animation rotateOpen;
    private Boolean saved;
    private TextView sign;
    private String signoMoneda;
    private Spinner spinnerProductos;
    volatile boolean stopWorker;
    private String taxes;
    private String telefono;
    private Animation toBottom;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
        ((ActionBar) Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())).show();
        this.Fecha = (TextView) inflate.findViewById(R.id.txtFecha);
        this.NomPro = (AutoCompleteTextView) inflate.findViewById(R.id.nPro);
        this.ValPro = (EditText) inflate.findViewById(R.id.Valpro);
        this.Cantidad = (EditText) inflate.findViewById(R.id.Cantidad);
        this.TotProdu = (TextView) inflate.findViewById(R.id.totalPro);
        this.Resultado = (TextView) inflate.findViewById(R.id.txtResultado);
        this.recycler = (RecyclerView) inflate.findViewById(R.id.recyclerId);
        this.spinnerProductos = (Spinner) inflate.findViewById(R.id.spinnerProductos);
        this.nFacturas = (TextView) inflate.findViewById(R.id.txtNumero);
        this.edtCliente = (AutoCompleteTextView) inflate.findViewById(R.id.editTextClientName);
        this.sign = (TextView) inflate.findViewById(R.id.textView6);
        this.fab = (FloatingActionButton) inflate.findViewById(R.id.floatingActionButton);
        this.fabClients = (FloatingActionButton) inflate.findViewById(R.id.floatingClients);
        this.fabInventory = (FloatingActionButton) inflate.findViewById(R.id.floatingInventory);
        this.fabReports = (FloatingActionButton) inflate.findViewById(R.id.floatingReports);
        this.fabInstruction = (FloatingActionButton) inflate.findViewById(R.id.floatingInstruction);
        this.fabReset = (FloatingActionButton) inflate.findViewById(R.id.floatingReset);
        this.fromBotton = AnimationUtils.loadAnimation(getContext(), R.anim.from_botton_anim);
        this.toBottom = AnimationUtils.loadAnimation(getContext(), R.anim.to_bottom_anim);
        this.rotateClose = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim);
        this.rotateOpen = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);

        this.layout = (LinearLayout) inflate.findViewById(R.id.linearLayout3);
        this.layout2 = (LinearLayout) inflate.findViewById(R.id.linearLayout12);

        this.Cantidad.setText("");
        this.ValPro.setText("");
        this.signoMoneda = "";
        this.noCurrency = false;
        this.negativo = false;
        this.impresionOk = false;
        this.mostrado = false;
        this.Add = (FloatingActionButton) inflate.findViewById(R.id.ButaddVenta);
        this.Print = (Button) inflate.findViewById(R.id.butPrint);
        this.butProducts = (Button) inflate.findViewById(R.id.buttonProducts);
        this.butSave = (Button) inflate.findViewById(R.id.Sell);
        this.bLink = (Button) inflate.findViewById(R.id.buttonLink);
        this.butNext = (Button) inflate.findViewById(R.id.butNext);
        this.butBack = (Button) inflate.findViewById(R.id.butBack);
        this.listDatosMostrar = new ArrayList<>();
        this.Resultado.setText("0");
        this.p = false;
        this.saved = false;
        this.natShow = false;
        this.e = false;
        this.Fecha.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        this.fecha4 = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        cargarSuscripcion();
        this.nFacturas.setText(getActivity().getSharedPreferences("NumFactura", 0).getString("NumFactura", ""));
        cargarSpinner();
        cargarImagen();
        CargarBussinesName();
        CargarPrinterName();
        listaClientes();
        listaProductos();
        configurarAutcompletar();
        configurarAutcompletarPro();
        //selectMoneda();
        cargarDatosBill();
        this.d = 0;
        if (this.moneda.contains("...") || this.moneda.contains("..")) {
            this.sign.setText("");
        } else {
            TextView textView = this.sign;
            textView.setText(this.moneda + " ");
        }
        this.butProducts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate((int) R.id.action_nav_home_to_fragmentProducts);
            }
        });
        this.Add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintBill.this.CargarVenta();
            }
        });
        this.Print.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintBill.this.imprimirFactura();
            }
        });
        this.butNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PrintBill.this.NomImpresora.equals("") || PrintBill.this.NomImpresora.contains("...")) {
                    Toast makeText = Toast.makeText(PrintBill.this.getContext(), PrintBill.this.getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
                    makeText.setGravity(17, 0, 0);
                    makeText.show();
                } else if (!PrintBill.this.Resultado.getText().toString().equals("0")) {
                    Boolean unused = PrintBill.this.natShow = true;
                    PrintBill.this.NativeImprimir();
                    PrintBill.this.imprimirFactura();
                    if (!PrintBill.this.mostrado.booleanValue()) {
                        Boolean unused2 = PrintBill.this.mostrado = true;
                    }
                } else {
                    Toast makeText2 = Toast.makeText(PrintBill.this.getContext(), PrintBill.this.getString(R.string.ToastNoData), Toast.LENGTH_SHORT);
                    makeText2.setGravity(17, 0, 0);
                    makeText2.show();
                }
            }
        });
        this.butSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintBill.this.guardarVenta();
            }
        });
        this.Cantidad.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                PrintBill.this.calcularTotal();
            }
        });
        this.ValPro.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                PrintBill.this.Cantidad.setText("");
                PrintBill.this.TotProdu.setText("");
            }
        });
        floatingClickListener();
        rotarBoton(this.butProducts);
        comprobarInternet();
        if (getDefaultsPreference("Open2", getContext()).booleanValue()) {
            alertDialog();
        }
        verShared();
        cargarNative();
        return inflate;
    }


    public void ocultar() {
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public void cargarNative() {
        if (this.estado.equals("No")) {
            return;
        }
        this.Print.setVisibility(View.VISIBLE);
        this.butNext.setVisibility(View.GONE);
    }

    public void NativeImprimir() {
        ocultar();
        if (this.estado.equals("No")) {
            this.recycler.setVisibility(View.INVISIBLE);
            this.butSave.setVisibility(View.GONE);
            this.butNext.setVisibility(View.GONE);
            this.butBack.setVisibility(View.VISIBLE);
            this.Print.setVisibility(View.VISIBLE);
            this.layout.setVisibility(View.INVISIBLE);
            this.layout2.setVisibility(View.INVISIBLE);
            this.Add.setVisibility(View.INVISIBLE);
            this.natShow = true;
            this.butBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    PrintBill.this.recycler.setVisibility(View.VISIBLE);
                    PrintBill.this.butSave.setVisibility(View.VISIBLE);
                    PrintBill.this.butNext.setVisibility(View.VISIBLE);
                    PrintBill.this.butBack.setVisibility(View.GONE);
                    PrintBill.this.Print.setVisibility(View.GONE);
                    PrintBill.this.layout.setVisibility(View.VISIBLE);
                    PrintBill.this.layout2.setVisibility(View.VISIBLE);
                    PrintBill.this.Add.setVisibility(View.VISIBLE);
                    Boolean unused = PrintBill.this.natShow = false;
                }
            });
        }
    }

    public void verShared() {
        if (!getDefaultsPreference("Open2", getContext()).booleanValue()) {
            setDefaultsPreference("Open2", "Si", getContext());
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate((int) R.id.tutorialFragment);
        }
    }

    public static void setDefaultsPreference(String str, String str2, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(str, 0).edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public static Boolean getDefaultsPreference(String str, Context context) {
        boolean z = false;
        if (context.getSharedPreferences(str, 0).getString(str, "No") != "No") {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public void floatingClickListener() {
        this.fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintBill.this.animateFab();
            }
        });
        this.fabInstruction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate((int) R.id.tutorialFragment);
            }
        });
        this.fabInventory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate((int) R.id.inventario);
            }
        });
        this.fabClients.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate((int) R.id.clients);
            }
        });
        this.fabReports.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate((int) R.id.reportsFragment);
            }
        });
        this.fabReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate((int) R.id.nav_home);
            }
        });
    }

    /* access modifiers changed from: private */
    public void animateFab() {
        if (this.isOpen.booleanValue()) {
            this.fab.startAnimation(this.rotateClose);
            this.fabReports.startAnimation(this.toBottom);
            this.fabClients.startAnimation(this.toBottom);
            this.fabInventory.startAnimation(this.toBottom);
            this.fabInstruction.startAnimation(this.toBottom);
            this.fabReset.startAnimation(this.toBottom);
            this.fabReports.setClickable(false);
            this.fabClients.setClickable(false);
            this.fabInventory.setClickable(false);
            this.fabInstruction.setClickable(false);
            this.fabReset.setClickable(false);
            this.isOpen = false;
            return;
        }
        this.fab.startAnimation(this.rotateOpen);
        this.fabReports.startAnimation(this.fromBotton);
        this.fabClients.startAnimation(this.fromBotton);
        this.fabInventory.startAnimation(this.fromBotton);
        this.fabInstruction.startAnimation(this.fromBotton);
        this.fabReset.startAnimation(this.fromBotton);
        this.fabReports.setClickable(true);
        this.fabClients.setClickable(true);
        this.fabInventory.setClickable(true);
        this.fabInstruction.setClickable(true);
        this.fabReset.setClickable(true);
        this.isOpen = true;
    }

    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
        if (this.estado == null) {
            this.estado = "No";
        }
    }

    public void alertDialog() {
        if (this.formatoFecha.isEmpty() && this.direccion.isEmpty() && this.telefono.isEmpty() && this.taxes.isEmpty() && this.mensaje1.isEmpty() && this.mensaje2.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.customize)).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Navigation.findNavController(PrintBill.this.getView()).navigate((int) R.id.bills2Fragment);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }

    public void cargarDatosBill() {
        SQLiteDatabase writableDatabase = new BaseDatosBills(getActivity(), "registro", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select formatofecha, direccion, telefono, taxes, mensaje1, mensaje2 from datosbill where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.formatoFecha = rawQuery.getString(0);
            this.direccion = rawQuery.getString(1);
            this.telefono = rawQuery.getString(2);
            this.taxes = rawQuery.getString(3);
            this.mensaje1 = rawQuery.getString(4);
            this.mensaje2 = rawQuery.getString(5);
            writableDatabase.close();
            return;
        }
        crearConfiguracionBaseBill();
        cargarDatosBill();
    }

    public void ModificarCantidad(String str, String str2) {
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getContext(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select codigo, nombre, valor, cantidad from productos where nombre ='" + str + "'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            String string = rawQuery.getString(0);
            String string2 = rawQuery.getString(1);
            String string3 = rawQuery.getString(2);
            String string4 = rawQuery.getString(3);
            if (string4 == null) {
                string4 = "0";
            }
            String num = Integer.toString(Integer.parseInt(string4) - Integer.parseInt(str2));
            ContentValues contentValues = new ContentValues();
            contentValues.put("codigo", string);
            contentValues.put("nombre", string2);
            contentValues.put("valor", string3);
            contentValues.put("cantidad", num);
            writableDatabase.update("productos", contentValues, "codigo='" + string + "'", (String[]) null);
            writableDatabase.close();
        }
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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void selectMoneda() {
        /*
            r7 = this;
            java.lang.String r0 = r7.moneda
            int r1 = r0.hashCode()
            r2 = 17
            r3 = 0
            r4 = 1
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r4)
            switch(r1) {
                case 75: goto L_0x010a;
                case 81: goto L_0x00ff;
                case 163: goto L_0x00f5;
                case 165: goto L_0x00eb;
                case 2051: goto L_0x00e1;
                case 2113: goto L_0x00d7;
                case 2284: goto L_0x00cd;
                case 2439: goto L_0x00c2;
                case 2578: goto L_0x00b7;
                case 2619: goto L_0x00ac;
                case 2620: goto L_0x00a0;
                case 2654: goto L_0x0094;
                case 2655: goto L_0x0088;
                case 2657: goto L_0x007c;
                case 4104: goto L_0x0070;
                case 8353: goto L_0x0064;
                case 8358: goto L_0x0058;
                case 8364: goto L_0x004d;
                case 8369: goto L_0x0042;
                case 8377: goto L_0x0036;
                case 8378: goto L_0x002a;
                case 8381: goto L_0x001f;
                case 71553: goto L_0x0013;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x0115
        L_0x0013:
            java.lang.String r1 = "HK$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 8
            goto L_0x0116
        L_0x001f:
            java.lang.String r1 = "₽"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 4
            goto L_0x0116
        L_0x002a:
            java.lang.String r1 = "₺"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 19
            goto L_0x0116
        L_0x0036:
            java.lang.String r1 = "₹"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 12
            goto L_0x0116
        L_0x0042:
            java.lang.String r1 = "₱"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 1
            goto L_0x0116
        L_0x004d:
            java.lang.String r1 = "€"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 0
            goto L_0x0116
        L_0x0058:
            java.lang.String r1 = "₦"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 11
            goto L_0x0116
        L_0x0064:
            java.lang.String r1 = "₡"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 22
            goto L_0x0116
        L_0x0070:
            java.lang.String r1 = "zł"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 15
            goto L_0x0116
        L_0x007c:
            java.lang.String r1 = "Rs"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 13
            goto L_0x0116
        L_0x0088:
            java.lang.String r1 = "SR"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 14
            goto L_0x0116
        L_0x0094:
            java.lang.String r1 = "Rp"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 9
            goto L_0x0116
        L_0x00a0:
            java.lang.String r1 = "S/"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 21
            goto L_0x0116
        L_0x00ac:
            java.lang.String r1 = "RM"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 20
            goto L_0x0116
        L_0x00b7:
            java.lang.String r1 = "R$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 10
            goto L_0x0116
        L_0x00c2:
            java.lang.String r1 = "Kr"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 17
            goto L_0x0116
        L_0x00cd:
            java.lang.String r1 = "Fr"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 6
            goto L_0x0116
        L_0x00d7:
            java.lang.String r1 = "C$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 7
            goto L_0x0116
        L_0x00e1:
            java.lang.String r1 = "A$"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 5
            goto L_0x0116
        L_0x00eb:
            java.lang.String r1 = "¥"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 2
            goto L_0x0116
        L_0x00f5:
            java.lang.String r1 = "£"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 3
            goto L_0x0116
        L_0x00ff:
            java.lang.String r1 = "Q"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 18
            goto L_0x0116
        L_0x010a:
            java.lang.String r1 = "K"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0115
            r0 = 16
            goto L_0x0116
        L_0x0115:
            r0 = -1
        L_0x0116:
            r1 = 30
            r6 = 170(0xaa, float:2.38E-43)
            switch(r0) {
                case 0: goto L_0x0301;
                case 1: goto L_0x02ed;
                case 2: goto L_0x02d9;
                case 3: goto L_0x02c4;
                case 4: goto L_0x02af;
                case 5: goto L_0x029a;
                case 6: goto L_0x0285;
                case 7: goto L_0x0270;
                case 8: goto L_0x025b;
                case 9: goto L_0x0246;
                case 10: goto L_0x0231;
                case 11: goto L_0x021c;
                case 12: goto L_0x0207;
                case 13: goto L_0x01f2;
                case 14: goto L_0x01dd;
                case 15: goto L_0x01c8;
                case 16: goto L_0x01b3;
                case 17: goto L_0x019e;
                case 18: goto L_0x0189;
                case 19: goto L_0x0174;
                case 20: goto L_0x015f;
                case 21: goto L_0x014a;
                case 22: goto L_0x0135;
                default: goto L_0x011d;
            }
        L_0x011d:
            java.lang.String r0 = r7.moneda
            java.lang.String r1 = "$"
            boolean r0 = r0.equals(r1)
            java.lang.String r4 = ""
            java.lang.String r6 = ".."
            if (r0 != 0) goto L_0x033f
            java.lang.String r0 = r7.moneda
            boolean r0 = r0.contains(r6)
            if (r0 == 0) goto L_0x0315
            goto L_0x033f
        L_0x0135:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230832(0x7f080070, float:1.8077728E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x014a:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230949(0x7f0800e5, float:1.8077965E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x015f:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230898(0x7f0800b2, float:1.8077862E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0174:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230897(0x7f0800b1, float:1.807786E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0189:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230938(0x7f0800da, float:1.8077943E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x019e:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230854(0x7f080086, float:1.8077773E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x01b3:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230906(0x7f0800ba, float:1.8077878E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x01c8:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230956(0x7f0800ec, float:1.807798E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x01dd:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230941(0x7f0800dd, float:1.8077949E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x01f2:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230945(0x7f0800e1, float:1.8077957E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0207:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230943(0x7f0800df, float:1.8077953E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x021c:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230916(0x7f0800c4, float:1.8077898E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0231:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230939(0x7f0800db, float:1.8077945E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0246:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230944(0x7f0800e0, float:1.8077955E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x025b:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230864(0x7f080090, float:1.8077793E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0270:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230862(0x7f08008e, float:1.8077789E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0285:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230877(0x7f08009d, float:1.807782E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x029a:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230861(0x7f08008d, float:1.8077787E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x02af:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230942(0x7f0800de, float:1.807795E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x02c4:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230868(0x7f080094, float:1.80778E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x02d9:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230955(0x7f0800eb, float:1.8077977E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x02ed:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230934(0x7f0800d6, float:1.8077935E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0301:
            android.content.res.Resources r0 = r7.getResources()
            r2 = 2131230869(0x7f080095, float:1.8077803E38)
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeResource(r0, r2)
            r7.currency = r0
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r6, r1, r4)
            r7.logoMoneda = r0
            goto L_0x0358
        L_0x0315:
            android.content.Context r0 = r7.getContext()
            java.lang.String r1 = "Open2"
            java.lang.Boolean r0 = getDefaultsPreference(r1, r0)
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x033a
            android.content.Context r0 = r7.getContext()
            r1 = 2131951927(0x7f130137, float:1.9540282E38)
            java.lang.String r1 = r7.getString(r1)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r3)
            r0.setGravity(r2, r3, r3)
            r0.show()
        L_0x033a:
            r7.signoMoneda = r4
            r7.noCurrency = r5
            goto L_0x0358
        L_0x033f:
            java.lang.String r0 = r7.moneda
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x034c
            r7.signoMoneda = r1
            r7.noCurrency = r5
            goto L_0x0358
        L_0x034c:
            java.lang.String r0 = r7.moneda
            boolean r0 = r0.contains(r6)
            if (r0 == 0) goto L_0x0358
            r7.signoMoneda = r4
            r7.noCurrency = r5
        L_0x0358:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: apps.james1.Probadorimpresorabluetooth.FragmentsUI.PrintBill.PrintBill.selectMoneda():void");
    }

    public void comprobarInternet() {
        if (this.estado.equals("No") && !new comprobarInternet().isOnlineNet().booleanValue()) {
            Toast.makeText(getContext(), getString(R.string.ToastInternet), 1).show();
            this.butSave.setEnabled(false);
            this.Print.setEnabled(false);
            this.butProducts.setEnabled(false);
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

    public void configurarAutcompletarPro() {
        this.NomPro.setAdapter(new ArrayAdapter(getContext(), 17367050, this.listaProductos));
        this.NomPro.setThreshold(1);
        this.NomPro.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("beforeTextChanged", String.valueOf(charSequence));
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("onTextChanged", String.valueOf(charSequence));
            }

            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", String.valueOf(editable));
                PrintBill.this.cargarPrecio();
                PrintBill.this.cargarSpinner();
            }
        });
        this.NomPro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                PrintBill.this.Cantidad.requestFocus();
            }
        });
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            public void handleOnBackPressed() {
            }
        });
    }

    public void Consecutivo() {
        String charSequence = this.nFacturas.getText().toString();
        if (charSequence.isEmpty()) {
            charSequence = "1";
        }
        this.number = String.valueOf(Integer.parseInt(charSequence) + 1);
    }

    public void Consecutivo2() {
        SharedPreferences.Editor edit = getActivity().getSharedPreferences("NumFactura", 0).edit();
        edit.putString("NumFactura", this.nFacturas.getText().toString());
        edit.commit();
    }

    private void rotarBoton(View view) {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatMode(2);
        view.startAnimation(rotateAnimation);
    }

    public void cargarPrecio() {
        this.ValPro.setText("");
        this.Cantidad.setText("");
        this.TotProdu.setText("");
        SQLiteDatabase writableDatabase = new BaseDatosProductos(getActivity(), "registroProductos", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        String obj = this.NomPro.getText().toString();
        if (!obj.isEmpty()) {
            Cursor rawQuery = writableDatabase.rawQuery("select valor from productos where nombre ='" + obj + "'", (String[]) null);
            if (rawQuery.moveToFirst()) {
                this.ValPro.setText(rawQuery.getString(0));
                writableDatabase.close();
            }
        }
    }

    public void calcularTotal() {
        String obj = this.Cantidad.getText().toString();
        String obj2 = this.ValPro.getText().toString();
        if (obj.contains("-")) {
            obj = obj.replaceFirst("-", "");
            this.negativo = true;
        } else {
            this.negativo = false;
        }
        if (!obj.isEmpty() && !obj2.isEmpty()) {
            double parseDouble = Double.parseDouble(obj2);
            double parseDouble2 = Double.parseDouble(obj);
            double d2 = parseDouble * parseDouble2;
            if (this.negativo.booleanValue()) {
                this.m = d2 * -1.0d;
            } else {
                this.m = d2;
            }
            String format = String.format("%.2f", new Object[]{Double.valueOf(this.m)});
            if (parseDouble2 == 0.0d) {
                this.TotProdu.setText("");
            }
            this.TotProdu.setText(format.replace(',', '.'));
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
            if (this.moneda == null) {
                this.moneda = "";
            }
            writableDatabase.close();
            return;
        }
        this.BUsinessName = "";
        this.moneda = "";
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
                this.logoEmpresa = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length), 350, 150, true);
                writableDatabase.close();
                return;
            }
            writableDatabase.close();
        } else if (getDefaultsPreference("Open2", getContext()).booleanValue()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastLodoNoLoaded), 0);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        }
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
    public void cargarSpinner() {
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
                    PrintBill.this.NomPro.setText(adapterView.getItemAtPosition(i).toString());
                    PrintBill.this.Cantidad.requestFocus();
                }
                PrintBill.this.cargarPrecio();
            }
        });
    }

    public void CargarVenta() {
        this.recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
        String obj = this.NomPro.getText().toString();
        String obj2 = this.Cantidad.getText().toString();
        String obj3 = this.Cantidad.getText().toString();
        String charSequence = this.TotProdu.getText().toString();
        if (this.saved.booleanValue()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.sellSaved), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        } else if (obj.contains("...") || this.d != 0 || obj.equals("") || obj2.equals("") || charSequence.equals("") || obj3.equals("")) {
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastNoProducts), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
        } else {
            this.listDatosMostrar.add(new VentaVo(this.NomPro.getText().toString(), this.Cantidad.getText().toString(), this.ValPro.getText().toString().replace(',', '.'), this.TotProdu.getText().toString().replace(',', '.')));
            this.Resultado.setText(String.format("%.2f", new Object[]{Double.valueOf(Double.parseDouble(this.TotProdu.getText().toString()) + Double.parseDouble(this.Resultado.getText().toString()))}).replace(',', '.'));
            this.a++;
        }
        this.adapter = new AdaptadorVenta(this.listDatosMostrar);
        listenerAdapter(true);
        this.recycler.setAdapter(this.adapter);
        this.NomPro.setText("");
        this.ValPro.setText("");
        this.Cantidad.setText("");
        this.TotProdu.setText("");
        cargarSpinner();
    }

    public void listenerAdapter(Boolean bool) {
        this.adapter.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                new AlertDialog.Builder(PrintBill.this.getContext()).setTitle((CharSequence) PrintBill.this.getString(R.string.delProVen)).setMessage((int) R.string.sureDelProVen).setPositiveButton(17039379, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!PrintBill.this.impresionOk.booleanValue()) {
                            PrintBill.this.Resultado.setText(String.format("%.2f", new Object[]{Double.valueOf(Double.parseDouble(PrintBill.this.Resultado.getText().toString()) - Double.parseDouble(PrintBill.this.listDatosMostrar.get(PrintBill.this.recycler.getChildAdapterPosition(view)).getPrecioTo()))}).replace(',', '.'));
                            PrintBill.this.listDatosMostrar.remove(PrintBill.this.recycler.getChildAdapterPosition(view));
                            PrintBill.this.recycler.setAdapter(PrintBill.this.adapter);
                            return;
                        }
                        Toast makeText = Toast.makeText(PrintBill.this.getContext(), PrintBill.this.getString(R.string.sellSaved), 0);
                        makeText.setGravity(17, 0, 0);
                        makeText.show();
                    }
                }).setNegativeButton(17039369, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });
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

    public void imprimirFactura() {
        if (this.NomImpresora.equals("")) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
            makeText.setGravity(80, 0, 300);
            makeText.show();
        } else if (this.NomImpresora.contains("...")) {
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
            makeText2.setGravity(80, 0, 300);
            makeText2.show();
        } else if (!this.Resultado.getText().toString().equals("0")) {
            try {
                FindBluetoothDevice();
                openBluetoothPrinter();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (!this.bluetoothSocket.isConnected()) {
                    Toast makeText3 = Toast.makeText(getActivity(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
                    makeText3.setGravity(80, 0, 300);
                    makeText3.show();
                } else if (this.b == 1) {
                    printBill();
                    this.impresionOk = true;
                    if (this.d == 0) {
                        guardarVenta();
                        comprobarInternet();
                    }
                    try {
                        Thread.sleep(2000);
                        Desconex();
                    } catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (Exception e4) {
                e4.printStackTrace();
                Toast makeText4 = Toast.makeText(getActivity(), getString(R.string.turnBluetooth), Toast.LENGTH_SHORT);
                makeText4.setGravity(80, 0, 300);
                makeText4.show();
                encenderBluetooth();
            }
        } else {
            Toast makeText5 = Toast.makeText(getActivity(), getString(R.string.ToastNoData), Toast.LENGTH_SHORT);
            makeText5.setGravity(80, 0, 300);
            makeText5.show();
        }
    }

    public void guardarVenta() {
        if (this.Resultado.getText().toString().equals("0")) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.NoDataToSave), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        } else if (this.saved.booleanValue()) {
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.toastSaleSaved), Toast.LENGTH_SHORT);
            makeText2.setGravity(80, 0, 300);
            makeText2.show();
        } else if (this.a != 0 && this.d == 0) {
            int size = this.listDatosMostrar.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                SQLiteDatabase writableDatabase = new BaseDeDatosVentas(getContext(), "RegistroVentas", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
                String str = this.fecha4;
                String producto = this.listDatosMostrar.get(i).getProducto();
                String cantidad = this.listDatosMostrar.get(i).getCantidad();
                String precioTo = this.listDatosMostrar.get(i).getPrecioTo();
                String obj = !this.edtCliente.getText().toString().isEmpty() ? this.edtCliente.getText().toString() : "";
                i++;
                ContentValues contentValues = new ContentValues();
                contentValues.put("fecha", str);
                contentValues.put("producto", producto);
                contentValues.put("cantidad", cantidad);
                contentValues.put("total", precioTo);
                contentValues.put("cliente", obj);
                writableDatabase.insert("Ventas", (String) null, contentValues);
                writableDatabase.close();
                this.saved = true;
                this.d = 1;
                ModificarCantidad(producto, cantidad);
            }
            Toast makeText3 = Toast.makeText(getContext(), getString(R.string.toastSaleSaved), Toast.LENGTH_SHORT);
            makeText3.setGravity(80, 0, 300);
            makeText3.show();
            if (!this.natShow.booleanValue()) {
                NativeImprimir();
            }
        }
    }

    public void Desconex() {
        try {
            disconnectBT();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void encenderBluetooth() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
    }

    /* access modifiers changed from: package-private */
    public void FindBluetoothDevice() {
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.bluetoothAdapter = defaultAdapter;
            if (defaultAdapter == null) {
                Toast makeText = Toast.makeText(getContext(), "No se encuentra adaptador bluetooth", Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
            }
            if (this.bluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
            }
            Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice next : bondedDevices) {
                    next.getName();
                    if (next.getName().equals(this.NomImpresora)) {
                        this.bluetoothDevice = next;
                        this.b = 1;
                        return;
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
            makeText2.setGravity(80, 0, 300);
            makeText2.show();
        }
    }

    /* access modifiers changed from: package-private */
    public void openBluetoothPrinter() throws IOException {
        try {
            BluetoothSocket createRfcommSocketToServiceRecord = this.bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            this.bluetoothSocket = createRfcommSocketToServiceRecord;
            createRfcommSocketToServiceRecord.connect();
            this.outputStream = this.bluetoothSocket.getOutputStream();
            this.inputStream = this.bluetoothSocket.getInputStream();
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public void disconnectBT() throws IOException {
        try {
            this.stopWorker = true;
            this.outputStream.close();
            this.inputStream.close();
            this.bluetoothSocket.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void printCustom(String str, int i, int i2) {
        byte[] bArr = {PrinterCommands.ESC, 33, 3};
        byte[] bArr2 = {PrinterCommands.ESC, 33, 8};
        byte[] bArr3 = {PrinterCommands.ESC, 33, 32};
        byte[] bArr4 = {PrinterCommands.ESC, 33, PrinterCommands.DLE};
        try {
            if (i == 0) {
                this.outputStream.write(bArr);
            } else if (i == 1) {
                this.outputStream.write(bArr2);
            } else if (i == 2) {
                this.outputStream.write(bArr3);
            } else if (i == 3) {
                try {
                    this.outputStream.write(bArr4);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            if (i2 == 0) {
                this.outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            } else if (i2 == 1) {
                this.outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            } else if (i2 == 2) {
                this.outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
            }
            this.outputStream.write(str.getBytes());
            this.outputStream.write(10);
        }catch (Exception ignored){

        }
    }

    private void printNewLine() {
        try {
            this.outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void printPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                byte[] decodeBitmap = Utils.decodeBitmap(bitmap);
                this.outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(decodeBitmap);
            } catch (Exception e2) {
                e2.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        } else {
            Log.e("Print Photo error", "the file isn't exists");
        }
    }

    private void printText(byte[] bArr) {
        try {
            this.outputStream.write(bArr);
            printNewLine();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void printPhotoPrueba(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                printTextPrueba(Utils.decodeBitmap(bitmap));
            } catch (Exception e2) {
                e2.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        } else {
            Log.e("Print Photo error", "the file isn't exists");
        }
    }

    private void printTextPrueba(byte[] bArr) {
        try {
            this.outputStream.write(bArr);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void printBill() {
        String str;
        String string = getString(R.string.Qty);
        String string2 = getString(R.string.prices);
        String string3 = getString(R.string.tPrice);
        if (!this.p.booleanValue()) {
            Consecutivo();
            this.nFacturas.setText(this.number);
            Consecutivo2();
            this.p = true;
        }
        int size = this.listDatosMostrar.size();
        if (this.formatoFecha.isEmpty() || this.formatoFecha.equals("24-01-2021")) {
            str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        } else {
            str = "";
        }
        if (this.formatoFecha.equals("01-24-2021")) {
            str = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
        }
        printPhoto(this.logoEmpresa);
        String str2 = this.BUsinessName;
        if (str2 != null) {
            printCustom(str2, 2, 1);
        }
        if (!this.direccion.isEmpty()) {
            printCustom(this.direccion, 1, 1);
        }
        if (!this.telefono.isEmpty()) {
            printCustom(this.telefono, 1, 1);
        }
        if (!this.edtCliente.getText().toString().isEmpty()) {
            String obj = this.edtCliente.getText().toString();
            printCustom(getString(R.string.Client) + obj, 1, 1);
        }
        TextView textView = this.nFacturas;
        if (textView != null) {
            String charSequence = textView.getText().toString();
            printCustom(getString(R.string.Bill) + charSequence, 1, 1);
        }
        printCustom(str, 0, 1);
        printNewLine();
        printCustom(String.format("%1$6s %2$8s %3$12s", new Object[]{string, string2, string3}), 0, 2);
        printCustom("-------------------------------", 0, 1);
        printNewLine();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            String producto = this.listDatosMostrar.get(i).getProducto();
            String cantidad = this.listDatosMostrar.get(i).getCantidad();
            String precioUni = this.listDatosMostrar.get(i).getPrecioUni();
            String precioTo = this.listDatosMostrar.get(i).getPrecioTo();
            printCustom(producto, 1, 0);
            printCustom(String.format(" %1$6s %2$8s %3$12s", new Object[]{cantidad, precioUni, precioTo}), 0, 2);
            i++;
        }
        printNewLine();
        String charSequence2 = this.Resultado.getText().toString();
        printCustom("-------------------------------", 0, 1);
        if (!this.noCurrency.booleanValue()) {
            try {
                this.outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                printPhotoPrueba(this.logoMoneda);
                printNewLine();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (this.noCurrency.booleanValue()) {
            printCustom("TOTAL ", 2, 0);
            printNewLine();
        }
        printCustom(this.signoMoneda + charSequence2, 2, 2);
        if (!this.taxes.isEmpty()) {
            printCustom(this.taxes, 0, 2);
        }
        printCustom("-------------------------------", 0, 1);
        if (!this.mensaje1.isEmpty()) {
            printCustom(this.mensaje1, 0, 1);
            printNewLine();
        }
        if (!this.mensaje2.isEmpty()) {
            printCustom(this.mensaje2, 0, 1);
            printNewLine();
            printNewLine();
            return;
        }
        printCustom("*** " + getString(R.string.ToastThanks) + " ***", 0, 1);
        printNewLine();
        printNewLine();
    }
}
