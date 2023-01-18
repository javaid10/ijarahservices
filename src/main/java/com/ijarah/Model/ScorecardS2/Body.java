package com.ijarah.Model.ScorecardS2;

import com.google.gson.annotations.SerializedName;

public class Body{

	@SerializedName("applicationCategory")
	private String applicationCategory;

	@SerializedName("dataTypeScore1")
	private String dataTypeScore1;

	@SerializedName("ratekey")
	private String ratekey;

	@SerializedName("tenor")
	private int tenor;

	@SerializedName("applicationStatus")
	private String applicationStatus;

	@SerializedName("salaryAmount")
	private double salaryAmount;

	@SerializedName("dataTypes1")
	private String dataTypes1;

	@SerializedName("retailer")
	private String retailer;

	@SerializedName("dataType")
	private String dataType;

	@SerializedName("dataVal1")
	private String dataVal1;

	@SerializedName("calculate")
	private String calculate;

	@SerializedName("loanRate")
	private double loanRate;

	public String getApplicationCategory(){
		return applicationCategory;
	}

	public String getDataTypeScore1(){
		return dataTypeScore1;
	}

	public String getRatekey(){
		return ratekey;
	}

	public int getTenor(){
		return tenor;
	}

	public String getApplicationStatus(){
		return applicationStatus;
	}

	public double getSalaryAmount(){
		return salaryAmount;
	}

	public String getDataTypes1(){
		return dataTypes1;
	}

	public String getRetailer(){
		return retailer;
	}

	public String getDataType(){
		return dataType;
	}

	public String getDataVal1(){
		return dataVal1;
	}

	public String getCalculate(){
		return calculate;
	}

	public double getLoanRate(){
		return loanRate;
	}
}