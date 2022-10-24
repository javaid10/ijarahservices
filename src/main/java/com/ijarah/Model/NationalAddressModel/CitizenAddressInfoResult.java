package com.ijarah.Model.NationalAddressModel;

import com.google.gson.annotations.SerializedName;

public class CitizenAddressInfoResult{

	@SerializedName("addressListList")
	private AddressListList addressListList;

	@SerializedName("logId")
	private int logId;

	public AddressListList getAddressListList(){
		return addressListList;
	}

	public int getLogId(){
		return logId;
	}
}