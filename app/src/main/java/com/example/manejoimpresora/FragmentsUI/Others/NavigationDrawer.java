package com.example.manejoimpresora.FragmentsUI.Others;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.example.manejoimpresora.DataBases.BaseDatosConfiguraciones2;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Securities;
import com.example.manejoimpresora.Utilities.comprobarInternet;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;


public class NavigationDrawer extends AppCompatActivity implements PurchasesUpdatedListener {
    public static final String ITEM_SKU_SUBSCRIBE = "suscripcion_mes";
    public static final String ITEM_SKU_SUBSCRIBE2 = "suscripcion_anio";
    public static final String PREF_FILE = "MyPref";
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String SUBSCRIBE_KEY = "subscribes";
    private int REQUEST_UPODATE_CODE = 11;
    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == 0) {
                NavigationDrawer.this.saveSubscribeValueToPref(true);
                NavigationDrawer.this.recreate();
            }
        }
    };
    /* access modifiers changed from: private */
    public FrameLayout adContainerView;
    public BillingClient billingClient;
    private String estado = "Yes";
    private String fecha = "";
    private Uri imageUri;
    private Uri imageUri2;
    private AppBarConfiguration mAppBarConfiguration;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_navigation_drawer);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_QR, R.id.nav_home, R.id.fragmentProducts, R.id.nav_gallery, R.id.inventario, R.id.nav_slideshow, R.id.suscriptions, R.id.nav_Settings, R.id.photosPrinting, R.id.drawAndPrint, R.id.instructionsFragment, R.id.pdfPrinting, R.id.rewardFragment, R.id.clients, R.id.reportsFragment).setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout)).build();
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        //NavController findNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController((AppCompatActivity) this, navController, this.mAppBarConfiguration);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.nav_view), navController);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.adContainerView = (FrameLayout) findViewById(R.id.frameLayout);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        this.imageUri = intent.getData();
        this.imageUri2 = (Uri) intent.getParcelableExtra("android.intent.extra.STREAM");
        if ("android.intent.action.SEND".equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                handleSendPdf2(intent);
            } else if (type.startsWith("image/")) {
                handleSendImage2(intent);
            }
        }
        if ("android.intent.action.VIEW".equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                handleSendPdf(intent);
            } else if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }
        iniciar();
        crearConfiguracionBase2();
        cargarSuscripcion();
        if (getDefaultsPreference("Open2", this).booleanValue()) {
            alertDialog2();
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

    public void crearConfiguracionBase2() {
        SQLiteDatabase writableDatabase = new BaseDatosConfiguraciones2(this, "registroConfiguraciones2", null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", 1);
        writableDatabase.insert("configuraciones", null, contentValues);
        writableDatabase.close();
    }

    public void alertDialog2() {
        if (this.estado.equals("No")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon((int) R.mipmap.ic_launcher);
            builder.setTitle((int) R.string.selectSus);
            final ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367058);
            arrayAdapter.add(getString(R.string.selectMensu));
            arrayAdapter.add(getString(R.string.selectFree));
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (((String) arrayAdapter.getItem(i)).contains("1.99")) {
                        Navigation.findNavController(NavigationDrawer.this, R.id.nav_host_fragment).navigate((int) R.id.suscriptions);
                    }
                }
            });
            builder.show();
        }
    }


    public void cargarSuscripcion() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(this, "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select subscription,fecha from suscripcion where numero ='1'", (String[]) null);
        if (rawQuery.moveToFirst()) {
            this.estado = rawQuery.getString(0);
            this.fecha = rawQuery.getString(1);
            writableDatabase.close();
            if (this.fecha == null) {
                this.fecha = "";
            }
        }
    }

    public void iniciar() {
        BillingClient build = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        this.billingClient = build;
        build.startConnection(new BillingClientStateListener() {
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == 0) {
                    List<Purchase> purchasesList = NavigationDrawer.this.billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                    if (purchasesList == null || purchasesList.size() <= 0) {
                        NavigationDrawer.this.saveSubscribeValueToPref(false);
                    } else {
                        NavigationDrawer.this.handlePurchases(purchasesList);
                    }
                }
            }

            public void onBillingServiceDisconnected() {
                NavigationDrawer navigationDrawer = NavigationDrawer.this;
                Toast.makeText(navigationDrawer, navigationDrawer.getString(R.string.disconected), Toast.LENGTH_SHORT).show();
            }
        });
        if (getSubscribeValueFromPref()) {
            saveSuscrip("Yes");
            this.estado = "Yes";
            return;
        }
        saveSuscrip("Yes");
    }

    public void saveSuscrip(String str) {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(this, "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        if (!str.isEmpty()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", "1");
            contentValues.put("subscription", str);
            writableDatabase.update("suscripcion", contentValues, "numero='1'", (String[]) null);
            writableDatabase.close();
        }
    }

    /* access modifiers changed from: package-private */
    public void handlePurchases(List<Purchase> list) {
        for (Purchase next : list) {
            if ((ITEM_SKU_SUBSCRIBE.equals(next.getSku()) || ITEM_SKU_SUBSCRIBE2.equals(next.getSku())) && next.getPurchaseState() == 1) {
                if (!verifyValidSignature(next.getOriginalJson(), next.getSignature())) {
                    Toast.makeText(getApplicationContext(), "Error : invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!next.isAcknowledged()) {
                    this.billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(next.getPurchaseToken()).build(), this.ackPurchase);
                } else if (!getSubscribeValueFromPref()) {
                    saveSubscribeValueToPref(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.purchased), Toast.LENGTH_SHORT).show();
                    recreate();
                }
            } else if ((ITEM_SKU_SUBSCRIBE.equals(next.getSku()) || ITEM_SKU_SUBSCRIBE2.equals(next.getSku())) && next.getPurchaseState() == 2) {
                Toast.makeText(getApplicationContext(), getString(R.string.pendiPurchased), Toast.LENGTH_SHORT).show();
            } else if ((ITEM_SKU_SUBSCRIBE.equals(next.getSku()) || ITEM_SKU_SUBSCRIBE2.equals(next.getSku())) && next.getPurchaseState() == 0) {
                saveSubscribeValueToPref(false);
                Toast.makeText(getApplicationContext(), getString(R.string.statusUkno), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean verifyValidSignature(String str, String str2) {
        try {
            return Securities.verifyPurchase("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7qQBevKx1INcEMnu1Wy3T3TqqcLIESQrMPq62xoHbOvzvmA2a2z11Uyw8qd4aLzvCYbsR02KmNTvd+XxObeB/8bvjWLU9i2III4cyTMmthG8LE5UO2kN2QgF/Hsoo8cR6QkkHhczlk5WhS2AmE1bt2bCcy8KshY6mwfOK25MoFNt2rS2xi9VTot5uxjjXt3fHNBt9HiR/CIWECDUEW2foXdRe/Tv61Ov9dH8hCchN+nCShbuTXCR8S/qdPO2h176yNwRKZ+caIhyW13Kb1b9qxEQ0cHwIqDxnp/nDyQmYCDtxW7ruiwpmaihAGxP2rKHu3IXMg324iosOYfK4fbJswIDAQAB", str, str2);
        } catch (IOException unused) {
            return false;
        }
    }

    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        if (billingResult.getResponseCode() == 0 && list != null) {
            handlePurchases(list);
        } else if (billingResult.getResponseCode() == 7) {
            List<Purchase> purchasesList = this.billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
            if (purchasesList != null) {
                handlePurchases(purchasesList);
            }
        } else if (billingResult.getResponseCode() == 1) {
            Toast.makeText(this, "Purchase Canceled", Toast.LENGTH_SHORT).show();
            saveSuscrip("No");
        } else {
            Toast.makeText(this, "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences("MyPref", 0);
    }

    private SharedPreferences.Editor getPreferenceEditObject() {
        return getApplicationContext().getSharedPreferences("MyPref", 0).edit();
    }

    private boolean getSubscribeValueFromPref() {
        return getPreferenceObject().getBoolean("subscribes", false);
    }

    /* access modifiers changed from: private */
    public void saveSubscribeValueToPref(boolean z) {
        getPreferenceEditObject().putBoolean("subscribes", z).commit();
    }

    public void handleSendImage2(Intent intent) {
        if (this.imageUri2 != null) {
            Bundle bundle = new Bundle();
            bundle.putString("URI", String.valueOf(this.imageUri2));
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate((int) R.id.photosPrinting, bundle);
        }
    }

    public void handleSendImage(Intent intent) {
        if (this.imageUri != null) {
            Bundle bundle = new Bundle();
            bundle.putString("URI", String.valueOf(this.imageUri));
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate((int) R.id.photosPrinting, bundle);
        }
    }

    public void handleSendPdf(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            Bundle bundle = new Bundle();
            bundle.putString("URI", String.valueOf(data));
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate((int) R.id.pdfPrinting, bundle);
        }
    }

    public void handleSendPdf2(Intent intent) {
        Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.STREAM");
        if (uri != null) {
            Bundle bundle = new Bundle();
            bundle.putString("URI", String.valueOf(uri));
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate((int) R.id.pdfPrinting, bundle);
        }
    }

    public void comprobarInternet() {
        if (new comprobarInternet().isOnlineNet().booleanValue()) {
            this.mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_QR, R.id.nav_home, R.id.fragmentProducts, R.id.nav_gallery, R.id.inventario, R.id.nav_slideshow, R.id.suscriptions, R.id.nav_Settings, R.id.photosPrinting, R.id.drawAndPrint, R.id.instructionsFragment, R.id.pdfPrinting, R.id.rewardFragment, R.id.clients, R.id.reportsFragment).setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout)).build();
            NavController findNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController((AppCompatActivity) this, findNavController, this.mAppBarConfiguration);
            NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.nav_view), findNavController);
            return;
        }
        Toast.makeText(getApplication(), getString(R.string.ToastInternet), Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(menuItem);
        }
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate((int) R.id.nav_Settings);
        return true;
    }

    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), this.mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
