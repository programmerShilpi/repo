package main.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.common.PriceListener;

public class BankPriceServiceImpl implements Runnable, BankPriceService
		 {
	private Map<String,Double> products;
	private Thread computePriceThread;
	private List<PriceListener> bankPriceUpdateListeners;
	private static int constantPriceIncerement=1;
	private Long fwdFreqInMs;

	public BankPriceServiceImpl() {
		this.products = new HashMap<String,Double>();
		this.bankPriceUpdateListeners=new ArrayList<PriceListener>();
		this.fwdFreqInMs=1000l;
		this.computePriceThread = new Thread(this,"BankPriceThread");
	}
	
	
	public void setFwdFreqInMs(Long fwdFreqInMs) {
		this.fwdFreqInMs = fwdFreqInMs;
	}


	public void setProductNames(Map<String,Double> products){
		this.products=products;
	}
	
	public void addPriceListener(PriceListener bankPriceUpdateListener) {
		this.bankPriceUpdateListeners.add(bankPriceUpdateListener);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Iterator<String> it=products.keySet().iterator();
			
			while(it.hasNext()){
				String prodName=it.next();
				bankPriceCompute(prodName);
			}
			try {
				Thread.sleep(fwdFreqInMs);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void start() {
		
		this.computePriceThread.start();
	}

	public void stop() {
		this.computePriceThread.interrupt();
	}

	 public void bankPriceCompute(String symbol) {
		Double bankPrice=products.get(symbol)+constantPriceIncerement;
		products.put(symbol,bankPrice);
		notifyBankPriceListener(symbol, bankPrice);
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


	@Override
	public Map<String, Double> getProducts() {
		return this.products;
	}

}
