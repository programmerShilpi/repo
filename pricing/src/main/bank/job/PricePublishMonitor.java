package main.bank.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import main.bank.BankPriceService;
import main.bank.PriceData;
import main.bank.alert.AlertService;
import main.bank.alert.AlertServiceImpl;
import main.common.PriceListener;
import main.thirdPartyComapny.ThirdPartyCompanyPriceService;

public class PricePublishMonitor implements Runnable {

	private BankPriceService bankPriceUpdateService;
	private ThirdPartyCompanyPriceService thirdPartyCompanyPriceUpdateService;
	private PriceListener bankPriceListener;
	private PriceListener thirdPartyCompanyPriceListener;
	private Map<PriceData, Double> bankPriceMap;
	private Map<String, PriceData> thirdPartyPriceMap;
	private BlockingQueue<PriceData> thirdPartyPriceQueue;
    private AlertService alertService;
	private Thread priceMonitorThread;
	private  Long fwdFreqInMs;
	private  Long stalePriceFreqInMs;
	private List<String> errorMessages;
	private List<String> priceMatchMessages;


	public PricePublishMonitor(BankPriceService bankPriceUpdateService,
			ThirdPartyCompanyPriceService thirdPartyCompanyPriceUpdateService) {
		this.bankPriceUpdateService = bankPriceUpdateService;
		this.thirdPartyCompanyPriceUpdateService = thirdPartyCompanyPriceUpdateService;
		this.bankPriceMap = new ConcurrentHashMap<PriceData, Double>();
		this.thirdPartyPriceMap = new ConcurrentHashMap<String, PriceData>();
		this.thirdPartyPriceQueue=new LinkedBlockingQueue<PriceData>();
		this.alertService=new AlertServiceImpl();
		this.priceMonitorThread = new Thread(this);
		this.fwdFreqInMs=thirdPartyCompanyPriceUpdateService.getfwdFreqInMs();
		this.stalePriceFreqInMs=this.fwdFreqInMs;
		this.errorMessages=new ArrayList<String>();
		this.priceMatchMessages=new ArrayList<String>();
		subscribe();
	}

	
	private void subscribe() {
		this.bankPriceListener = new PriceListener() {
			@Override
			public void priceUpdate(String symbol, double price) {
				PriceData pricedata=new PriceData(symbol,price);
					bankPriceMap.put(pricedata, price);
					//System.out.println("bank :"+pricedata);
				}

		};
		
		this.thirdPartyCompanyPriceListener = new PriceListener() {
			@Override
			public void priceUpdate(String symbol, double price) {
				PriceData priceData = new PriceData(symbol, price);
				thirdPartyPriceMap.put(symbol, priceData);
				//System.out.println("thirdParty :"+priceData);
				
				try {
					thirdPartyPriceQueue.put(priceData);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};
		bankPriceUpdateService.subscribeToBankPriceUpdates(bankPriceListener);
		thirdPartyCompanyPriceUpdateService
				.subscribeToCompanyPriceUpdates(thirdPartyCompanyPriceListener);

	}
	public void setStalePriceFreqInMs(Long stalePriceFreqInMs){
		this.stalePriceFreqInMs=stalePriceFreqInMs;
	}
	public List<String> getErrorMessages() {
		return errorMessages;
	}
	public List<String> getPriceMatchMessages() {
		return priceMatchMessages;
	}


	public void setPriceMatchMessages(List<String> priceMatchMessages) {
		this.priceMatchMessages = priceMatchMessages;
	}
	public void start() {

		
		priceMonitorThread.start();
	}

	public void stop() {
		priceMonitorThread.interrupt();
		
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
				PriceData priceData = thirdPartyPriceQueue.poll();
				if (priceData != null) {
					PriceData thirdPartyPriceData = thirdPartyPriceMap
							.get(priceData.getProductName());
					Double bankPrice = bankPriceMap.get(thirdPartyPriceData);
					Long priceTime = thirdPartyPriceData.getTimestamp();
					Long priceInLast30Secs = new Long(priceTime);
					while (bankPrice == null
							&& priceInLast30Secs >= priceTime - stalePriceFreqInMs) {
						thirdPartyPriceData = new PriceData(
								priceData.getProductName(),
								priceData.getProductPrice(), priceInLast30Secs);
						bankPrice = bankPriceMap.get(thirdPartyPriceData);
						priceInLast30Secs--;
					}
					 if(bankPrice!=null){
					 if (priceData.getProductPrice().compareTo(bankPrice) == 0) {
						 String message=" Product Price matches for Product : "
										+ priceData.getProductName() + " price :"+priceData.getProductPrice();
						priceMatchMessages.add(message);
						alertService.alert(message );
						
					} else  {
						String message=" Alert : Product Price doesnot match for Product : "
								+ priceData.getProductName() + " company price :"+priceData.getProductPrice() +" BankPrice :" +bankPrice;
						errorMessages.add(message);
						alertService.alert(message);
					}
					 }
					 else{
						 String message=" No such price Release from bank in last  "+stalePriceFreqInMs+" ms for product :"
								 + priceData.getProductName() + " company price :"+priceData.getProductPrice();
							errorMessages.add(message);
						 alertService.alert( message);
					 }
					 

				}else {
					//alertService.alert("Alert : No new data sent from bank in last 30 sec");
				}

			}
		}
	

	}


	