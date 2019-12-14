package com.facultate.magi.magazinonline.controller;

import com.facultate.magi.magazinonline.controller.dto.ClientRequestRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
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

    @GetMapping("clienti/{clientId}/comenzi")
    public Object getClientOrders(@PathVariable String clientId) {

        Map<String, Object> client = (Map<String, Object>) this.getClientById(clientId);

        List<Map<String, Object>> comenzi = jdbcTemplate.queryForList(
                "SELECT * FROM V_COMANDA c WHERE c.client_id = :var", clientId
        );

        client.put("COMENZI AFERENTE", comenzi);

        return client;
    }

    @GetMapping("clienti/{clientId}/comenzi/produse")
    public Object getClientOrdersWithProducts(@PathVariable String clientId) {

        Map<String, Object> clientOrders = (Map<String, Object>) this.getClientOrders(clientId);

        List<Map<String, Object>> orders = (List<Map<String, Object>>) clientOrders.get("COMENZI AFERENTE");

        for (Map<String, Object> order : orders) {
            BigDecimal comandaId = (BigDecimal) order.get("COMANDA_ID");
            var products = jdbcTemplate.queryForList("SELECT * FROM V_PRODUS WHERE COMANDA_ID = :var", comandaId);
            order.put("PRODUSE AFERENTE", products);
        }

        return clientOrders;
    }

    @PostMapping(path="clienti/adaugaClient")
    public Client createClient(@RequestBody ClientRequestRepresentation client){
        Client nou = new Client(jdbcTemplate);

        BigDecimal result = (BigDecimal) jdbcTemplate.queryForList("select db1_global.sqnc.nextval from dual").get(0).get("NEXTVAL");

        jdbcTemplate.update("INSERT INTO V_CLIENT(CLIENT_ID,NUME,PRENUME,EMAIL,TIP_CLIENT) " +
                        "VALUES(:id, :nume, :prenume, :email, :tipclient)", result, client.getNume(),
                                    client.getPrenume(), client.getEmail(), client.getTipClient());


        return nou;
    }
}