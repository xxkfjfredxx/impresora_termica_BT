package com.example.manejoimpresora.Utilities;

public class ClientesVo {
    private String Direccion;
    private String Local;
    private String Nombre;
    private String Ruta;
    private String Telefono;

    public ClientesVo(String str, String str2, String str3, String str4, String str5) {
        this.Local = str;
        this.Direccion = str2;
        this.Nombre = str3;
        this.Telefono = str4;
        this.Ruta = str5;
    }

    public String getLocal() {
        return this.Local;
    }

    public void setLocal(String str) {
        this.Local = str;
    }

    public String getDireccion() {
        return this.Direccion;
    }

    public void setDireccion(String str) {
        this.Direccion = str;
    }

    public String getNombre() {
        return this.Nombre;
    }

    public void setNombre(String str) {
        this.Nombre = str;
    }

    public String getTelefono() {
        return this.Telefono;
    }

    public void setTelefono(String str) {
        this.Telefono = str;
    }

    public String getRuta() {
        return this.Ruta;
    }

    public void setRuta(String str) {
        this.Ruta = str;
    }
}
