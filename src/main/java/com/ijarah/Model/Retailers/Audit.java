package com.ijarah.Model.Retailers;

import com.google.gson.annotations.SerializedName;

public class Audit{

	@SerializedName("T24_time")
	private int t24Time;

	@SerializedName("responseParse_time")
	private int responseParseTime;

	@SerializedName("requestParse_time")
	private int requestParseTime;

	public int getT24Time(){
		return t24Time;
	}

	public int getResponseParseTime(){
		return responseParseTime;
	}

	public int getRequestParseTime(){
		return requestParseTime;
	}
}