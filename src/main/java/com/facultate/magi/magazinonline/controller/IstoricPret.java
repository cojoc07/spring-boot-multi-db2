package com.facultate.magi.magazinonline.controller;

import com.facultate.magi.magazinonline.controller.dto.IstoricPretRequestRepresentation;
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
                    this.put("Niciun rezultat pentru istoric id", istoricId);
                }});
    }

    @GetMapping("istoric-preturi/{produsId}")
    public Object getPreviousPriceByProductId(@PathVariable String produsId) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM V_ISTORIC_PRET WHERE PRODUS_ID = :var", produsId);
        if (result.size() == 0) {
            return "Niciun rezultat pentru produs id " + produsId;
        }
        return result;
    }

    @PostMapping("istoric-preturi/adaugaIstoric")
    public IstoricPretRequestRepresentation addHistoricPrice(@RequestBody IstoricPretRequestRepresentation istoric){
        BigDecimal result = (BigDecimal) jdbcTemplate.queryForList("select db1_global.sqnc.nextval from dual").get(0).get("NEXTVAL");
        //TODO fix query
        jdbcTemplate.update("INSERT INTO V_ISTORIC_PRET(PRODUS_ID,CATEGORIE_ID,COMANDA_ID,NUME_PRODUS,CULOARE,PRET) " +
                        "VALUES(:id, :categorie_id, :comanda_id, :nume_produs, :culoare, :pret)", result, istoric.getProdus_id(),
                            istoric.getData(), istoric.getPret());

        return new IstoricPretRequestRepresentation(result, istoric.getProdus_id(),
                istoric.getData(), istoric.getPret());
    }
}
