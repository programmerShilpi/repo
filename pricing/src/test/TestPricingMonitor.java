package test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.bank.BankPriceService;
import main.bank.BankPriceServiceImpl;
import main.bank.job.PricePublishMonitor;
import main.thirdPartyComapny.ThirdPartyCompanyPriceService;
import main.thirdPartyComapny.ThirdPartyCompanyPriceServiceImpl;

public class TestPricingMonitor {
	 
	 BankPriceService bankPriceService=null;
	 ThirdPartyCompanyPriceService thirdPartyCompanyPriceService=null;
	 PricePublishMonitor pricePublishMonitor=null;

	@Before
	public void setUp() throws Exception {
		bankPriceService=new BankPriceServiceImpl();
		thirdPartyCompanyPriceService=new ThirdPartyCompanyPriceServiceImpl(bankPriceService);
		pricePublishMonitor=new PricePublishMonitor(bankPriceService,thirdPartyCompanyPriceService);
			}

	
	@Test
	public void testPricingMatches() {
		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("A",1.9);
		productWithPrice.put("B",2.9);
		productWithPrice.put("C",3.9);
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		productWithPrice.put("F",6.9);
		productWithPrice.put("G",7.9);
		bankPriceService.setProductNames(productWithPrice);
		bankPriceService.setFwdFreqInMs(1000l);
		thirdPartyCompanyPriceService.setfwdFreqInMs(3000l);
		
		startServices();
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(pricePublishMonitor.getErrorMessages().size(),0);
		assertNotEquals(pricePublishMonitor.getPriceMatchMessages().size(),0);
		stopServices();
		
	}

	@Test
	public void testCompanyFwdLatestDataInLast30Secs() {
		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("A",1.9);
		productWithPrice.put("B",2.9);
		productWithPrice.put("C",3.9);
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		productWithPrice.put("F",6.9);
		productWithPrice.put("G",7.9);
		bankPriceService.setProductNames(productWithPrice);
		thirdPartyCompanyPriceService.setfwdFreqInMs(3000l);
		thirdPartyCompanyPriceService.setDelayInGettingBankPriceInMs(4000l);
		startServices();
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(pricePublishMonitor.getErrorMessages().size(),0);
		
	}

	
	@Test
	public void testCompanyNotGotPriceInLast30Secs() {
		Map<String,Double> productWithPrice=new HashMap<String,Double>();
		productWithPrice.put("A",1.9);
		productWithPrice.put("B",2.9);
		productWithPrice.put("C",3.9);
		productWithPrice.put("D",4.9);
		productWithPrice.put("E",5.9);
		productWithPrice.put("F",6.9);
		productWithPrice.put("G",7.9);
		bankPriceService.setProductNames(productWithPrice);
		thirdPartyCompanyPriceService.setfwdFreqInMs(30000l);
		thirdPartyCompanyPriceService.setDelayInGettingBankPriceInMs(40000l);
		startServices();
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(pricePublishMonitor.getPriceMatchMessages().size(),0);
	}
	
	
	private  void startServices() {
		bankPriceService.start();
		thirdPartyCompanyPriceService.start();
	    pricePublishMonitor.start();
	}
	
	private  void stopServices() {
		thirdPartyCompanyPriceService.stop();
		bankPriceService.stop();
		pricePublishMonitor.stop();
	}
	@After
	public void tearDown() throws Exception {
	}
}
