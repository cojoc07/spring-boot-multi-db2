package com.facultate.magi.magazinonline.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IstoricPretRequestRepresentation {
    @JsonIgnore
    private BigDecimal id;
    @JsonProperty
    private int produs_id;
    @JsonProperty
    private LocalDateTime data;
    @JsonProperty
    private BigDecimal pret;

    public IstoricPretRequestRepresentation(BigDecimal id, int produs_id, LocalDateTime data, BigDecimal pret) {
        this.id = id;
        this.produs_id = produs_id;
        this.data = data;
        this.pret = pret;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public int getProdus_id() {
        return produs_id;
    }

    public void setProdus_id(int produs_id) {
        this.produs_id = produs_id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public BigDecimal getPret() {
        return pret;
    }

    public void setPret(BigDecimal pret) {
        this.pret = pret;
    }
}
