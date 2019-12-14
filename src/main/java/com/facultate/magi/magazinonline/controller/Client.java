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
public class Client {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Client(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("clienti")
    public Object getAllClients() {
        return jdbcTemplate.queryForList("SELECT * FROM V_CLIENT");
    }

    @GetMapping("clienti/{clientId}")
    public Object getClientById(@PathVariable String clientId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_CLIENT WHERE CLIENT_ID = :var", clientId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", clientId);
                }});
    }

}
