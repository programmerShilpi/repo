package bank;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThirdPartyCompanyPriceServiceImpl implements Runnable,ThirdPartyCompanyPriceService,PriceListener{

	private Thread companyThrottleThread;
	private PriceListener companyPriceListener;
	private BlockingQueue<PriceData> blockingQueueBankPriceData;
	private Long delayInGettingBankPriceInMs;
	private Long fwdFreqInMs;
	
	ThirdPartyCompanyPriceServiceImpl(BankPriceService bankPriceService){
		this.blockingQueueBankPriceData=new LinkedBlockingQueue<PriceData>();
		bankPriceService.subscribeToBankPriceUpdates(this);
		this.companyThrottleThread=new Thread(this,"ThirdPartyCompanyPrice");
		this.fwdFreqInMs=30000l;
		delayInGettingBankPriceInMs=0l;
	}
	
	public void setDelayInGettingBankPriceInMs(Long delayInGettingBankPriceInMs) {
		this.delayInGettingBankPriceInMs = delayInGettingBankPriceInMs;
	}


	public void setfwdFreqInMs(Long fwdFreqInMs) {
		this.fwdFreqInMs = fwdFreqInMs;
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
				Thread.sleep(fwdFreqInMs);
			} catch (InterruptedException e) {
				System.out.println("Thread was interrupted :"+Thread.currentThread().getName());
			}
			
		}
		
	}
	
	public void start(){
		this.companyThrottleThread.start();
	}
	
	public void stop(){
		this.companyThrottleThread.interrupt();
	}

	@Override
	public void companyPriceCompute(String symbol, double price){
		 if(companyPriceListener!=null)
			 notifyCompanyPriceListener(symbol,price);
	 }
	 
	 
	private void notifyCompanyPriceListener(String symbol, double price) {
		companyPriceListener.priceUpdate(symbol, price);
		
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
		this.companyPriceListener=priceListener;
		
	}

	@Override
	public Long getfwdFreqInMs() {
		return this.fwdFreqInMs;
	}
}
