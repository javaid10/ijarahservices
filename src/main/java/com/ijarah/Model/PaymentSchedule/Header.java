package com.ijarah.Model.PaymentSchedule;

import com.google.gson.annotations.SerializedName;

public class Header{

	@SerializedName("audit")
	private Audit audit;

	@SerializedName("page_start")
	private int pageStart;

	@SerializedName("page_token")
	private String pageToken;

	@SerializedName("total_size")
	private int totalSize;

	@SerializedName("page_size")
	private int pageSize;

	@SerializedName("status")
	private String status;

	public Audit getAudit(){
		return audit;
	}

	public int getPageStart(){
		return pageStart;
	}

	public String getPageToken(){
		return pageToken;
	}

	public int getTotalSize(){
		return totalSize;
	}

	public int getPageSize(){
		return pageSize;
	}

	public String getStatus(){
		return status;
	}
}