package iss.nus.edu.sg.PurchaseOrder.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import iss.nus.edu.sg.PurchaseOrder.model.Quotation;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class QuotationService {
    
    private final String URL = "https://quotation.chuklee.com%s";
    private final String QUOTE = "/quotation";
    
    public Optional<Quotation> getQuotations(List<String> items) {
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();

        for(String item : items) {
            jArrayBuilder.add(item);
        }

        RequestEntity<String> req = RequestEntity
                                .post(URL.formatted(QUOTE))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jArrayBuilder.build().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

        if(resp.getStatusCodeValue() >= 400)
            return Optional.empty();

        String quoteString = resp.getBody();
        System.out.println(">>> RESPONSE: " + quoteString);

        InputStream is = new ByteArrayInputStream(quoteString.getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject quoteObj = reader.readObject();

        Quotation q = new Quotation();
        q.setQuoteId(quoteObj.getString("quoteId"));

        JsonArray quotationsArray = quoteObj.getJsonArray("quotations");
        for(int i = 0; i < quotationsArray.size(); i++) {
            JsonObject item = quotationsArray.getJsonObject(i);
            String itemName = item.getString("item");
            Double unitPrice = item.getJsonNumber("unitPrice").doubleValue();
            q.addQuotation(itemName, unitPrice.floatValue());
        }

        return Optional.of(q);
    }
}
    


