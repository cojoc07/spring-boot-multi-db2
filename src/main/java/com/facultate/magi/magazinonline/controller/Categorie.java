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
public class Categorie {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Categorie(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("categorii")
    public Object getCategories() {
        return jdbcTemplate.queryForList("SELECT * FROM V_MAT_CATEGORIE");
    }

    @GetMapping("categorii/{categorieId}")
    public Object getCategoryById(@PathVariable String categoryId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_MAT_CATEGORIE WHERE CATEGORIE_ID = :var", categoryId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("Niciun rezultat pentru categorie id", categoryId);
                }});
    }

}
