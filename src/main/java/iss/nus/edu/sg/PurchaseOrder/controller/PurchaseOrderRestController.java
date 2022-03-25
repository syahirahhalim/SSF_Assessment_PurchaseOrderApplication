package iss.nus.edu.sg.PurchaseOrder.controller;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iss.nus.edu.sg.PurchaseOrder.model.Quotation;
import iss.nus.edu.sg.PurchaseOrder.service.QuotationService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@RestController
@RequestMapping(path="/api")
public class PurchaseOrderRestController {
    
    @Autowired
    private QuotationService quotationSvc;
    
    @PostMapping(path="/po", consumes = MediaType.APPLICATION_JSON_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postPurchaseOrder(@RequestBody String payload) {

        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject poObj = reader.readObject();

        String name = poObj.getString("name");
        JsonArray lineItems = poObj.getJsonArray("lineItems");
        Map<String, Integer> itemMap = new HashMap<>();

        for(int i = 0; i < lineItems.size(); i++) {
            JsonObject item = lineItems.getJsonObject(i);
            itemMap.put(item.getString("item"), item.getInt("quantity"));
        }

        Optional<Quotation> quotationOpt = quotationSvc.getQuotations(new ArrayList<>(itemMap.keySet()));

        if(quotationOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("{}");
        }

        Quotation quotation = quotationOpt.get();
        Float total = 0.0f;
        for(Entry<String, Integer> entry : itemMap.entrySet()) {
            Float unitPrice = quotation.getQuotation(entry.getKey());
            total += entry.getValue() * unitPrice;
        }

        JsonObject jObj = Json.createObjectBuilder()
                                .add("invoiceId", quotation.getQuoteId())
                                .add("name", name)
                                .add("total", total)
                                .build();

        return ResponseEntity.ok().body(jObj.toString());
    }
}
