package com.facultate.magi.magazinonline.controller;

import com.facultate.magi.magazinonline.controller.dto.ClientRequestRepresentation;
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

    @PostMapping(path="produse/adaugaProdus")
    public ProdusRequestRepresentation createProduct(@RequestBody ProdusRequestRepresentation produs){

        BigDecimal result = (BigDecimal) jdbcTemplate.queryForList("select db1_global.sqnc.nextval from dual").get(0).get("NEXTVAL");

        jdbcTemplate.update("INSERT INTO V_PRODUS(PRODUS_ID,CATEGORIE_ID,COMANDA_ID,NUME_PRODUS,CULOARE,PRET) " +
                        "VALUES(:id, :categorie_id, :comanda_id, :nume_produs, :culoare, :pret)", result, produs.getCategorie_id(),
                produs.getComanda_id(), produs.getNume_produs(), produs.getCuloare(), produs.getPret());

        return new ProdusRequestRepresentation(result, produs.getCategorie_id(),
                produs.getComanda_id(), produs.getNume_produs(), produs.getCuloare(), produs.getPret());
    }

    @DeleteMapping(path="produse/{produsId}")
    public void deleteProductById(@PathVariable BigDecimal produsId){
        jdbcTemplate.update("DELETE FROM V_PRODUS WHERE produs_id = :id", produsId);
    }

}
