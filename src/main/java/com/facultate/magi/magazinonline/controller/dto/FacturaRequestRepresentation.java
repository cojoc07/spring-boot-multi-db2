package com.facultate.magi.magazinonline.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class FacturaRequestRepresentation {
    @JsonIgnore
    private int id;
    @JsonProperty
    private BigDecimal totalPret;
    @JsonProperty
    private int comanda_id;

    public FacturaRequestRepresentation(int id, BigDecimal totalPret, int comanda_id) {
        this.id = id;
        this.totalPret = totalPret;
        this.comanda_id = comanda_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getTotalPret() {
        return totalPret;
    }

    public void setTotalPret(BigDecimal totalPret) {
        this.totalPret = totalPret;
    }

    public int getComanda_id() {
        return comanda_id;
    }

    public void setComanda_id(int comanda_id) {
        this.comanda_id = comanda_id;
    }
}
