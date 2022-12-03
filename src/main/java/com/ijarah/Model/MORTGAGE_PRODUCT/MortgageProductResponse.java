package com.ijarah.Model.MORTGAGE_PRODUCT;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MortgageProductResponse{

	@SerializedName("mortgageproduct")
	private List<MortgageproductItem> mortgageproduct;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public List<MortgageproductItem> getMortgageproduct(){
		return mortgageproduct;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}