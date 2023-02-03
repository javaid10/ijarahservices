package com.ijarah.Model.RedeemVoucher;

import com.google.gson.annotations.SerializedName;

public class Audit{

	@SerializedName("T24_time")
	private int t24Time;

	@SerializedName("responseParse_time")
	private int responseParseTime;

	@SerializedName("requestParse_time")
	private int requestParseTime;

	@SerializedName("versionNumber")
	private String versionNumber;

	public int getT24Time(){
		return t24Time;
	}

	public int getResponseParseTime(){
		return responseParseTime;
	}

	public int getRequestParseTime(){
		return requestParseTime;
	}

	public String getVersionNumber(){
		return versionNumber;
	}
}