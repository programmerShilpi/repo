package main.thirdPartyComapny;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import main.bank.BankPriceService;
import main.bank.PriceData;
import main.common.PriceListener;

public class ThirdPartyCompanyPriceServiceImpl implements Runnable,ThirdPartyCompanyPriceService,PriceListener{

	private Thread companyThrottleThread;
	private List<PriceListener> companyPriceListeners;
	private BlockingQueue<PriceData> blockingQueueBankPriceData;
	private BlockingQueue<String> blockingQueueCompanyPriceData;
	private Long delayInGettingBankPriceInMs;
	private Long fwdFreqInMs;
	private Map<String,Double> products;
	private Thread fwdPriceThread;
	
	public ThirdPartyCompanyPriceServiceImpl(BankPriceService bankPriceService){
		this.blockingQueueBankPriceData=new LinkedBlockingQueue<PriceData>();
		this.blockingQueueCompanyPriceData=new LinkedBlockingQueue<String>();
		this.companyPriceListeners=new ArrayList<PriceListener>();
		bankPriceService.subscribeToBankPriceUpdates(this);
		this.companyThrottleThread=new Thread(this,"ThirdPartyCompanyPrice");
		this.fwdFreqInMs=30000l;
		delayInGettingBankPriceInMs=0l;
		products=new ConcurrentHashMap<String,Double>();
		fwdPriceThread=new Thread(new FwdPriceRunnable());
	}
	
	public void setDelayInGettingBankPriceInMs(Long delayInGettingBankPriceInMs) {
		this.delayInGettingBankPriceInMs = delayInGettingBankPriceInMs;
	}


	public void setfwdFreqInMs(Long fwdFreqInMs) {
		this.fwdFreqInMs = fwdFreqInMs;
	}
	public void addPriceListener(PriceListener companyPriceListener) {
		this.companyPriceListeners.add(companyPriceListener);
	}
	public Thread getCompanyThrottleThread(){
		return this.companyThrottleThread;
	}


	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				while(!blockingQueueBankPriceData.isEmpty()){
					PriceData priceData=blockingQueueBankPriceData.take();
					companyPriceCompute(priceData.getProductName(),priceData.getProductPrice());
				}
				//System.out.println("No price came from bank"+System.currentTimeMillis());
				
			} catch (InterruptedException e) {
				System.out.println("Thread was interrupted :"+Thread.currentThread().getName());
			}
			
		}
		
	}
	
	public void start(){
		this.companyThrottleThread.start();
		this.fwdPriceThread.start();
	}
	
	public void stop(){
		this.companyThrottleThread.interrupt();
	}

	@Override
	public void companyPriceCompute(String symbol, double price) throws InterruptedException{
		blockingQueueCompanyPriceData.put(symbol);
		products.put(symbol, price);
	 }
	 
	 
	private void notifyCompanyPriceListener(String symbol) {
		
		for(PriceListener companyPriceListener:companyPriceListeners){
		companyPriceListener.priceUpdate(symbol, products.get(symbol));
		}
	}
	
	@Override
	public void priceUpdate(String symbol, double price) {
		try {
			Thread.sleep(delayInGettingBankPriceInMs);
		} catch (InterruptedException e) {
			System.out.println("interrupted"+ Thread.currentThread().getName());
		}
		final PriceData priceData=new PriceData(symbol,price);
		try {
			blockingQueueBankPriceData.put(priceData);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void subscribeToBankPriceUpdates(PriceListener priceListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribeToCompanyPriceUpdates(PriceListener priceListener) {
		this.companyPriceListeners.add(priceListener);
		
	}

	@Override
	public Long getfwdFreqInMs() {
		return this.fwdFreqInMs;
	}
	
	class FwdPriceRunnable implements Runnable{

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				try {
					notifyCompanyPriceListener(blockingQueueCompanyPriceData.take());
					Thread.sleep(fwdFreqInMs);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
