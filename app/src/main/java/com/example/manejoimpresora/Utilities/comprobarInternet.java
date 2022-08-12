package com.example.manejoimpresora.Utilities;

public class comprobarInternet {
    public Boolean isOnlineNet() {
        try {
            return Boolean.valueOf(Runtime.getRuntime().exec("ping -c 1 www.google.es").waitFor() == 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
