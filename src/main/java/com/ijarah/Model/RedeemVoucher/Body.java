package com.ijarah.Model.RedeemVoucher;

import com.google.gson.annotations.SerializedName;

public class Body{

	@SerializedName("voucherStatus")
	private String voucherStatus;

	public String getVoucherStatus(){
		return voucherStatus;
	}
}