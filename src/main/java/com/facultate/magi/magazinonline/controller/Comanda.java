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
@SuppressWarnings("unchecked")
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

    @GetMapping("comenzi/{comandaId}/factura")
    public Object getInvoiceForOrder(@PathVariable String comandaId){
        Map<String, Object> order = (Map<String, Object>) this.getOrderById(comandaId);

        Map<String, Object> factura = jdbcTemplate.queryForList(
                "SELECT * FROM V_FACTURA f WHERE f.comanda_id = :var", comandaId
        ).get(0);

        order.put("Factura aferenta", factura);
        return order;
    }
}
