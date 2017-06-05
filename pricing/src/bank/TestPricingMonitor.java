package bank;

import java.util.HashMap;
import java.util.Map;

public class TestPricingMonitor {
	static BankPriceService bankPriceService=null;
	static ThirdPartyCompanyPriceService thirdPartyCompanyPriceService=null;
	static PricePublishMonitor pricePublishMonitor=null;
	
	
	public static void main(String[] args) {
		testPricingMatches();
		//testPricingNotFoundInLast30Secs();
		//testStalePricingFound();
	    
	    
	}

	private static void testStalePricingFound() {

		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("A",1.9);
		productWithPrice.put("B",2.9);
		productWithPrice.put("C",3.9);
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		productWithPrice.put("F",6.9);
		productWithPrice.put("G",7.9);
		bankPriceService=new BankPriceServiceImpl();
		thirdPartyCompanyPriceService=new ThirdPartyCompanyPriceServiceImpl(bankPriceService);
		pricePublishMonitor=new PricePublishMonitor(bankPriceService,thirdPartyCompanyPriceService);
		bankPriceService.setProductNames(productWithPrice);
		thirdPartyCompanyPriceService.setfwdFreqInMs(30l);
		pricePublishMonitor.setStalePriceFreqInMs(20l);
		startServices();
		
	
	}

	private static void testPricingNotFoundInLast30Secs() {
		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("A",1.9);
		productWithPrice.put("B",2.9);
		productWithPrice.put("C",3.9);
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		productWithPrice.put("F",6.9);
		productWithPrice.put("G",7.9);
		bankPriceService=new BankPriceServiceImpl();
		thirdPartyCompanyPriceService=new ThirdPartyCompanyPriceServiceImpl(bankPriceService);
		pricePublishMonitor=new PricePublishMonitor(bankPriceService,thirdPartyCompanyPriceService);
		bankPriceService.setProductNames(productWithPrice);
		thirdPartyCompanyPriceService.setfwdFreqInMs(30l);
		thirdPartyCompanyPriceService.setDelayInGettingBankPriceInMs(40l);
		startServices();
		
	}

	private static void testPricingMatches() {
		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("A",1.9);
		productWithPrice.put("B",2.9);
		productWithPrice.put("C",3.9);
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		productWithPrice.put("F",6.9);
		productWithPrice.put("G",7.9);
		bankPriceService=new BankPriceServiceImpl();
		thirdPartyCompanyPriceService=new ThirdPartyCompanyPriceServiceImpl(bankPriceService);
		pricePublishMonitor=new PricePublishMonitor(bankPriceService,thirdPartyCompanyPriceService);
		bankPriceService.setProductNames(productWithPrice);
		thirdPartyCompanyPriceService.setfwdFreqInMs(30l);
		
		startServices();
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopServices();
		
	}

	private static void startServices() {
		bankPriceService.start();
		thirdPartyCompanyPriceService.start();
	    pricePublishMonitor.start();
	}
	
	private static void stopServices() {
		thirdPartyCompanyPriceService.stop();
		bankPriceService.stop();
		pricePublishMonitor.stop();
	}

}
