package com.facultate.magi.magazinonline.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientRequestRepresentation {
    @JsonIgnore
    private int id;
    @JsonProperty
    private String nume;
    @JsonProperty
    private String prenume;
    @JsonProperty
    private String email;
    @JsonProperty
    private String tipClient;

    public ClientRequestRepresentation(int id, String nume, String prenume, String email, String tipClient) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.tipClient = tipClient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipClient() {
        return tipClient;
    }

    public void setTipClient(String tipClient) {
        this.tipClient = tipClient;
    }
}
