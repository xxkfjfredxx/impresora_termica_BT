package com.example.manejoimpresora.FragmentsUI.Others;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.manejoimpresora.DataBases.BaseDatosSuscripcion;
import com.example.manejoimpresora.R;
import com.example.manejoimpresora.Utilities.Securities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuscriptionsFragment extends Fragment implements PurchasesUpdatedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String ITEM_SKU_SUBSCRIBE = "";
    public static final String PREF_FILE = "MyPref";
    public static final String SUBSCRIBE_KEY = "subscribes";
    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == 0) {
                SuscriptionsFragment.this.saveSubscribeValueToPref(true);
                SuscriptionsFragment.this.getActivity().recreate();
            }
        }
    };
    /* access modifiers changed from: private */
    public BillingClient billingClient;
    private String mParam1;
    private String mParam2;
    private Button manageSubs;
    private TextView premiumContent;
    private Button subscribe;
    private Button subscribeYear;
    private TextView subscriptionStatus;

    public static SuscriptionsFragment newInstance(String str, String str2) {
        SuscriptionsFragment suscriptionsFragment = new SuscriptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        suscriptionsFragment.setArguments(bundle);
        return suscriptionsFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_suscriptions, viewGroup, false);
        this.premiumContent = (TextView) inflate.findViewById(R.id.premium_content);
        this.subscriptionStatus = (TextView) inflate.findViewById(R.id.subscription_status);
        this.subscribe = (Button) inflate.findViewById(R.id.subscribe);
        this.subscribeYear = (Button) inflate.findViewById(R.id.subscribe2);
        this.manageSubs = (Button) inflate.findViewById(R.id.btnManage);
        crearConfiguracionBase();
        BillingClient build = BillingClient.newBuilder(getContext()).enablePendingPurchases().setListener(this).build();
        this.billingClient = build;
        build.startConnection(new BillingClientStateListener() {
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == 0) {
                    List<Purchase> purchasesList = SuscriptionsFragment.this.billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                    if (purchasesList == null || purchasesList.size() <= 0) {
                        SuscriptionsFragment.this.saveSubscribeValueToPref(false);
                    } else {
                        SuscriptionsFragment.this.handlePurchases(purchasesList);
                    }
                }
            }

            public void onBillingServiceDisconnected() {
                Toast.makeText(SuscriptionsFragment.this.getContext(), SuscriptionsFragment.this.getString(R.string.disconected), Toast.LENGTH_SHORT).show();
            }
        });
        if (getSubscribeValueFromPref()) {
            this.subscribe.setVisibility(View.GONE);
            this.subscribeYear.setVisibility(View.GONE);
            this.manageSubs.setVisibility(View.VISIBLE);
            this.premiumContent.setVisibility(View.VISIBLE);
            this.subscriptionStatus.setText(getString(R.string.statutusOk));
            saveSuscrip("Yes");
        } else {
            this.premiumContent.setVisibility(View.GONE);
            this.subscribe.setVisibility(View.VISIBLE);
            this.subscribeYear.setVisibility(View.VISIBLE);
            this.subscriptionStatus.setText(getString(R.string.statutusNot));
            saveSuscrip("No");
        }
        this.subscribe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SuscriptionsFragment.ITEM_SKU_SUBSCRIBE = NavigationDrawer.ITEM_SKU_SUBSCRIBE;
                SuscriptionsFragment.this.subscribe();
            }
        });
        this.subscribeYear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SuscriptionsFragment.ITEM_SKU_SUBSCRIBE = NavigationDrawer.ITEM_SKU_SUBSCRIBE2;
                SuscriptionsFragment.this.subscribe();
            }
        });
        this.manageSubs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://play.google.com/store/account/subscriptions"));
                SuscriptionsFragment.this.startActivity(intent);
            }
        });
        return inflate;
    }

    public void crearConfiguracionBase() {
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getContext(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("numero", "1");
        writableDatabase.insert("suscripcion", (String) null, contentValues);
        writableDatabase.close();
    }

    public void saveSuscrip(String str) {
        String format = str.equals("Yes") ? new SimpleDateFormat("dd-MM-yyyy").format(new Date()) : "";
        SQLiteDatabase writableDatabase = new BaseDatosSuscripcion(getContext(), "registroSuscripcion", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
        if (!str.isEmpty()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("numero", "1");
            contentValues.put("subscription", str);
            contentValues.put("fecha", format);
            writableDatabase.update("suscripcion", contentValues, "numero='1'", (String[]) null);
            writableDatabase.close();
        }
    }

    private SharedPreferences getPreferenceObject() {
        return getContext().getSharedPreferences("MyPref", 0);
    }

    private SharedPreferences.Editor getPreferenceEditObject() {
        return getContext().getSharedPreferences("MyPref", 0).edit();
    }

    private boolean getSubscribeValueFromPref() {
        return getPreferenceObject().getBoolean("subscribes", false);
    }

    /* access modifiers changed from: private */
    public void saveSubscribeValueToPref(boolean z) {
        getPreferenceEditObject().putBoolean("subscribes", z).commit();
    }

    public void subscribe() {
        if (this.billingClient.isReady()) {
            initiatePurchase();
            return;
        }
        BillingClient build = BillingClient.newBuilder(getContext()).enablePendingPurchases().setListener(this).build();
        this.billingClient = build;
        build.startConnection(new BillingClientStateListener() {
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == 0) {
                    SuscriptionsFragment.this.initiatePurchase();
                    return;
                }
                Context context = SuscriptionsFragment.this.getContext();
                Toast.makeText(context, "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
            }

            public void onBillingServiceDisconnected() {
                Toast.makeText(SuscriptionsFragment.this.getContext(), SuscriptionsFragment.this.getString(R.string.disconected), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: private */
    public void initiatePurchase() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(ITEM_SKU_SUBSCRIBE);
        SkuDetailsParams.Builder newBuilder = SkuDetailsParams.newBuilder();
        newBuilder.setSkusList(arrayList).setType(BillingClient.SkuType.SUBS);
        if (this.billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).getResponseCode() == 0) {
            this.billingClient.querySkuDetailsAsync(newBuilder.build(), new SkuDetailsResponseListener() {
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                    if (billingResult.getResponseCode() != 0) {
                        Context context = SuscriptionsFragment.this.getContext();
                        Toast.makeText(context, " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        SuscriptionsFragment.this.saveSuscrip("No");
                    } else if (list == null || list.size() <= 0) {
                        Toast.makeText(SuscriptionsFragment.this.getContext(), "Item not Found", Toast.LENGTH_SHORT).show();
                        SuscriptionsFragment.this.saveSuscrip("No");
                    } else {
                        SuscriptionsFragment.this.billingClient.launchBillingFlow(SuscriptionsFragment.this.getActivity(), BillingFlowParams.newBuilder().setSkuDetails(list.get(0)).build());
                    }
                }
            });
            return;
        }
        Toast.makeText(getContext(), getString(R.string.noSupported), Toast.LENGTH_SHORT).show();
        saveSuscrip("No");
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
            Toast.makeText(getContext(), getString(R.string.canceledPurcha), Toast.LENGTH_SHORT).show();
            saveSuscrip("No");
        } else {
            Context context = getContext();
            Toast.makeText(context, "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: package-private */
    public void handlePurchases(List<Purchase> list) {
        for (Purchase next : list) {
            if (!ITEM_SKU_SUBSCRIBE.equals(next.getSku()) || next.getPurchaseState() != 1) {
                if (ITEM_SKU_SUBSCRIBE.equals(next.getSku()) && next.getPurchaseState() == 2) {
                    Toast.makeText(getContext(), getString(R.string.pendiPurchased), Toast.LENGTH_SHORT).show();
                } else if (ITEM_SKU_SUBSCRIBE.equals(next.getSku()) && next.getPurchaseState() == 0) {
                    saveSubscribeValueToPref(false);
                    this.premiumContent.setVisibility(View.GONE);
                    this.subscribe.setVisibility(View.VISIBLE);
                    this.subscriptionStatus.setText(getString(R.string.statutusOk));
                    saveSuscrip("No");
                    Toast.makeText(getContext(), getString(R.string.statusUkno), Toast.LENGTH_SHORT).show();
                }
            } else if (!verifyValidSignature(next.getOriginalJson(), next.getSignature())) {
                Toast.makeText(getContext(), getString(R.string.canceledPurcha), Toast.LENGTH_SHORT).show();
                saveSuscrip("No");
                return;
            } else if (!next.isAcknowledged()) {
                this.billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(next.getPurchaseToken()).build(), this.ackPurchase);
            } else if (!getSubscribeValueFromPref()) {
                saveSubscribeValueToPref(true);
                Toast.makeText(getContext(), getString(R.string.purchased), Toast.LENGTH_SHORT).show();
                saveSuscrip("Yes");
                Intent launchIntentForPackage = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                launchIntentForPackage.addFlags(67108864);
                startActivity(launchIntentForPackage);
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

    public void onDestroy() {
        super.onDestroy();
        BillingClient billingClient2 = this.billingClient;
        if (billingClient2 != null) {
            billingClient2.endConnection();
        }
    }
}
