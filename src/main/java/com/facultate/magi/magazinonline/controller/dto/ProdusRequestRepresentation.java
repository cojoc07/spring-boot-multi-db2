package com.facultate.magi.magazinonline.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ProdusRequestRepresentation {

    @JsonIgnore
    private BigDecimal produs_id;
    @JsonProperty
    private int categorie_id;
    @JsonProperty
    private int comanda_id;
    @JsonProperty
    private String nume_produs;
    @JsonProperty
    private String culoare;
    @JsonProperty
    private double pret;

    public ProdusRequestRepresentation(BigDecimal produs_id, int categorie_id, int comanda_id, String nume_produs, String culoare, double pret) {
        this.produs_id = produs_id;
        this.categorie_id = categorie_id;
        this.comanda_id = comanda_id;
        this.nume_produs = nume_produs;
        this.culoare = culoare;
        this.pret = pret;
    }

    public BigDecimal getProdus_id() {
        return produs_id;
    }

    public void setProdus_id(BigDecimal produs_id) {
        this.produs_id = produs_id;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }

    public int getComanda_id() {
        return comanda_id;
    }

    public void setComanda_id(int comanda_id) {
        this.comanda_id = comanda_id;
    }

    public String getNume_produs() {
        return nume_produs;
    }

    public void setNume_produs(String nume_produs) {
        this.nume_produs = nume_produs;
    }

    public String getCuloare() {
        return culoare;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }
}
