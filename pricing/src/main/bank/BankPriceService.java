package main.bank;

import java.util.Map;

import main.common.PriceUpdateService;

public interface BankPriceService extends PriceUpdateService{
	void start();

	void setProductNames(Map<String,Double> products);

	void stop();
	
	 void bankPriceCompute(String symbol) ;

	Map<String, Double> getProducts();

	void setFwdFreqInMs(Long fwdFreqInMs);
}
