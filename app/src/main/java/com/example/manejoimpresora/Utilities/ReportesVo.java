package com.example.manejoimpresora.Utilities;

public class ReportesVo {
    private String cantidad;
    private String precio;
    private String producto;

    public ReportesVo(String str, String str2, String str3) {
        this.producto = str;
        this.precio = str3;
        this.cantidad = str2;
    }

    public String getProducto() {
        return this.producto;
    }

    public void setProducto(String str) {
        this.producto = str;
    }

    public String getPrecio() {
        return this.precio;
    }

    public void setPrecio(String str) {
        this.precio = str;
    }

    public String getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(String str) {
        this.cantidad = this.cantidad;
    }
}
