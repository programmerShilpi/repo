package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BankPriceServiceImpl implements Runnable, BankPriceService
		 {
	private Map<String,Double> products;
	private Thread computePriceThread;
	private List<PriceListener> bankPriceUpdateListeners;

	BankPriceServiceImpl() {
		this.products = new HashMap<String,Double>();
		this.bankPriceUpdateListeners=new ArrayList<PriceListener>();
		this.computePriceThread = new Thread(this,"BankPriceThread");
	}
	
	
	public void setProductNames(Map<String,Double> products){
		this.products=products;
	}
	
	public void addPriceListener(PriceListener bankPriceUpdateListener) {
		this.bankPriceUpdateListeners.add(bankPriceUpdateListener);
	}

	@Override
	public void run() {
		int count=0;
		//while (!Thread.currentThread().isInterrupted()) {
			Iterator<String> it=products.keySet().iterator();
			count++;
			while(it.hasNext()){
				String prodName=it.next();
				bankPriceCompute(prodName, products.get(prodName)+count);
			}
			
		//}

	}

	public void start() {
		
		this.computePriceThread.start();
	}

	public void stop() {
		this.computePriceThread.interrupt();
	}

	public void bankPriceCompute(String symbol, double price) {

		notifyBankPriceListener(symbol, price);
	}

	public void notifyBankPriceListener(String symbol, double price) {
		for(PriceListener bankPriceUpdateListener:bankPriceUpdateListeners){
			bankPriceUpdateListener.priceUpdate(symbol, price);
		}

	}

	@Override
	public void subscribeToBankPriceUpdates(PriceListener priceListener) {
		this.bankPriceUpdateListeners.add(priceListener);
	
	}

	@Override
	public void subscribeToCompanyPriceUpdates(PriceListener priceListener) {
		// TODO Auto-generated method stub

	}

}
