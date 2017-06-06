package main.bank;

import java.util.Date;

final public class PriceData {
	final private String productName;
	final private Double productPrice;
	final private Long timestamp;
	
	public PriceData(final String productName,final Double productPrice){
		this.productName=productName;
		this.productPrice=productPrice;
		this.timestamp=new Date().getTime()/1000;
	}
	
	public PriceData(final String productName,final Double productPrice,Long timestamp){
		this.productName=productName;
		this.productPrice=productPrice;
		this.timestamp=timestamp;
	}
	

	public String getProductName() {
		return productName;
	}
	public Double getProductPrice() {
		return productPrice;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((productName == null) ? 0 : productName.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceData other = (PriceData) obj;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (timestamp!=null){
			if(!timestamp.equals(other.timestamp))
				return false;
		}
			
		return true;
	}
	
	@Override
	public String toString() {
		return "PriceData [productName=" + productName + ", productPrice="
				+ productPrice + ", timestamp=" + timestamp + "]";
	}
}
