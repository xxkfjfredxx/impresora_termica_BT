package com.example.manejoimpresora.FragmentsUI.Others;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones2;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Set;

public class SettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int READ_REQUEST_CODE = 42;
    /* access modifiers changed from: private */
    public String NomImpresora;
    private Button bNameBusisness;
    private Button bSaveLogo;
    private Button bSearh;
    private BluetoothAdapter bluetoothAdapter;
    private Boolean buscar;
    private EditText bussinesName;
    private Button buttonBlueSettings;
    private Button buttonCurrency;
    private Button buttonCustomize;
    private Button buttonDelete;
    private Button buttonInstructions;
    private Button buttonPolitica;
    private Button buttonPrintSize;
    private Button buttonRefresh;
    private Button buttonSavePrinter;
    /* access modifiers changed from: private */
    public EditText currency;
    private String direccion;
    private String estado = "No";
    private ImageView logoNegocio;
    /* access modifiers changed from: private */
    public String moneda;
    /* access modifiers changed from: private */
    public String printSize;
    /* access modifiers changed from: private */
    public int printed;
    private RadioButton size104;
    private RadioButton size58;
    private RadioButton size80;
    private Spinner spinnerLista;
    private Spinner spinnerMoneda;
    /* access modifiers changed from: private */
    public TextView tvPrintName;

    static /* synthetic */ int access$008(SettingsFragment settingsFragment) {
        int i = settingsFragment.printed;
        settingsFragment.printed = i + 1;
        return i;
    }

    public static SettingsFragment newInstance(String str, String str2) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_settings, viewGroup, false);
        this.tvPrintName = (TextView) inflate.findViewById(R.id.tVPrintName);
        this.bussinesName = (EditText) inflate.findViewById(R.id.edtBussiName);
        this.currency = (EditText) inflate.findViewById(R.id.edtCurrency);
        this.logoNegocio = (ImageView) inflate.findViewById(R.id.imageLogoNegocio);
        this.bSearh = (Button) inflate.findViewById(R.id.ButtonSearch);
        this.bSaveLogo = (Button) inflate.findViewById(R.id.buttonSaveLogo);
        this.bNameBusisness = (Button) inflate.findViewById(R.id.buttonSaveBname);
        this.buttonBlueSettings = (Button) inflate.findViewById(R.id.buttonBlueSettings);
        this.buttonPolitica = (Button) inflate.findViewById(R.id.buttonPolitica);
        this.buttonInstructions = (Button) inflate.findViewById(R.id.buttonInstructions);
        this.buttonDelete = (Button) inflate.findViewById(R.id.buttonDelete);
        this.buttonPrintSize = (Button) inflate.findViewById(R.id.buttonPrintSize);
        this.buttonCurrency = (Button) inflate.findViewById(R.id.buttonSaveCurrency);
        this.buttonCustomize = (Button) inflate.findViewById(R.id.buttonCustomize);
        this.buttonSavePrinter = (Button) inflate.findViewById(R.id.buttonSavePrinter);
        this.buttonRefresh = (Button) inflate.findViewById(R.id.buttonBlueSettings2);
        this.spinnerMoneda = (Spinner) inflate.findViewById(R.id.spinnerCurrency);
        this.spinnerLista = (Spinner) inflate.findViewById(R.id.spinner);
        this.size58 = (RadioButton) inflate.findViewById(R.id.radioButton58);
        this.size80 = (RadioButton) inflate.findViewById(R.id.radioButton80);
        this.size104 = (RadioButton) inflate.findViewById(R.id.radioButton104);
        this.printed = 0;
        this.printSize = "";
        this.buscar = false;
        this.buttonCustomize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.access$008(SettingsFragment.this);
                Navigation.findNavController(view).navigate((int) R.id.bills2Fragment);
            }
        });
        this.buttonCurrency.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.saveCurrency();
            }
        });
        this.size58.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String unused = SettingsFragment.this.printSize = "58";
            }
        });
        this.size80.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String unused = SettingsFragment.this.printSize = "80";
            }
        });
        this.size104.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String unused = SettingsFragment.this.printSize = "104";
            }
        });
        this.buttonPrintSize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.savePrinterSize();
            }
        });
        this.bSearh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.BuscarImagen();
            }
        });
        this.bSaveLogo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.guardarImagen();
            }
        });
        this.bNameBusisness.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.saveBusinessName();
            }
        });
        this.buttonBlueSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.startActivityForResult(new Intent("android.settings.BLUETOOTH_SETTINGS"), 0);
            }
        });
        this.buttonPolitica.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/politicapivacidadappimpresiont/p%C3%A1gina-principal")));
            }
        });
        this.buttonInstructions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/watch?v=WbD5rJdTq4k")));
            }
        });
        this.buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.eliminarImagen();
                SettingsFragment.this.saveBusinessName();
            }
        });
        this.buttonSavePrinter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.savePrinterName();
            }
        });
        this.buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SettingsFragment.this.ListaBluetooth();
            }
        });
        cargarSuscripcion();
        ListaBluetooth();
        cargarSharePreferences();
        cargarImagen();
        CargarSpinnerMoneda();
        CargarNombreImpresora();
        encenderBluetooth();
        return inflate;
    }

    public void encenderBluetooth() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        }
    }

    public void ListaBluetooth() {
        ArrayList arrayList = new ArrayList();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Toast.makeText(getContext(), "No se encuentra adaptador bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (this.bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
        }
        Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
        arrayList.add(getString(R.string.SelectPRint));
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice next : bondedDevices) {
                arrayList.add(next.getName());
                next.getAddress();
            }
        }
        this.spinnerLista.setAdapter(new ArrayAdapter(getContext(), 17367043, arrayList));
        this.spinnerLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String unused = SettingsFragment.this.NomImpresora = adapterView.getItemAtPosition(i).toString();
                if (!SettingsFragment.this.NomImpresora.contains("...")) {
                    SettingsFragment.this.tvPrintName.setText(SettingsFragment.this.NomImpresora);
                }
            }
        });
    }

    public void savePrinterName() {
        crearConfiguracionBase2();
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones2(getContext(), "registroConfiguraciones2",null, 1).getWritableDatabase();
        String str = this.NomImpresora;
        if (!str.isEmpty()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", "1");
            contentValues.put("printerName", str);
            int update = writableDatabase.update("configuraciones", contentValues, "numero='1'",null);
            writableDatabase.close();
            this.printed++;
            if (update == 1) {
                Toast.makeText(getContext(), getString(R.string.PrintNameSaved), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void CargarNombreImpresora() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones2(getActivity(), "registroConfiguraciones2",  null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select printerName from configuraciones where numero ='1'",  null);
        if (rawQuery.moveToFirst()) {
            this.NomImpresora = rawQuery.getString(0);
            writableDatabase.close();
        } else {
            this.NomImpresora = "";
        }
        String str = this.NomImpresora;
        if (str != null && !str.isEmpty() && this.NomImpresora.length() != 0) {
            this.tvPrintName.setText(this.NomImpresora);
        }
    }

    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion",  null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
    }

    public void CargarSpinnerMoneda() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(getString(R.string.Select));
        arrayList.add(getString(R.string.NoCurrency));
        arrayList.add("$");
        arrayList.add("€");
        arrayList.add("₱");
        arrayList.add("¥");
        arrayList.add("£");
        arrayList.add("₽");
        arrayList.add("₡");
        arrayList.add("A$");
        arrayList.add("C$");
        arrayList.add("Fr");
        arrayList.add("HK$");
        arrayList.add("K");
        arrayList.add("Kr");
        arrayList.add("RM");
        arrayList.add("Rp");
        arrayList.add("R$");
        arrayList.add("Q");
        arrayList.add("₦");
        arrayList.add("₹");
        arrayList.add("Rs");
        arrayList.add("SR");
        arrayList.add("S/");
        arrayList.add("₺");
        arrayList.add("zł");
        this.spinnerMoneda.setAdapter(new ArrayAdapter(getContext(), 17367043, arrayList));
        this.spinnerMoneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String unused = SettingsFragment.this.moneda = adapterView.getItemAtPosition(i).toString();
                if (!SettingsFragment.this.moneda.contains("...")) {
                    SettingsFragment.this.currency.setText(SettingsFragment.this.moneda);
                }
            }
        });
    }

    public void BuscarImagen() {
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("image/*");
        startActivityForResult(intent, 42);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 42 && i2 == -1 && intent != null) {
            Uri data = intent.getData();
            this.direccion = data.toString();
            this.logoNegocio.setImageURI(data);
            this.buscar = true;
        }
        if (i == 1 && i2 == -1) {
            ListaBluetooth();
        }
    }

    public void eliminarImagen() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones", null, 1).getWritableDatabase();
        new ContentValues().put("foto", "");
        int delete = writableDatabase.delete("configuraciones", "numero='1'",  null);
        writableDatabase.close();
        if (delete == 1) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastImageDeleted), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        }
    }

    public void guardarImagen() {
        if (this.buscar.booleanValue()) {
            crearConfiguracionBase();
            Bitmap bitmap = ((BitmapDrawable) this.logoNegocio.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, true).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones",  null, 1).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("foto", byteArray);
            int update = writableDatabase.update("configuraciones", contentValues, "numero='1'", (String[]) null);
            writableDatabase.close();
            if (update == 1) {
                Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastImageSaved), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
                this.printed++;
            }
        }
    }

    public void crearConfiguracionBase() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones", null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        writableDatabase.insert("configuraciones", (String) null, contentValues);
        writableDatabase.close();
    }

    public void crearConfiguracionBase2() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones2(getContext(), "registroConfiguraciones2", null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        writableDatabase.insert("configuraciones", (String) null, contentValues);
        writableDatabase.close();
    }

    public void cargarImagen() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones",null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select foto from configuraciones where numero ='" + "1" + "'",null);
        if (rawQuery.moveToFirst()) {
            byte[] blob = rawQuery.getBlob(0);
            if (blob == null) {
                return;
            }
            if (blob.length != 0) {
                this.logoNegocio.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                writableDatabase.close();
                return;
            }
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastLodoNoLoaded), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            writableDatabase.close();
            return;
        }
        Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastLodoNoLoaded), Toast.LENGTH_SHORT);
        makeText2.setGravity(17, 0, 0);
        makeText2.show();
        writableDatabase.close();
    }

    public void saveBusinessName() {
        crearConfiguracionBase();
        SharedPreferences.Editor edit = getActivity().getSharedPreferences("NomBusiness", 0).edit();
        edit.putString("NomBusiness", this.bussinesName.getText().toString());
        edit.commit();
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones",  null, 1).getWritableDatabase();
        String obj = this.bussinesName.getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        contentValues.put("nombreImpresora", obj);
        int update = writableDatabase.update("configuraciones", contentValues, "numero='1'", (String[]) null);
        writableDatabase.close();
        if (update == 1) {
            this.printed++;
        }
    }

    public void saveCurrency() {
        SharedPreferences.Editor edit = getActivity().getSharedPreferences("CURRENCY", 0).edit();
        edit.putString("CURRENCY", this.currency.getText().toString());
        edit.commit();
        crearConfiguracionBase();
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones",null, 1).getWritableDatabase();
        String obj = this.currency.getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        contentValues.put("otra", obj);
        int update = writableDatabase.update("configuraciones", contentValues, "numero='1'", (String[]) null);
        writableDatabase.close();
        if (update == 1) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastCurrency), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            this.printed++;
        }
    }

    public void cargarSharePreferences() {
        this.bussinesName.setText(getActivity().getSharedPreferences("NomBusiness", 0).getString("NomBusiness", ""));
        this.currency.setText(getActivity().getSharedPreferences("CURRENCY", 0).getString("CURRENCY", ""));
    }

    public void savePrinterSize() {
        crearConfiguracionBase();
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones",  null, 1).getWritableDatabase();
        if (!this.printSize.isEmpty()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", "1");
            contentValues.put("printSize", this.printSize);
            int update = writableDatabase.update("configuraciones", contentValues, "numero='1'", (String[]) null);
            writableDatabase.close();
            if (update == 1) {
                Toast makeText = Toast.makeText(getContext(), getString(R.string.PrintSizeSaved), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
                this.printed++;
            }
        }
    }
}
