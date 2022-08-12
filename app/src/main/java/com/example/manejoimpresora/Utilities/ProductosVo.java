package com.example.manejoimpresora.Utilities;

public class ProductosVo {
    private String cantidad;
    private String codigo;
    private String precio;
    private String producto;

    public ProductosVo(String str, String str2, String str3) {
        this.codigo = str;
        this.producto = str2;
        this.precio = str3;
        this.cantidad = str3;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String str) {
        this.codigo = str;
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
