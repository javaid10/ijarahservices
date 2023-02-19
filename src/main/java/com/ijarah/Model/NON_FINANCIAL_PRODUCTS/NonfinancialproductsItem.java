package com.ijarah.Model.NON_FINANCIAL_PRODUCTS;

import com.google.gson.annotations.SerializedName;

public class NonfinancialproductsItem{

	@SerializedName("non_financial_products_value")
	private String nonFinancialProductsValue;

	public String getNonFinancialProductsValue(){
		return nonFinancialProductsValue;
	}
}