package com.facultate.magi.magazinonline.controller;

import com.facultate.magi.magazinonline.controller.dto.ClientRequestRepresentation;
import com.facultate.magi.magazinonline.controller.dto.FacturaRequestRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@Transactional
public class Factura {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Factura(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @GetMapping("facturi")
    public Object getAllBills() {
        return jdbcTemplate.queryForList("SELECT * FROM V_FACTURA");
    }

    @GetMapping("facturi/{facturaId}")
    public Object getBillById(@PathVariable String facturaId) {
        return jdbcTemplate.queryForList("SELECT * FROM V_FACTURA WHERE FACTURA_ID = :var", facturaId)
                .stream()
                .findFirst()
                .orElse(new HashMap<>() {{
                    this.put("NOT FOUND ID", facturaId);
                }});
    }

    @PostMapping(path="facturi/adaugaFactura")
    public FacturaRequestRepresentation createInvoice(@RequestBody FacturaRequestRepresentation factura){

        BigDecimal result = (BigDecimal) jdbcTemplate.queryForList("select db1_global.sqnc.nextval from dual").get(0).get("NEXTVAL");

        jdbcTemplate.update("INSERT INTO V_FACTURA(FACTURA_ID,TOTAL_PRET,COMANDA_ID,MODALITATE_PLATA) " +
                        "VALUES(:id, :totalpret, :comandaid, :modalitateplata)", result, factura.getTotalPret(),
                                factura.getComanda_id(), factura.getModalitate_plata());

        return factura;
    }

    @DeleteMapping(path="facturi/{facturaId}")
    public void deleteInvoiceById(@PathVariable int invoiceId){
        jdbcTemplate.update("DELETE FROM V_FACTURA WHERE FACTURA_ID = :id", invoiceId);
    }

}
