package com.ijarah.Model.LoanDetailsForExpiry;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoanDetailsforExpiryResponse{

	@SerializedName("records")
	private List<RecordsItem> records;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public List<RecordsItem> getRecords(){
		return records;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}