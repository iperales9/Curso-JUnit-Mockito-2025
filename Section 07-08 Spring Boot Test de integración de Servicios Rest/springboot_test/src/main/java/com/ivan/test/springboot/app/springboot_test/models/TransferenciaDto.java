package com.ivan.test.springboot.app.springboot_test.models;

import java.math.BigDecimal;

public class TransferenciaDto {
    private Long cuentaOrigen;
    private Long cuentaDestino;
    private BigDecimal monto;
    private Long bancoID;

    public TransferenciaDto() {
    }

    public Long getBancoID() {
        return bancoID;
    }

    public void setBancoID(Long bancoID) {
        this.bancoID = bancoID;
    }

    public Long getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(Long cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public Long getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(Long cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
