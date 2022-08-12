package com.example.manejoimpresora.FragmentsUI.Others;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Impresion;
import com.example.manejoimpresora.Utilities.OpenPdf;
import com.example.manejoimpresora.Utilities.PrintBitmap;
import com.example.manejoimpresora.Utilities.TransformacionRotarBitmap;
import com.example.manejoimpresora.Utilities.comprobarInternet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHalftoneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter;

public class PdfPrintFragment extends Fragment {
    private static final int MODE_PRINT_IMG = 0;
    private static final int READ_REQUEST_CODE = 42;
    private final int ANCHO_IMG_58_MM = 384;
    /* access modifiers changed from: private */
    public String NomImpresora;
    /* access modifiers changed from: private */
    public Boolean PhotoLista;
    private BluetoothAdapter bluetoothAdapter;
    private Boolean bthOn;
    private Button btnNext;
    private Button btnPrevious;
    private Button buscar;
    private ImageButton butGirar;
    private String estado = "No";
    /* access modifiers changed from: private */
    public Bitmap imageOriginal;
    private Button imprimir;
    /* access modifiers changed from: private */
    public ImageView mm;
    /* access modifiers changed from: private */
    public int pageNumber;
    private Spinner spinnerLista;
    private Spinner spinnerefec;
    /* access modifiers changed from: private */
    public int totalPag;
    /* access modifiers changed from: private */
    public Uri uri;

    static /* synthetic */ int access$108(PdfPrintFragment pdfPrintFragment) {
        int i = pdfPrintFragment.pageNumber;
        pdfPrintFragment.pageNumber = i + 1;
        return i;
    }

    static /* synthetic */ int access$110(PdfPrintFragment pdfPrintFragment) {
        int i = pdfPrintFragment.pageNumber;
        pdfPrintFragment.pageNumber = i - 1;
        return i;
    }

    public static PdfPrintFragment newInstance(String str, String str2) {
        PdfPrintFragment pdfPrintFragment = new PdfPrintFragment();
        pdfPrintFragment.setArguments(new Bundle());
        return pdfPrintFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_pdf_printing, viewGroup, false);
        cargarSuscripcion();
        this.mm = (ImageView) inflate.findViewById(R.id.imageView2);
        this.buscar = (Button) inflate.findViewById(R.id.butGenerar);
        this.imprimir = (Button) inflate.findViewById(R.id.butPrint);
        this.btnNext = (Button) inflate.findViewById(R.id.butNext);
        this.btnPrevious = (Button) inflate.findViewById(R.id.butPrevious);
        this.butGirar = (ImageButton) inflate.findViewById(R.id.imgButRo);
        this.spinnerLista = (Spinner) inflate.findViewById(R.id.spinner2);
        this.spinnerefec = (Spinner) inflate.findViewById(R.id.spinner4);
        this.PhotoLista = false;
        this.bthOn = false;
        this.pageNumber = 0;
        this.totalPag = 0;
        cargaImaExter();
        ListaBluetooth();
        CargarSpinnerEfecto();
        comprobarInternet();
        encenderBluetooth();
        this.buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PdfPrintFragment.this.buscarPdf();
            }
        });
        this.btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PdfPrintFragment.this.totalPag - 1 > PdfPrintFragment.this.pageNumber) {
                    PdfPrintFragment.access$108(PdfPrintFragment.this);
                    try {
                        Bitmap openRenderer = new OpenPdf().openRenderer(PdfPrintFragment.this.getContext(), PdfPrintFragment.this.uri, PdfPrintFragment.this.pageNumber);
                        PdfPrintFragment.this.mm.setImageBitmap(openRenderer);
                        Bitmap unused = PdfPrintFragment.this.imageOriginal = openRenderer;
                        Boolean unused2 = PdfPrintFragment.this.PhotoLista = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.btnPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PdfPrintFragment.this.totalPag > PdfPrintFragment.this.pageNumber && PdfPrintFragment.this.pageNumber > 0) {
                    PdfPrintFragment.access$110(PdfPrintFragment.this);
                    try {
                        Bitmap openRenderer = new OpenPdf().openRenderer(PdfPrintFragment.this.getContext(), PdfPrintFragment.this.uri, PdfPrintFragment.this.pageNumber);
                        PdfPrintFragment.this.mm.setImageBitmap(openRenderer);
                        Bitmap unused = PdfPrintFragment.this.imageOriginal = openRenderer;
                        Boolean unused2 = PdfPrintFragment.this.PhotoLista = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.imprimir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    PdfPrintFragment.this.imprimirImagen2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.butGirar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PdfPrintFragment.this.PhotoLista.booleanValue()) {
                    PdfPrintFragment.this.girarImagen();
                }
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
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastInternet), Toast.LENGTH_SHORT);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            this.imprimir.setEnabled(false);
        }
    }

    public void CargarSpinnerEfecto() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Normal");
        arrayList.add("Halftone");
        arrayList.add("Sketch");
        arrayList.add("Toon");
        arrayList.add("CroossHatch");
        this.spinnerefec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                PdfPrintFragment.this.efectoImagen(adapterView.getItemAtPosition(i).toString());
            }
        });
        this.spinnerefec.setAdapter(new ArrayAdapter(getActivity(), 17367043, arrayList));
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
            Toast.makeText(getActivity(), "No se encuentra adaptador bluetooth", Toast.LENGTH_LONG).show();
        }
        if (this.bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
        }
        Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
        arrayList.add(getString(R.string.SelectPRint));
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice name : bondedDevices) {
                arrayList.add(name.getName());
            }
        }
        this.spinnerLista.setAdapter(new ArrayAdapter(getActivity(), 17367043, arrayList));
        this.spinnerLista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String unused = PdfPrintFragment.this.NomImpresora = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    public void efectoImagen(String str) {
        boolean z = true;
        if (this.PhotoLista.booleanValue()) {
            Bitmap bitmap = this.imageOriginal;
            GPUImage gPUImage = new GPUImage(getContext());
            gPUImage.setImage(bitmap);
            char c = 65535;
            switch (str.hashCode()) {
                case -1955878649:
                    if (str.equals("Normal")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1816807476:
                    if (str.equals("Sketch")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1599332303:
                    if (str.equals("CroossHatch")) {
                        c = 4;
                        break;
                    }
                    break;
                case 2612666:
                    if (str.equals("Toon")) {
                        c = 3;
                        break;
                    }
                    break;
                case 11549765:
                    if (str.equals("Halftone")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            if (c != 0) {
                if (c == 1) {
                    GPUImageHalftoneFilter gPUImageHalftoneFilter = new GPUImageHalftoneFilter();
                    gPUImageHalftoneFilter.setFractionalWidthOfAPixel(1.25062E-5f);
                    gPUImage.setFilter(gPUImageHalftoneFilter);
                } else if (c == 2) {
                    gPUImage.setFilter(new GPUImageSketchFilter());
                } else if (c == 3) {
                    gPUImage.setFilter(new GPUImageToonFilter());
                } else if (c == 4) {
                    GPUImageCrosshatchFilter gPUImageCrosshatchFilter = new GPUImageCrosshatchFilter();
                    gPUImageCrosshatchFilter.setCrossHatchSpacing(0.01532f);
                    gPUImageCrosshatchFilter.setCrossHatchSpacing(0.009f);
                    gPUImage.setFilter(gPUImageCrosshatchFilter);
                }
                z = false;
            } else {
                this.mm.setImageBitmap(this.imageOriginal);
            }
            if (!z) {
                this.mm.setImageBitmap(gPUImage.getBitmapWithFilterApplied());
            }
        }
    }

    public void girarImagen() {
        this.mm.setImageBitmap(new TransformacionRotarBitmap(getContext(), Float.parseFloat("90")).transform((BitmapPool) null, ((BitmapDrawable) this.mm.getDrawable()).getBitmap(), 0, 0));
    }

    public boolean verificarBluetooth() {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            return true;
        }
        encenderBluetooth();
        return false;
    }

    public void imprimirImagen2() throws IOException {
        if (!verificarBluetooth()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.turnBluetooth2), Toast.LENGTH_LONG);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        } else if (!this.PhotoLista.booleanValue()) {
            Toast makeText2 = Toast.makeText(getContext(), getString(R.string.ToastNoImage), Toast.LENGTH_LONG);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
        } else if (!this.NomImpresora.contains("...")) {
            Impresion impresion = new Impresion();
            if (impresion.conectarImpresora(this.NomImpresora).booleanValue()) {
                this.mm.buildDrawingCache();
                Bitmap drawingCache = this.mm.getDrawingCache();
                PrintBitmap printBitmap = new PrintBitmap();
                byte[] bitmapTobyte = printBitmap.bitmapTobyte(printBitmap.reSize(drawingCache, 384, 0));
                impresion.printNewLine();
                impresion.printNewLine();
                impresion.printText(bitmapTobyte);
                impresion.printNewLine();
                impresion.printNewLine();
                this.mm.destroyDrawingCache();
                comprobarInternet();
                impresion.desconectarImpresora();
                return;
            }
            Toast makeText3 = Toast.makeText(getContext(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_LONG);
            makeText3.setGravity(17, 0, 0);
            makeText3.show();
        } else {
            Toast makeText4 = Toast.makeText(getContext(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_LONG);
            makeText4.setGravity(17, 0, 0);
            makeText4.show();
        }
    }

    public void cargaImaExter() {
        if (getArguments() != null) {
            this.uri = Uri.parse(getArguments().getString("URI"));
            OpenPdf openPdf = new OpenPdf();
            try {
                this.totalPag = openPdf.numberPages(getContext(), this.uri);
                Bitmap openRenderer = openPdf.openRenderer(getContext(), this.uri, 0);
                this.mm.setImageBitmap(openRenderer);
                this.imageOriginal = openRenderer;
                this.PhotoLista = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void buscarPdf() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("application/pdf");
        startActivityForResult(intent, 42);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 42 && i2 == -1 && intent != null) {
            this.uri = null;
            this.uri = intent.getData();
            OpenPdf openPdf = new OpenPdf();
            try {
                this.totalPag = openPdf.numberPages(getContext(), this.uri);
                Bitmap reSize = new PrintBitmap().reSize(openPdf.openRenderer(getContext(), this.uri, 0), 384, 0);
                this.mm.setImageBitmap(reSize);
                this.imageOriginal = reSize;
                this.PhotoLista = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (i == 1 && i2 == -1) {
            ListaBluetooth();
        }
    }
}
