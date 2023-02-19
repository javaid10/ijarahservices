package com.ijarah.Model.CREDIT_CARD_PRODUCTS;

import com.google.gson.annotations.SerializedName;

public class CreditcardproductsItem{

	@SerializedName("credit_card_products_value")
	private String creditCardProductsValue;

	public String getCreditCardProductsValue(){
		return creditCardProductsValue;
	}
}