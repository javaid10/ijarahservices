package com.ijarah.Model.NationalAddressModel;

import com.google.gson.annotations.SerializedName;

public class Ns1GetCitizenAddressInfoResponse{

	@SerializedName("CitizenAddressInfoResult")
	private CitizenAddressInfoResult citizenAddressInfoResult;

	public CitizenAddressInfoResult getCitizenAddressInfoResult(){
		return citizenAddressInfoResult;
	}
}