package com.ijarah.Model.CREDIT_CARD_PRODUCTS;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditCardProductsResponse{

	@SerializedName("creditcardproducts")
	private List<CreditcardproductsItem> creditcardproducts;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public List<CreditcardproductsItem> getCreditcardproducts(){
		return creditcardproducts;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}