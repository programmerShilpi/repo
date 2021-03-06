package bank;

public interface ThirdPartyCompanyPriceService extends PriceUpdateService{
	public void companyPriceCompute(String symbol, double price);
	public void start();
	public void stop();
	public void setDelayInGettingBankPriceInMs(Long extraSleepMs);
	public void setfwdFreqInMs(Long regularsleepMs);
	public Long getfwdFreqInMs();
	public Thread getCompanyThrottleThread();
}
