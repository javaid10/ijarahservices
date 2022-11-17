package com.ijarah.Model.EmdhaDetails;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EmdhaDBResponse{

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