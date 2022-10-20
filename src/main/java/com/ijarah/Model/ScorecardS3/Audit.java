package com.ijarah.Model.ScorecardS3;

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

	public void setT24Time(int t24Time){
		this.t24Time = t24Time;
	}

	public int getT24Time(){
		return t24Time;
	}

	public void setResponseParseTime(int responseParseTime){
		this.responseParseTime = responseParseTime;
	}

	public int getResponseParseTime(){
		return responseParseTime;
	}

	public void setRequestParseTime(int requestParseTime){
		this.requestParseTime = requestParseTime;
	}

	public int getRequestParseTime(){
		return requestParseTime;
	}

	public void setVersionNumber(String versionNumber){
		this.versionNumber = versionNumber;
	}

	public String getVersionNumber(){
		return versionNumber;
	}

	@Override
 	public String toString(){
		return 
			"Audit{" + 
			"t24_time = '" + t24Time + '\'' + 
			",responseParse_time = '" + responseParseTime + '\'' + 
			",requestParse_time = '" + requestParseTime + '\'' + 
			",versionNumber = '" + versionNumber + '\'' + 
			"}";
		}
}