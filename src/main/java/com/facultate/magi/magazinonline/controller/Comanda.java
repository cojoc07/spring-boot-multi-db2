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
public class Comanda {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Comanda(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("comenzi")
    public Object getAllOrders() {
        return jdbcTemplate.queryForList("SELECT * FROM V_COMANDA");
    }

    @GetMapping("comenzi/{comandaId}")
    public Object getOrderById(@PathVariable String comandaId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_COMANDA WHERE COMANDA_ID = :var", comandaId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", comandaId);
                }});
    }

}
