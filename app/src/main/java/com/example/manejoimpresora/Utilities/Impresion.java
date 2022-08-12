package com.example.manejoimpresora.Utilities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class Impresion {
    byte FONT_TYPE;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    InputStream inputStream;
    OutputStream outputStream;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    Thread thread;

    public boolean conectar(String str) {
        try {
            FindBluetoothDevice(str);
            openBluetoothPrinter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.bluetoothSocket.isConnected();
    }

    public boolean imprimirImagen(String str, Bitmap bitmap) {
        try {
            FindBluetoothDevice(str);
            openBluetoothPrinter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!this.bluetoothSocket.isConnected()) {
            return false;
        }
        try {
            printPhotos(bitmap);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            disconnectBT();
            return true;
        } catch (Exception e3) {
            e3.printStackTrace();
            return true;
        }
    }

    public void desconectarImpresora() {
        if (this.bluetoothSocket.isConnected()) {
            try {
                disconnectBT();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean conectarImpresora(String str) {
        try {
            FindBluetoothDevice(str);
            openBluetoothPrinter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.bluetoothSocket.isConnected()) {
            return true;
        }
        return false;
    }

    public void FindBluetoothDevice(String str) {
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.bluetoothAdapter = defaultAdapter;
            if (defaultAdapter.isEnabled()) {
                new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            }
            Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice next : bondedDevices) {
                    if (next.getName().equals(str)) {
                        this.bluetoothDevice = next;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                    while (!Thread.currentThread().isInterrupted() && !Impresion.this.stopWorker) {
                        try {
                            int available = Impresion.this.inputStream.available();
                            if (available > 0) {
                                byte[] bArr = new byte[available];
                                Impresion.this.inputStream.read(bArr);
                                for (int i = 0; i < available; i++) {
                                    byte b = bArr[i];
                                    if (b == 10) {
                                        int i2 = Impresion.this.readBufferPosition;
                                        byte[] bArr2 = new byte[i2];
                                        System.arraycopy(Impresion.this.readBuffer, 0, bArr2, 0, i2);
                                        new String(bArr2, "US-ASCII");
                                        Impresion.this.readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    } else {
                                        byte[] bArr3 = Impresion.this.readBuffer;
                                        Impresion impresion = Impresion.this;
                                        int i3 = impresion.readBufferPosition;
                                        impresion.readBufferPosition = i3 + 1;
                                        bArr3[i3] = b;
                                    }
                                }
                            }
                        } catch (Exception unused) {
                            Impresion.this.stopWorker = true;
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

    public void printCustom(String str, int i, int i2) {
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
                } catch (IOException e) {
                    e.printStackTrace();
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
        }catch (Exception e){

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

    public void printNewLine() {
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

    public void fontA() throws IOException {
        this.outputStream.write(PrinterCommands.SELECT_FONT_B);
    }

    public void printPhotoPrueba(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                printTextPrueba(Utils.decodeBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        } else {
            Log.e("Print Photo error", "the file isn't exists");
        }
    }

    private void printTextPrueba(byte[] bArr) {
        try {
            this.outputStream.write(bArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void printBill() {
        OutputStream outputStream2;
        try {
            outputStream2 = this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            outputStream2 = null;
        }
        this.outputStream = outputStream2;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        OutputStream outputStream3 = null;
        try {
            outputStream3 = this.bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.outputStream = outputStream3;
        try {
            outputStream3.write(new byte[]{PrinterCommands.ESC, 33, 3});
        } catch (IOException e) {
            e.printStackTrace();
        }
        printCustom("GUERRERO Y ASOCIADOS", 1, 1);
        printCustom("Sociedad de Derecho Ltda.", 1, 1);
        printCustom("Calle 102 # 10-26, Bogota,Col", 0, 1);
        printCustom("Tel: 8546758", 0, 1);
        printCustom("Nit: 937748-0", 0, 1);
        String[] dateTime = getDateTime();
        printText(leftRightAlign(dateTime[0], dateTime[1]));
        printNewLine();
        printText("Servicio Asesoria$100.000 ");
        printCustom(new String(new char[32]).replace("\u0000", "."), 0, 1);
        printText(leftRightAlign("Total", "$ 100.000"));
        printNewLine();
        printCustom("Muchas Gracias por su visita", 0, 1);
        printCustom("Siempre a su servicio", 0, 1);
        printNewLine();
        printNewLine();
        try {
            this.outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printPhotos(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                byte[] decodeBitmap = Utils.decodeBitmap(bitmap);
                this.outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                this.outputStream.write(decodeBitmap);
                printText(decodeBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PrintTools", "the file isn't exists");
            }
        } else {
            Log.e("Print Photo error", "the file isn't exists");
        }
    }

    private void printText(String str) {
        try {
            this.outputStream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printText(byte[] bArr) {
        try {
            this.outputStream.write(bArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imprimirLeft() throws IOException {
        try {
            this.outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
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
}
