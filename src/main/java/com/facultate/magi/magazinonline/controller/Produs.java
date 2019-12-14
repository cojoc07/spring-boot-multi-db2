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
public class Produs {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Produs(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("produse")
    public Object getProducts() {
        return jdbcTemplate.queryForList("SELECT * FROM V_PRODUS");
    }

    @GetMapping("produse/{produsId}")
    public Object getProductById(@PathVariable String produsId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_PRODUS WHERE PRODUS_ID = :var", produsId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", produsId);
                }});
    }

}
