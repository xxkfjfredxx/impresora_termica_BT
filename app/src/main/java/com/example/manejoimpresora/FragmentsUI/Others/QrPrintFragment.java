package com.example.manejoimpresora.FragmentsUI.Others;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Impresion;
import com.example.manejoimpresora.Utilities.PrintBitmap;
import com.example.manejoimpresora.Utilities.comprobarInternet;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class QrPrintFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MODE_PRINT_IMG = 0;
    private final int ANCHO_IMG_58_MM = 384;
    private ImageView ImageQR;
    /* access modifiers changed from: private */
    public String NomImpresora;
    private Boolean QrListo;
    BluetoothAdapter bluetoothAdapter;
    /* access modifiers changed from: private */
    public String datos;
    private String estado = "No";
    private Button generar;
    private Button imprimir;
    private int printed;
    private Spinner spinnerLista;
    /* access modifiers changed from: private */
    public EditText texto;

    public static QrPrintFragment newInstance(String str, String str2) {
        QrPrintFragment qrPrintFragment = new QrPrintFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        qrPrintFragment.setArguments(bundle);
        return qrPrintFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_q_r, viewGroup, false);
        this.ImageQR = (ImageView) inflate.findViewById(R.id.imageView);
        this.texto = (EditText) inflate.findViewById(R.id.edtInformacion);
        this.generar = (Button) inflate.findViewById(R.id.butGenerar);
        this.imprimir = (Button) inflate.findViewById(R.id.butPrint);
        this.spinnerLista = (Spinner) inflate.findViewById(R.id.spinner2);
        this.QrListo = false;
        this.NomImpresora = "";
        this.printed = 0;
        this.generar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                QrPrintFragment qrPrintFragment = QrPrintFragment.this;
                String unused = qrPrintFragment.datos = qrPrintFragment.texto.getText().toString();
                QrPrintFragment qrPrintFragment2 = QrPrintFragment.this;
                qrPrintFragment2.generateCode(qrPrintFragment2.datos);
            }
        });
        this.imprimir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    QrPrintFragment.this.imprimirImagen2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cargarSuscripcion();
        ListaBluetooth();
        comprobarInternet();
        encenderBluetooth();
        return inflate;
    }

    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getActivity(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription from suscripcion where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            writableDatabase.close();
        }
    }

    public void comprobarInternet() {
        if (this.estado.equals("No") && !new comprobarInternet().isOnlineNet().booleanValue()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastInternet), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            this.imprimir.setEnabled(false);
        }
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

    public void imprimirImagen2() throws IOException {
        if (!verificarBluetooth()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.turnBluetooth2), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        } else if (!this.QrListo.booleanValue()) {
            Toast makeText2 = Toast.makeText(getActivity(), getString(R.string.ToastNoQR), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
        } else if (!this.NomImpresora.contains("...")) {
            Impresion impresion = new Impresion();
            if (!impresion.conectar(this.NomImpresora)) {
                Toast makeText3 = Toast.makeText(getActivity(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
                makeText3.setGravity(17, 0, 0);
                makeText3.show();
                return;
            }
            Bitmap bitmap = ((BitmapDrawable) this.ImageQR.getDrawable()).getBitmap();
            new PrintBitmap();
            byte[] POS_PrintBMP = PrintBitmap.POS_PrintBMP(bitmap, 384, 0);
            impresion.printNewLine();
            impresion.printNewLine();
            impresion.printText(POS_PrintBMP);
            impresion.desconectarImpresora();
            this.printed++;
            comprobarInternet();
        } else {
            Toast makeText4 = Toast.makeText(getActivity(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
            makeText4.setGravity(17, 0, 0);
            makeText4.show();
        }
    }

    /* access modifiers changed from: private */
    public void generateCode(String str) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        if (this.texto.length() != 0) {
            try {
                this.ImageQR.setImageBitmap(new BarcodeEncoder().createBitmap(multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 500, 500)));
                this.QrListo = true;
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            this.QrListo = false;
            Toast makeText = Toast.makeText(getActivity(), getString(R.string.ToastNoTextQR), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        }
    }

    public void ImprimirImagen() {
        if (!this.QrListo.booleanValue()) {
            return;
        }
        if (!this.NomImpresora.contains("...")) {
            if (!new Impresion().imprimirImagen(this.NomImpresora, ((BitmapDrawable) this.ImageQR.getDrawable()).getBitmap())) {
                Toast makeText = Toast.makeText(getActivity(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
                makeText.setGravity(17, 0, 0);
                makeText.show();
                return;
            }
            Toast makeText2 = Toast.makeText(getActivity(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
            return;
        }
        Toast makeText3 = Toast.makeText(getActivity(), getString(R.string.ToastNoQR), Toast.LENGTH_SHORT);
        makeText3.setGravity(17, 0, 0);
        makeText3.show();
    }

    public void ListaBluetooth() {
        ArrayList arrayList = new ArrayList();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Toast.makeText(getActivity(), "No se encuentra adaptador bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (this.bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), Toast.LENGTH_SHORT);
        }
        Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
        arrayList.add(getString(R.string.SelectPRint));
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice next : bondedDevices) {
                arrayList.add(next.getName());
                next.getAddress();
            }
        }
        this.spinnerLista.setAdapter(new ArrayAdapter(getActivity(), 17367043, arrayList));
        this.spinnerLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String unused = QrPrintFragment.this.NomImpresora = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            ListaBluetooth();
        }
    }
}
