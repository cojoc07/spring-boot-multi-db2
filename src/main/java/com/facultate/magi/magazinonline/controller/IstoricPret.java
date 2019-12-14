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
public class IstoricPret {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public IstoricPret(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("istoric-preturi")
    public Object getPreviousPrices() {
        return jdbcTemplate.queryForList("SELECT * FROM V_ISTORIC_PRET");
    }

    @GetMapping("istoric-preturi/{istoricId}")
    public Object getPreviousPriceById(@PathVariable String istoricId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_ISTORIC_PRET WHERE ISTORIC_PRET_ID = :var", istoricId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", istoricId);
                }});
    }

}
