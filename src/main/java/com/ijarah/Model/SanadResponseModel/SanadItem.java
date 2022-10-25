package com.ijarah.Model.SanadResponseModel;

import com.google.gson.annotations.SerializedName;

public class SanadItem{

	@SerializedName("number")
	private String number;

	@SerializedName("total_value")
	private String totalValue;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("reference_id")
	private String referenceId;

	@SerializedName("due_date")
	private String dueDate;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private String id;

	@SerializedName("due_type")
	private String dueType;

	@SerializedName("status")
	private String status;

	public String getNumber(){
		return number;
	}

	public String getTotalValue(){
		return totalValue;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getReferenceId(){
		return referenceId;
	}

	public String getDueDate(){
		return dueDate;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getId(){
		return id;
	}

	public String getDueType(){
		return dueType;
	}

	public String getStatus(){
		return status;
	}
}