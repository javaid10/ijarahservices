package com.ijarah.Model.NON_FINANCIAL_PRODUCTS;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NonFinancialProductResponse{

	@SerializedName("nonfinancialproducts")
	private List<NonfinancialproductsItem> nonfinancialproducts;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public List<NonfinancialproductsItem> getNonfinancialproducts(){
		return nonfinancialproducts;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}