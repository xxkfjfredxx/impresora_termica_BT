package com.example.manejoimpresora.FragmentsUI.Others;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.manejoimpresora.DataBases.BaseDatosBills;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones;
import com.example.manejoimpresora.R;

import java.util.ArrayList;

public class SetBillFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String direccion;
    private EditText edtAdress;
    /* access modifiers changed from: private */
    public TextView edtDateformat;
    private EditText edtMessage1;
    private EditText edtMessage2;
    private TextView edtName;
    private EditText edtPhone;
    private EditText edtTaxes;
    private String formatoFecha;
    private ImageView imageViewLogo;
    private String mParam1;
    private String mParam2;
    private String mensaje1;
    private String mensaje2;
    private String nombreNegocio;
    private Button save;
    private Spinner spinnerFormat;
    private String taxes;
    private String telefono;

    public static SetBillFragment newInstance(String str, String str2) {
        SetBillFragment setBillFragment = new SetBillFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        setBillFragment.setArguments(bundle);
        return setBillFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_bills2, viewGroup, false);
        this.imageViewLogo = (ImageView) inflate.findViewById(R.id.imageViewlogo1);
        this.edtName = (TextView) inflate.findViewById(R.id.edtName);
        this.spinnerFormat = (Spinner) inflate.findViewById(R.id.spinnerFormat);
        this.edtDateformat = (TextView) inflate.findViewById(R.id.textViewFecha);
        this.edtAdress = (EditText) inflate.findViewById(R.id.edtAddress);
        this.edtPhone = (EditText) inflate.findViewById(R.id.edtPhone);
        this.edtTaxes = (EditText) inflate.findViewById(R.id.edtTaxes);
        this.edtMessage1 = (EditText) inflate.findViewById(R.id.edtMessage1);
        this.edtMessage2 = (EditText) inflate.findViewById(R.id.edtMessagge2);
        Button button = (Button) inflate.findViewById(R.id.button);
        this.save = button;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SetBillFragment.this.guardar();
            }
        });
        cargarImagen();
        cargarSpinner();
        cargarDatos();
        return inflate;
    }

    public void cargarDatos() {
        SQLiteDatabase writableDatabase = new BaseDatosBills(getActivity(), "registro", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select formatofecha, direccion, telefono, taxes, mensaje1, mensaje2 from datosbill where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.formatoFecha = rawQuery.getString(0);
            this.direccion = rawQuery.getString(1);
            this.telefono = rawQuery.getString(2);
            this.taxes = rawQuery.getString(3);
            this.mensaje1 = rawQuery.getString(4);
            this.mensaje2 = rawQuery.getString(5);
            this.edtDateformat.setText(this.formatoFecha);
            this.edtAdress.setText(this.direccion);
            this.edtPhone.setText(this.telefono);
            this.edtTaxes.setText(this.taxes);
            this.edtMessage1.setText(this.mensaje1);
            this.edtMessage2.setText(this.mensaje2);
            writableDatabase.close();
            return;
        }
        crearConfiguracionBase();
        cargarDatos();
    }

    public void crearConfiguracionBase() {
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

    public void guardar() {
        SQLiteDatabase writableDatabase = new BaseDatosBills(getContext(), "registro", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        obtenerStrings();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        contentValues.put("formatofecha", this.formatoFecha);
        contentValues.put("direccion", this.direccion);
        contentValues.put("telefono", this.telefono);
        contentValues.put("taxes", this.taxes);
        contentValues.put("mensaje1", this.mensaje1);
        contentValues.put("mensaje2", this.mensaje2);
        int update = writableDatabase.update("datosbill", contentValues, "numero='1'", (String[]) null);
        writableDatabase.close();
        if (update == 1) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.toastModifiedCorrec), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        }
    }

    public void obtenerStrings() {
        this.formatoFecha = this.edtDateformat.getText().toString();
        this.direccion = this.edtAdress.getText().toString();
        this.telefono = this.edtPhone.getText().toString();
        this.taxes = this.edtTaxes.getText().toString();
        this.mensaje1 = this.edtMessage1.getText().toString();
        this.mensaje2 = this.edtMessage2.getText().toString();
    }

    public void cargarSpinner() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Select Format...");
        arrayList.add("DD-MM-YYYY");
        arrayList.add("MM-DD-YYYY");
        this.spinnerFormat.setAdapter(new ArrayAdapter(getContext(), 17367043, arrayList));
        this.spinnerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String obj = adapterView.getItemAtPosition(i).toString();
                if (obj.contains("...")) {
                    return;
                }
                if (obj.equals("DD-MM-YYYY")) {
                    SetBillFragment.this.edtDateformat.setText("24-01-2021");
                } else {
                    SetBillFragment.this.edtDateformat.setText("01-24-2021");
                }
            }
        });
    }

    public void cargarImagen() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones(getContext(), "registroConfiguraciones", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select foto, nombreImpresora from configuraciones where numero ='" + "1" + "'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            byte[] blob = rawQuery.getBlob(0);
            if (blob != null) {
                if (blob.length != 0) {
                    this.imageViewLogo.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                    writableDatabase.close();
                } else {
                    Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastLodoNoLoaded), Toast.LENGTH_SHORT);
                    makeText.setGravity(17, 0, 0);
                    makeText.show();
                    writableDatabase.close();
                }
            }
            String string = rawQuery.getString(1);
            this.nombreNegocio = string;
            if (string != null) {
                this.edtName.setText(string);
                return;
            }
            return;
        }
        Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastLodoNoLoaded), Toast.LENGTH_SHORT);
        makeText2.setGravity(17, 0, 0);
        makeText2.show();
        writableDatabase.close();
    }
}
