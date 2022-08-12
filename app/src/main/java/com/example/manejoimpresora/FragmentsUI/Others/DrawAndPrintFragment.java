package com.example.manejoimpresora.FragmentsUI.Others;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Impresion;
import com.example.manejoimpresora.Utilities.PrintBitmap;
import com.example.manejoimpresora.Utilities.comprobarInternet;

import java.util.ArrayList;
import java.util.Set;

public class DrawAndPrintFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int COD_PERMISOS = 426;
    private static final int MODE_PRINT_IMG = 0;
    private final int ANCHO_IMG_58_MM = 384;
    /* access modifiers changed from: private */
    public String NomImpresora;
    private Button bImprimir;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    private Button boton;
    RelativeLayout dibujo;
    private String estado = "No";
    private ImageView imag;
    private LinearLayout layoutProgresoImagen;
    private String mParam1;
    private String mParam2;
    private String ruta;
    private Spinner spinnerLista;
    private TextView ver;

    public static DrawAndPrintFragment newInstance(String str, String str2) {
        DrawAndPrintFragment drawAndPrintFragment = new DrawAndPrintFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        drawAndPrintFragment.setArguments(bundle);
        return drawAndPrintFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_draw_and_print, viewGroup, false);
        cargarSuscripcion();
        this.imag = (ImageView) inflate.findViewById(R.id.imageView3);
        RelativeLayout relativeLayout = (RelativeLayout) inflate.findViewById(R.id.rect);
        this.dibujo = relativeLayout;
        relativeLayout.addView(new Vista(getActivity()));
        this.spinnerLista = (Spinner) inflate.findViewById(R.id.spinner3);
        this.ver = (TextView) inflate.findViewById(R.id.textView9);
        this.boton = (Button) inflate.findViewById(R.id.button);
        this.boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DrawAndPrintFragment.this.imprimirDibujo();
            }
        });
        ListaBluetooth();
        comprobarInternet();
        this.ver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //DrawAndPrintFragment.this.cargarCalificacion();
            }
        });
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
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastInternet), Toast.LENGTH_LONG);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            this.boton.setEnabled(false);
        }
    }

    public void ListaBluetooth() {
        ArrayList arrayList = new ArrayList();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Toast.makeText(getActivity(), "No se encuentra adaptador bluetooth", 0).show();
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
        this.spinnerLista.setAdapter(new ArrayAdapter(getActivity(), 17367043, arrayList));
        this.spinnerLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String unused = DrawAndPrintFragment.this.NomImpresora = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    public void imprimirDibujo() {
        Bitmap createBitmap = Bitmap.createBitmap(1000, 1680, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.dibujo.layout(0, 0, 1000, 1680);
        Drawable foreground = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            foreground = this.dibujo.getForeground();
        }
        if (foreground != null) {
            foreground.draw(canvas);
        } else {
            this.dibujo.draw(canvas);
        }
        this.imag.setImageBitmap(createBitmap);
        imprimir();
    }

    public void imprimir() {
        if (!this.NomImpresora.contains("...")) {
            Impresion impresion = new Impresion();
            if (impresion.conectarImpresora(this.NomImpresora).booleanValue()) {
                Bitmap bitmap = ((BitmapDrawable) this.imag.getDrawable()).getBitmap();
                new PrintBitmap();
                byte[] POS_PrintBMP = PrintBitmap.POS_PrintBMP(bitmap, 384, 0);
                impresion.printNewLine();
                impresion.printNewLine();
                impresion.printText(POS_PrintBMP);
                impresion.desconectarImpresora();
                comprobarInternet();
                return;
            }
            Toast makeText = Toast.makeText(getActivity(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            return;
        }
        Toast makeText2 = Toast.makeText(getActivity(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
        makeText2.setGravity(17, 0, 0);
        makeText2.show();
    }

    private class Vista extends View {
        String accion = "accion";
        Path path = new Path();
        float x = 100.0f;
        float y = 100.0f;

        public Vista(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8.0f);
            paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            canvas.getWidth();
            canvas.drawPath(this.path, paint);
            if (this.accion == "down") {
                this.path.moveTo(this.x, this.y);
            }
            if (this.accion == "move") {
                this.path.lineTo(this.x, this.y);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            this.x = motionEvent.getX();
            this.y = motionEvent.getY();
            if (motionEvent.getAction() == 0) {
                this.accion = "down";
            }
            if (motionEvent.getAction() == 2) {
                this.accion = "move";
            }
            invalidate();
            return true;
        }
    }
}
