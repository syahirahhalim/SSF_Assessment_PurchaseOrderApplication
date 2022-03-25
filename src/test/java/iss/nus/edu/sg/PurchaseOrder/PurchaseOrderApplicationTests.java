package iss.nus.edu.sg.PurchaseOrder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import iss.nus.edu.sg.PurchaseOrder.model.Quotation;
import iss.nus.edu.sg.PurchaseOrder.service.QuotationService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseOrderApplicationTests {

    @Autowired
	private QuotationService quotationSvc;

	@Autowired
	private MockMvc mvc;

	@Test
	void contextLoads() {
		List<String> items = new ArrayList<>();
		items.add("durian");
		items.add("plum");
		items.add("pear");

		Optional<Quotation> opt = quotationSvc.getQuotations(items);
		assertTrue(opt.isEmpty());

	}
    
}







