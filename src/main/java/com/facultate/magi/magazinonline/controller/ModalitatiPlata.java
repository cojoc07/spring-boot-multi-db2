package com.facultate.magi.magazinonline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Transactional
public class ModalitatiPlata {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ModalitatiPlata(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("modalitati-plata")
    public Object getPaymentMethods() {
        return jdbcTemplate.queryForList("SELECT * FROM V_MAT_MODALITATE_PLATA");
    }

    @GetMapping("modalitati-plata/{modalitatePlata}")
    public Object getPaymentMethodsById(@PathVariable String modalitatePlata) {
        return jdbcTemplate.queryForList("SELECT * FROM V_MAT_MODALITATE_PLATA WHERE MODALITATE_PLATA_ID = :var", modalitatePlata)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", modalitatePlata);
                }});
    }

}
