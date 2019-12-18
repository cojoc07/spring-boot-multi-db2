package com.facultate.magi.magazinonline.controller;

import com.facultate.magi.magazinonline.controller.dto.ComandaRequestRepresentation;
import com.facultate.magi.magazinonline.controller.dto.ProdusRequestRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public Object getOrderById(@PathVariable int comandaId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_COMANDA WHERE COMANDA_ID = :var", comandaId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", comandaId);
                }});
    }

    @GetMapping("comenzi/{comandaId}/factura")
    public Object getInvoiceForOrder(@PathVariable int comandaId){
        Map<String, Object> order = (Map<String, Object>) this.getOrderById(comandaId);

        try{
            Map<String, Object> factura = jdbcTemplate.queryForList(
                    "SELECT * FROM V_FACTURA f WHERE f.comanda_id = :var", comandaId
            ).get(0);

            order.put("Factura aferenta", factura);
            return order;
        } catch (Exception e){
            order.put("Nu exista factura pentru comanda cu id: ", comandaId);
        }
        return order;
    }

    @PostMapping(path="comenzi/adaugaComanda")
    public ComandaRequestRepresentation createOrder(@RequestBody ComandaRequestRepresentation comanda){

        BigDecimal result = (BigDecimal) jdbcTemplate.queryForList("select db1_global.sqnc.nextval from dual").get(0).get("NEXTVAL");

        Client client = new Client(jdbcTemplate);
        Map<String, Object> res = (Map<String, Object>) client.getClientById(comanda.getClient_id());
        if (!res.containsKey("NOT FOUND ID")){
            jdbcTemplate.update("INSERT INTO V_COMANDA(COMANDA_ID,OBSERVATII,CLIENT_ID) " +
                    "VALUES(:id, :observatii, :client_id)", result, comanda.getObservatii(), comanda.getClient_id());
            return comanda;
        } else {
            return new ComandaRequestRepresentation(0,"CLIENTUL SPECIFICAT NU EXISTA!", 0);
        }

    }

    @DeleteMapping(path="comenzi/{comandaId}")
    public void deleteOrderById(@PathVariable BigDecimal comandaId){
        jdbcTemplate.update("DELETE FROM V_COMANDA WHERE comanda_id = :id", comandaId);
    }
}
