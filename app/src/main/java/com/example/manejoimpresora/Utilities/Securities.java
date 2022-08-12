package com.example.manejoimpresora.Utilities;

import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Securities {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String TAG = "IABUtil/Security";

    public static boolean verifyPurchase(String str, String str2, String str3) throws IOException {
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str) || TextUtils.isEmpty(str3)) {
            return false;
        }
        return verify(generatePublicKey(str), str2, str3);
    }

    public static PublicKey generatePublicKey(String str) throws IOException {
        try {
            return KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decode(str, 0)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e2) {
            throw new IOException("Invalid key specification: " + e2);
        }
    }

    public static boolean verify(PublicKey publicKey, String str, String str2) {
        try {
            byte[] decode = Base64.decode(str2, 0);
            try {
                Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initVerify(publicKey);
                instance.update(str.getBytes());
                if (!instance.verify(decode)) {
                    return false;
                }
                return true;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException | SignatureException unused) {
                return false;
            }
        } catch (IllegalArgumentException unused2) {
            return false;
        }
    }
}
