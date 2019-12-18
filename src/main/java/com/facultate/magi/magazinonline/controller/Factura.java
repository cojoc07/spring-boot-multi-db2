package com.facultate.magi.magazinonline.controller;

import com.facultate.magi.magazinonline.controller.dto.ClientRequestRepresentation;
import com.facultate.magi.magazinonline.controller.dto.FacturaRequestRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@Transactional
@SuppressWarnings("unchecked")
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
    public Object createInvoice(@RequestBody FacturaRequestRepresentation factura) throws Exception {

        BigDecimal result = (BigDecimal) jdbcTemplate.queryForList("select db1_global.sqnc.nextval from dual").get(0).get("NEXTVAL");

        ModalitatePlata mp = new ModalitatePlata(jdbcTemplate);
        Map<String, Object> res = (Map<String, Object>) mp.getPaymentMethodsById(factura.getModalitate_plata());
        if (!res.containsKey("NOT FOUND ID")){
            Comanda com = new Comanda(jdbcTemplate);
            Map<String, Object> res2 = (Map<String, Object>)com.getOrderById(factura.getComanda_id());
            if (!res2.containsKey("NOT FOUND ID")){
                jdbcTemplate.update("INSERT INTO V_FACTURA(FACTURA_ID,TOTAL_PRET,COMANDA_ID,MODALITATE_PLATA) " +
                                "VALUES(:id, :totalpret, :comandaid, :modalitateplata)", result, factura.getTotalPret(),
                        factura.getComanda_id(), factura.getModalitate_plata());
                return this.getBillById(result.toString());
            } else {
                throw new Exception("Comanda specificata nu exista");
            }
        } else {
            throw new Exception("Modalitatea de plata specificata nu exista");
        }
    }

    @DeleteMapping(path="facturi/{facturaId}")
    public void deleteInvoiceById(@PathVariable int invoiceId){
        jdbcTemplate.update("DELETE FROM V_FACTURA WHERE FACTURA_ID = :id", invoiceId);
    }

}
