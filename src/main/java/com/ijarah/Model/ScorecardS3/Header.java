package com.ijarah.Model.ScorecardS3;

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

	public void setTransactionStatus(String transactionStatus){
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionStatus(){
		return transactionStatus;
	}

	public void setAudit(Audit audit){
		this.audit = audit;
	}

	public Audit getAudit(){
		return audit;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Header{" + 
			"transactionStatus = '" + transactionStatus + '\'' + 
			",audit = '" + audit + '\'' + 
			",id = '" + id + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}