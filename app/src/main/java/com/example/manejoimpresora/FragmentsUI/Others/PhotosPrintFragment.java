package com.example.manejoimpresora.FragmentsUI.Others;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.manejoimpresora.DataBases.BaseDatosSettings;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.FotoDeCamara;
import com.example.manejoimpresora.Utilities.GalleryPhoto;
import com.example.manejoimpresora.Utilities.Impresion;
import com.example.manejoimpresora.Utilities.PrintBitmap;
import com.example.manejoimpresora.Utilities.TransformacionRotarBitmap;
import com.example.manejoimpresora.Utilities.comprobarInternet;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;


import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHalftoneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter;

public class PhotosPrintFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int INTENT_CAMARA = 123;
    private static final int INTENT_GALERIA = 321;
    private static final int MODE_PRINT_IMG = 0;
    private static final int READ_REQUEST_CODE = 42;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int ANCHO_IMG_58_MM = 384;
    private final String CARPETA_RAIZ = "misImagenesPrueba";
    /* access modifiers changed from: private */
    public String NomImpresora;
    private Boolean PhotoLista;
    private final String RUTA_IMAGEN = "misImagenesPrueba/misFotos";
    private BluetoothAdapter bluetoothAdapter;
    private Button btnScanner;
    private Button buscar;
    /* access modifiers changed from: private */
    public FotoDeCamara cameraPhoto;
    private String currentPhotoPath;
    private String datos;
    private String direccion;
    private String estado = "No";
    private GalleryPhoto galleryPhoto;
    private Bitmap imageOriginal;
    private Button imprimir;
    private Button imprimirOtro;
    private InputStream inputStream;
    private String mParam1;
    private String mParam2;
    private int numeroImpres;
    private OutputStream outputStream;
    private String path;
    int photo;
    private ImageView photoSelected;
    private Spinner spiEfect;
    private Spinner spinnerLista;
    private EditText texto;

    public static PhotosPrintFragment newInstance(String str, String str2) {
        PhotosPrintFragment photosPrintFragment = new PhotosPrintFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        photosPrintFragment.setArguments(bundle);
        return photosPrintFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_photos_printing, viewGroup, false);
        this.photoSelected = (ImageView) inflate.findViewById(R.id.imageView);
        this.texto = (EditText) inflate.findViewById(R.id.edtInformacion);
        this.buscar = (Button) inflate.findViewById(R.id.butGenerar);
        this.imprimir = (Button) inflate.findViewById(R.id.butPrint);
        this.spinnerLista = (Spinner) inflate.findViewById(R.id.spinner2);
        this.spiEfect = (Spinner) inflate.findViewById(R.id.spinnerEfectos);
        this.NomImpresora = "";
        this.PhotoLista = false;
        this.buscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PhotosPrintFragment.this.BuscarImagen2();
            }
        });
        this.imprimir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    PhotosPrintFragment.this.imprimirImagen2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cargarSuscripcion();
        encenderBluetooth();
        ListaBluetooth();
        CargarSpinnerEfecto();
        comprobarInternet();
        crearConfiguracionBase();
        cargarPrints();
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
        SQLiteDatabase writableDatabase = new BaseDatosSettings(getContext(), "settings", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        writableDatabase.insert("configuraciones", (String) null, contentValues);
        writableDatabase.close();
    }

    public void cargarPrints() {
        if (this.estado.equals("No")) {
            crearConfiguracionBase();
            SQLiteDatabase writableDatabase = new BaseDatosSettings(getActivity(), "settings", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
            Cursor rawQuery = writableDatabase.rawQuery("select numPrints from configuraciones where numero ='1'", (String[]) null);
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
            SQLiteDatabase writableDatabase = new BaseDatosSettings(getContext(), "settings", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", "1");
            contentValues.put("numPrints", Integer.valueOf(this.numeroImpres - 1));
            writableDatabase.update("configuraciones", contentValues, "numero='1'", (String[]) null);
            writableDatabase.close();
            cargarPrints();
        }
    }

    public void CargarSpinnerEfecto() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Normal");
        arrayList.add("Halftone");
        arrayList.add("Sketch");
        arrayList.add("Toon");
        arrayList.add("CroossHatch");
        this.spiEfect.setAdapter(new ArrayAdapter(getActivity(), 17367043, arrayList));
        this.spiEfect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                PhotosPrintFragment.this.efectoImagen(adapterView.getItemAtPosition(i).toString());
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
                this.photoSelected.setImageBitmap(this.imageOriginal);
            }
            if (!z) {
                this.photoSelected.setImageBitmap(gPUImage.getBitmapWithFilterApplied());
            }
        }
    }

    public void comprobarInternet() {
        if (this.estado.equals("No") && !new comprobarInternet().isOnlineNet().booleanValue()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.ToastInternet), 1);
            makeText.setGravity(17, 0, 0);
            makeText.show();
            this.imprimir.setEnabled(false);
        }
    }

    private void rotarImagen(View view) {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(2);
        view.startAnimation(rotateAnimation);
    }

    public void imprimirImagen2() throws IOException {
        if (!verificarBluetooth()) {
            Toast makeText = Toast.makeText(getContext(), getString(R.string.turnBluetooth2), 0);
            makeText.setGravity(17, 0, 0);
            makeText.show();
        } else if (!this.PhotoLista.booleanValue()) {
            Toast makeText2 = Toast.makeText(getActivity(), getString(R.string.ToastNoImage), 0);
            makeText2.setGravity(17, 0, 0);
            makeText2.show();
        } else if (!this.NomImpresora.contains("...")) {
            Impresion impresion = new Impresion();
            if (impresion.conectarImpresora(this.NomImpresora).booleanValue()) {
                byte[] bitmapTobyte = new PrintBitmap().bitmapTobyte(new TransformacionRotarBitmap(getContext(), Float.parseFloat("0")).transform((BitmapPool) null, ((BitmapDrawable) this.photoSelected.getDrawable()).getBitmap(), 0, 0));
                impresion.printNewLine();
                impresion.printNewLine();
                impresion.printText(bitmapTobyte);
                if (this.numeroImpres <= 0) {

                } else {
                    restarPrints();
                }
                impresion.desconectarImpresora();
                return;
            }
            Toast makeText3 = Toast.makeText(getContext(), getString(R.string.ToastTurnOnPrinter), Toast.LENGTH_SHORT);
            makeText3.setGravity(17, 0, 0);
            makeText3.show();
        } else {
            Toast makeText4 = Toast.makeText(getActivity(), getString(R.string.ToastSelectPrinter), Toast.LENGTH_SHORT);
            makeText4.setGravity(17, 0, 0);
            makeText4.show();
        }
    }

    public void ListaBluetooth() {
        ArrayList arrayList = new ArrayList();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Toast.makeText(getActivity(), "No se encuentra adaptador bluetooth", Toast.LENGTH_SHORT).show();
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
                String unused = PhotosPrintFragment.this.NomImpresora = adapterView.getItemAtPosition(i).toString();
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
            this.photoSelected.setImageURI(data);
            this.photoSelected.setImageBitmap(new PrintBitmap().reSize(((BitmapDrawable) this.photoSelected.getDrawable()).getBitmap(), 384, 0));
            this.imageOriginal = ((BitmapDrawable) this.photoSelected.getDrawable()).getBitmap();
            this.PhotoLista = true;
        }
        if (i == 1 && i2 == -1) {
            this.photoSelected.setImageBitmap(new PrintBitmap().reSize((Bitmap) intent.getExtras().get("data"), 384, 0));
            this.imageOriginal = ((BitmapDrawable) this.photoSelected.getDrawable()).getBitmap();
            this.PhotoLista = true;
        }
        if (i == 2 && i2 == -1) {
            ListaBluetooth();
        }
    }

    /* access modifiers changed from: private */
    public void tomarFoto() {
        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.CAMERA") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.CAMERA"}, 0);
            return;
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    public void BuscarImagen2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage((CharSequence) "choose where you want to get the image from, elija de donde quiere obtener la imagen").setPositiveButton((CharSequence) "CAMERA", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PhotosPrintFragment photosPrintFragment = PhotosPrintFragment.this;
                FotoDeCamara unused = photosPrintFragment.cameraPhoto = new FotoDeCamara(photosPrintFragment.getContext());
                PhotosPrintFragment.this.tomarFoto();
            }
        }).setNegativeButton((CharSequence) "GALLERY", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PhotosPrintFragment.this.BuscarImagen();
            }
        });
        builder.create().show();
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
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 2);
        }
    }
}
