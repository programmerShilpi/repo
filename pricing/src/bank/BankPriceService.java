package bank;

import java.util.Map;

public interface BankPriceService extends PriceUpdateService{
	void bankPriceCompute(String symbol, double price);

	void start();

	void setProductNames(Map<String,Double> products);

	void stop();
}
