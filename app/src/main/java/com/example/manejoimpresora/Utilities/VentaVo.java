package com.example.manejoimpresora.Utilities;

public class VentaVo {
    private String cantidad;
    private String precioTo;
    private String precioUni;
    private String producto;

    public String getProducto() {
        return this.producto;
    }

    public void setProducto(String str) {
        this.producto = str;
    }

    public String getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(String str) {
        this.cantidad = str;
    }

    public String getPrecioUni() {
        return this.precioUni;
    }

    public void setPrecioUni(String str) {
        this.precioUni = str;
    }

    public String getPrecioTo() {
        return this.precioTo;
    }

    public void setPrecioTo(String str) {
        this.precioTo = str;
    }

    public VentaVo(String str, String str2, String str3, String str4) {
        this.producto = str;
        this.cantidad = str2;
        this.precioUni = str3;
        this.precioTo = str4;
    }
}
