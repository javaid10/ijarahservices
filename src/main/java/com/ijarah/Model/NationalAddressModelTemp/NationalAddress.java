package com.ijarah.Model.NationalAddressModelTemp;

import com.google.gson.annotations.SerializedName;

public class NationalAddress{

	@SerializedName("addressListList")
	private String addressListList;

	@SerializedName("CitizenAddressInfoResult")
	private CitizenAddressInfoResult citizenAddressInfoResult;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("ns1:getCitizenAddressInfoResponse")
	private Ns1GetCitizenAddressInfoResponse ns1GetCitizenAddressInfoResponse;

	@SerializedName("logId")
	private String logId;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public String getAddressListList(){
		return addressListList;
	}

	public CitizenAddressInfoResult getCitizenAddressInfoResult(){
		return citizenAddressInfoResult;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public Ns1GetCitizenAddressInfoResponse getNs1GetCitizenAddressInfoResponse(){
		return ns1GetCitizenAddressInfoResponse;
	}

	public String getLogId(){
		return logId;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}