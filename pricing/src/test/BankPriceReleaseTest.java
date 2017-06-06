package test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import main.bank.BankPriceService;
import main.bank.BankPriceServiceImpl;

import org.junit.Test;

public class BankPriceReleaseTest {

	static BankPriceService bankPriceService=null;
	
	@Test
	public void test() {
		//fail("Not yet implemented");
	}
	@Test
	public void testComputePrice() {
		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		bankPriceService=new BankPriceServiceImpl();
		bankPriceService.setProductNames(productWithPrice);
		bankPriceService.bankPriceCompute("D");
		assertEquals(5.9, productWithPrice.get("D"), 0.001);
		
	}
}
