package com.ijarah.Model.MORTGAGE_PRODUCT;

import com.google.gson.annotations.SerializedName;

public class MortgageproductItem{

	@SerializedName("mortgage_product_value")
	private String mortgageProductValue;

	public String getMortgageProductValue(){
		return mortgageProductValue;
	}
}