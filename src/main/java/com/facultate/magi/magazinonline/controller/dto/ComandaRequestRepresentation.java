package com.facultate.magi.magazinonline.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ComandaRequestRepresentation {
    @JsonIgnore
    private int comanda_id;
    @JsonProperty
    private String observatii;
    @JsonProperty
    private int client_id;

    public ComandaRequestRepresentation(int comanda_id, String observatii, int client_id) {
        this.comanda_id = comanda_id;
        this.observatii = observatii;
        this.client_id = client_id;
    }

    public int getComanda_id() {
        return comanda_id;
    }

    public void setComanda_id(int comanda_id) {
        this.comanda_id = comanda_id;
    }

    public String getObservatii() {
        return observatii;
    }

    public void setObservatii(String observatii) {
        this.observatii = observatii;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
