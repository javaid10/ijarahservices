package com.ijarah.Model.ScorecardS2;

import com.google.gson.annotations.SerializedName;

public class Header{

	@SerializedName("transactionStatus")
	private String transactionStatus;

	@SerializedName("audit")
	private Audit audit;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	public String getTransactionStatus(){
		return transactionStatus;
	}

	public Audit getAudit(){
		return audit;
	}

	public String getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}
}