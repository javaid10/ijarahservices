package com.ijarah.Model.PaymentSchedule;

import com.google.gson.annotations.SerializedName;

public class BodyItem{

	@SerializedName("outstandingAmount")
	private int outstandingAmount;

	@SerializedName("scheduleType")
	private String scheduleType;

	@SerializedName("sadadNumber")
	private String sadadNumber;

	@SerializedName("interestAmount")
	private double interestAmount;

	@SerializedName("paymentDate")
	private String paymentDate;

	@SerializedName("principalAmount")
	private double principalAmount;

	@SerializedName("totalAmount")
	private double totalAmount;

	@SerializedName("chargeAmount")
	private int chargeAmount;

	@SerializedName("taxAmount")
	private String taxAmount;

	public int getOutstandingAmount(){
		return outstandingAmount;
	}

	public String getScheduleType(){
		return scheduleType;
	}

	public String getSadadNumber(){
		return sadadNumber;
	}

	public double getInterestAmount(){
		return interestAmount;
	}

	public String getPaymentDate(){
		return paymentDate;
	}

	public double getPrincipalAmount(){
		return principalAmount;
	}

	public double getTotalAmount(){
		return totalAmount;
	}

	public int getChargeAmount(){
		return chargeAmount;
	}

	public String getTaxAmount(){
		return taxAmount;
	}
}