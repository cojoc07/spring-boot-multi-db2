package com.facultate.magi.magazinonline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return jdbcTemplate.queryForList("SELECT p.*, c.NUME_CATEGORIE FROM V_PRODUS p, V_MAT_CATEGORIE c WHERE p.CATEGORIE_ID = c.CATEGORIE_ID AND PRODUS_ID = :var", produsId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("Niciun rezultat pentru produs id", produsId);
                }});
    }

    @GetMapping("produse/dupa-categorie/{categoryId}")
    public Object getProductByCategory(@PathVariable String categoryId) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM V_PRODUS WHERE CATEGORIE_ID = :var", categoryId);
        if (result.size() == 0) {
            return "Niciun rezultat pentru categorie id " + categoryId;
        }
        return result;
    }

}
