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
public class Factura {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Factura(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @GetMapping("facturi")
    public Object getAllBills() {
        return jdbcTemplate.queryForList("SELECT * FROM V_FACTURA");
    }

    @GetMapping("facturi/{facturaId}")
    public Object getBillById(@PathVariable String facturaId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_FACTURA WHERE FACTURA_ID = :var", facturaId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", facturaId);
                }});
    }

}
