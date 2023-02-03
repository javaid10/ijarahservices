package com.ijarah.Model.Retailers;

import com.google.gson.annotations.SerializedName;

public class BodyItem{

	@SerializedName("retailerName")
	private String retailerName;

	@SerializedName("commission")
	private String commission;

	@SerializedName("retailerId")
	private String retailerId;

	@SerializedName("category")
	private String category;

	@SerializedName("accountNumber")
	private String accountNumber;

	@SerializedName("status")
	private String status;

	public String getRetailerName(){
		return retailerName;
	}

	public String getCommission(){
		return commission;
	}

	public String getRetailerId(){
		return retailerId;
	}

	public String getCategory(){
		return category;
	}

	public String getAccountNumber(){
		return accountNumber;
	}

	public String getStatus(){
		return status;
	}
}