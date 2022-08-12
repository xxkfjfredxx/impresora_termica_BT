package com.example.manejoimpresora.Utilities;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final byte[] UNICODE_TEXT = {35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35};
    private static String[] binaryArray = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
    private static String hexStr = "0123456789ABCDEF";

    public static byte[] decodeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ArrayList arrayList = new ArrayList();
        int i = width / 8;
        int i2 = width % 8;
        String str = "";
        if (i2 > 0) {
            for (int i3 = 0; i3 < 8 - i2; i3++) {
                str = str + "0";
            }
        }
        for (int i4 = 0; i4 < height; i4++) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i5 = 0; i5 < width; i5++) {
                int pixel = bitmap.getPixel(i5, i4);
                int i6 = (pixel >> 16) & 255;
                int i7 = (pixel >> 8) & 255;
                int i8 = pixel & 255;
                if (i6 <= 160 || i7 <= 160 || i8 <= 160) {
                    stringBuffer.append("1");
                } else {
                    stringBuffer.append("0");
                }
            }
            if (i2 > 0) {
                stringBuffer.append(str);
            }
            arrayList.add(stringBuffer.toString());
        }
        List<String> binaryListToHexStringList = binaryListToHexStringList(arrayList);
        if (i2 != 0) {
            i++;
        }
        String hexString = Integer.toHexString(i);
        if (hexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        }
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        String str2 = hexString + "00";
        String hexString2 = Integer.toHexString(height);
        if (hexString2.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        }
        if (hexString2.length() == 1) {
            hexString2 = "0" + hexString2;
        }
        String str3 = hexString2 + "00";
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add("1D763000" + str2 + str3);
        arrayList2.addAll(binaryListToHexStringList);
        return hexList2Byte(arrayList2);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String next : list) {
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            while (i < next.length()) {
                int i2 = i + 8;
                stringBuffer.append(myBinaryStrToHexString(next.substring(i, i2)));
                i = i2;
            }
            arrayList.add(stringBuffer.toString());
        }
        return arrayList;
    }

    public static String myBinaryStrToHexString(String str) {
        int i = 0;
        String substring = str.substring(0, 4);
        String substring2 = str.substring(4, 8);
        String str2 = "";
        int i2 = 0;
        while (true) {
            String[] strArr = binaryArray;
            if (i2 >= strArr.length) {
                break;
            }
            if (substring.equals(strArr[i2])) {
                str2 = str2 + hexStr.substring(i2, i2 + 1);
            }
            i2++;
        }
        while (true) {
            String[] strArr2 = binaryArray;
            if (i >= strArr2.length) {
                return str2;
            }
            if (substring2.equals(strArr2[i])) {
                str2 = str2 + hexStr.substring(i, i + 1);
            }
            i++;
        }
    }

    public static byte[] hexList2Byte(List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String hexStringToBytes : list) {
            arrayList.add(hexStringToBytes(hexStringToBytes));
        }
        return sysCopy(arrayList);
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String upperCase = str.toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (charToByte(charArray[i2 + 1]) | (charToByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    public static byte[] sysCopy(List<byte[]> list) {
        int i = 0;
        for (byte[] length : list) {
            i += length.length;
        }
        byte[] bArr = new byte[i];
        int i2 = 0;
        for (byte[] next : list) {
            System.arraycopy(next, 0, bArr, i2, next.length);
            i2 += next.length;
        }
        return bArr;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
