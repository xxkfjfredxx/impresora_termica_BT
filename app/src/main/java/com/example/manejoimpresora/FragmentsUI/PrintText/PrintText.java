package com.example.manejoimpresora.FragmentsUI.PrintText;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.example.manejoimpresora.DataBases.BaseDatosSettings;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.PrinterCommands;
import com.example.manejoimpresora.Utilities.comprobarInternet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class PrintText extends Fragment {
    byte FONT_TYPE;
    /* access modifiers changed from: private */
    public String NomImpresora;
    int b = 0;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private RadioButton center;
    private String estado = "No";
    private Button imprimirTexto;
    /* access modifiers changed from: private */
    public InputStream inputStream;
    private RadioButton left;
    private int numeroImpres;
    private OutputStream outputStream;
    private PrintTextViewModel printTextViewModel;
    int printed;
    byte[] readBuffer;
    int readBufferPosition;
    private RadioButton rigth;
    private Spinner spinnerLista;
    private Spinner spnAlto;
    private Spinner spnAncho;
    private Spinner spnNegrita;
    volatile boolean stopWorker;
    /* access modifiers changed from: private */
    public String textAligment;
    /* access modifiers changed from: private */
    public EditText textBox;
    private Thread thread;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_gallery, viewGroup, false);
        this.textBox = (EditText) inflate.findViewById(R.id.txtText);
        this.spinnerLista = (Spinner) inflate.findViewById(R.id.spinner);
        this.left = (RadioButton) inflate.findViewById(R.id.radioLeft);
        this.center = (RadioButton) inflate.findViewById(R.id.radioCenter);
        this.rigth = (RadioButton) inflate.findViewById(R.id.radioRight);
        this.spnNegrita = (Spinner) inflate.findViewById(R.id.spn_negrita);
        this.spnAncho = (Spinner) inflate.findViewById(R.id.spn_ancho);
        this.spnAlto = (Spinner) inflate.findViewById(R.id.spn_alto);
        String numbers[] = {"1","2","3"};
        this.spnAncho.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, numbers));
        this.spnAlto.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, numbers));
        this.NomImpresora = "Select printer...";
        this.textAligment = "left";
        this.printed = 0;
        this.left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintText.this.textBox.setGravity(3);
                String unused = PrintText.this.textAligment = "left";
            }
        });
        this.center.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintText.this.textBox.setGravity(1);
                String unused = PrintText.this.textAligment = "center";
            }
        });
        this.rigth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintText.this.textBox.setGravity(5);
                String unused = PrintText.this.textAligment = "right";
            }
        });
        if (getArguments() != null) {
            this.textBox.setText(getArguments().getString("Text"));
        }
        Button button = (Button) inflate.findViewById(R.id.btnPrint);
        this.imprimirTexto = button;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PrintText.this.imprimirTxt();
            }
        });
        cargarSuscripcion();
        ListaBluetooth();
        comprobarInternet();
        crearConfiguracionBase();
        cargarPrints();
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

    public void crearConfiguracionBase() {
        SQLiteDatabase writableDatabase = new BaseDatosSettings(getContext(), "settings", null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        writableDatabase.insert("configuraciones", null, contentValues);
        writableDatabase.close();
    }

    public void cargarPrints() {
        if (this.estado.equals("No")) {
            crearConfiguracionBase();
            SQLiteDatabase writableDatabase = new BaseDatosSettings(getActivity(), "settings", null, 1).getWritableDatabase();
            Cursor rawQuery = writableDatabase.rawQuery("select numPrints from configuraciones where numero ='1'", null);
            if (rawQuery.moveToFirst()) {
                String string = rawQuery.getString(0);
                if (string != null) {
                    this.numeroImpres = Integer.parseInt(string);
                }
                writableDatabase.close();
            }
        }
    }

    public void restarPrints() {
        if (this.estado.equals("No")) {
            SQLiteDatabase writableDatabase = new BaseDatosSettings(getContext(), "settings", null, 1).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", "1");
            contentValues.put("numPrints", Integer.valueOf(this.numeroImpres - 1));
            writableDatabase.update("configuraciones", contentValues, "numero='1'", null);
            writableDatabase.close();
            cargarPrints();
        }
    }

    public void comprobarInternet() {
        if (this.estado.equals("No") && !new comprobarInternet().isOnlineNet().booleanValue()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastInternet), Toast.LENGTH_LONG);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            this.imprimirTexto.setEnabled(false);
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

    public void imprimirTxt() {
        if (!verificarBluetooth()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.turnBluetooth2), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        } else if (this.NomImpresora.contains("...")) {
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.SelectPRint), Toast.LENGTH_SHORT);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
        } else if (!this.textBox.getText().toString().equals("")) {
            boolean equals = this.spnNegrita.getSelectedItem().toString().equals("Yes");
            int parseInt = Integer.parseInt(this.spnAncho.getSelectedItem().toString());
            int parseInt2 = Integer.parseInt(this.spnAlto.getSelectedItem().toString());
            String obj = this.textBox.getText().toString();
            try {
                FindBluetoothDevice();
                openBluetoothPrinter();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.bluetoothSocket.isConnected()) {
                try {
                    this.outputStream.write(28);
                    this.outputStream.write(46);
                    this.outputStream.write(27);
                    this.outputStream.write(116);
                    this.outputStream.write(16);
                    String str = this.textAligment;
                    char c = 65535;
                    int hashCode = str.hashCode();
                    if (hashCode != -1364013995) {
                        if (hashCode != 3317767) {
                            if (hashCode == 108511772) {
                                if (str.equals("right")) {
                                    c = 2;
                                }
                            }
                        } else if (str.equals("left")) {
                            c = 0;
                        }
                    } else if (str.equals("center")) {
                        c = 1;
                    }
                    if (c == 0) {
                        this.outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    } else if (c == 1) {
                        this.outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    } else if (c != 2) {
                        this.outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    } else {
                        this.outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    }
                    this.outputStream.write(getByteString(obj, equals ? 1 : 0, 0, parseInt, parseInt2));
                    this.outputStream.write("\n\n".getBytes());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                    disconnectBT();
                    this.printed++;
                    comprobarInternet();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            } else {
                Toast makeText3 = Toast.makeText(getContext(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
                makeText3.setGravity(17, 0, 0);
                makeText3.show();
            }
        } else {
            Toast makeText4 = Toast.makeText(getActivity(), getString(R.string.NoData), Toast.LENGTH_SHORT);
            makeText4.setGravity(17, 0, 0);
            makeText4.show();
        }
    }

    public static byte[] getByteString(String str, int i, int i2, int i3, int i4) {
        if (((str.length() == 0) | (i3 < 0) | (i3 > 3) | (i4 < 0) | (i4 > 3) | (i2 < 0)) || (i2 > 1)) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes("iso-8859-1");
            byte[] bArr = new byte[(bytes.length + 9)];
            byte[] bArr2 = {0, PrinterCommands.DLE, 32, 48};
            bArr[0] = PrinterCommands.ESC;
            bArr[1] = 69;
            bArr[2] = (byte) i;
            bArr[3] = PrinterCommands.ESC;
            bArr[4] = 77;
            bArr[5] = (byte) i2;
            bArr[6] = PrinterCommands.GS;
            bArr[7] = 33;
            bArr[8] = (byte) (bArr2[i3] + new byte[]{0, 1, 2, 3}[i4]);
            System.arraycopy(bytes, 0, bArr, 9, bytes.length);
            return bArr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void ListaBluetooth() {
        ArrayList arrayList = new ArrayList();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Toast makeText = Toast.makeText(getActivity(), "No se encuentra adaptador bluetooth", Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
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
        this.spinnerLista.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, arrayList));
        this.spinnerLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String unused = PrintText.this.NomImpresora = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void FindBluetoothDevice() {
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.bluetoothAdapter = defaultAdapter;
            if (defaultAdapter == null) {
                Toast makeText = Toast.makeText(getActivity(), "No se encuentra adaptador bluetooth", 0);
                makeText.setGravity(17, 0, 0);
                makeText.show();
            }
            if (this.bluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
            }
            Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice next : bondedDevices) {
                    if (next.getName().equals(this.NomImpresora)) {
                        this.bluetoothDevice = next;
                        this.b = 1;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Impresora Bluetooth no encontrada", 0).show();
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
            beginListenData();
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public void beginListenData() {
        try {
            final Handler handler = new Handler();
            this.stopWorker = false;
            this.readBufferPosition = 0;
            this.readBuffer = new byte[1024];
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !PrintText.this.stopWorker) {
                        try {
                            int available = PrintText.this.inputStream.available();
                            if (available > 0) {
                                byte[] bArr = new byte[available];
                                PrintText.this.inputStream.read(bArr);
                                for (int i = 0; i < available; i++) {
                                    byte b = bArr[i];
                                    if (b == 10) {
                                        int i2 = PrintText.this.readBufferPosition;
                                        byte[] bArr2 = new byte[i2];
                                        System.arraycopy(PrintText.this.readBuffer, 0, bArr2, 0, i2);
                                        final String str = new String(bArr2, "US-ASCII");
                                        PrintText.this.readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(PrintText.this.getActivity(), str, 0).show();
                                            }
                                        });
                                    } else {
                                        byte[] bArr3 = PrintText.this.readBuffer;
                                        PrintText printText = PrintText.this;
                                        int i3 = printText.readBufferPosition;
                                        printText.readBufferPosition = i3 + 1;
                                        bArr3[i3] = b;
                                    }
                                }
                            }
                        } catch (Exception unused) {
                            PrintText.this.stopWorker = true;
                        }
                    }
                }
            });
            this.thread = thread2;
            thread2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: package-private */
    public void disconnectBT() throws IOException {
        try {
            this.stopWorker = true;
            this.outputStream.close();
            this.inputStream.close();
            this.bluetoothSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printUnicode() {
        try {
            this.outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void printNewLine() {
        try {
            this.outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetPrint() {
        try {
            this.outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            this.outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            this.outputStream.write(PrinterCommands.FS_FONT_ALIGN);
            this.outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            this.outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            this.outputStream.write(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printText(String str) {
        try {
            this.outputStream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printText(byte[] bArr) {
        try {
            this.outputStream.write(bArr);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String leftRightAlign(String str, String str2) {
        String str3 = str + str2;
        if (str3.length() >= 31) {
            return str3;
        }
        int length = (31 - str.length()) + str2.length();
        return str + new String(new char[length]).replace("\u0000", " ") + str2;
    }

    private String[] getDateTime() {
        Calendar instance = Calendar.getInstance();
        return new String[]{instance.get(5) + "/" + instance.get(2) + "/" + instance.get(1), instance.get(11) + ":" + instance.get(12)};
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            ListaBluetooth();
        }
    }
}
