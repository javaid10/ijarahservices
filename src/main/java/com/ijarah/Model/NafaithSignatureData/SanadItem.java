package com.ijarah.Model.NafaithSignatureData;

import com.google.gson.annotations.SerializedName;

public class SanadItem{

	@SerializedName("total_value")
	private int totalValue;

	@SerializedName("reference_id")
	private String referenceId;

	@SerializedName("due_date")
	private String dueDate;

	@SerializedName("due_type")
	private String dueType;

	public void setTotalValue(int totalValue){
		this.totalValue = totalValue;
	}

	public double getTotalValue(){
		return totalValue;
	}

	public void setReferenceId(String referenceId){
		this.referenceId = referenceId;
	}

	public String getReferenceId(){
		return referenceId;
	}

	public void setDueDate(String dueDate){
		this.dueDate = dueDate;
	}

	public String getDueDate(){
		return dueDate;
	}

	public void setDueType(String dueType){
		this.dueType = dueType;
	}

	public String getDueType(){
		return dueType;
	}

	@Override
 	public String toString(){
		return 
			"SanadItem{" + 
			"total_value = '" + totalValue + '\'' + 
			",reference_id = '" + referenceId + '\'' + 
			",due_date = '" + dueDate + '\'' + 
			",due_type = '" + dueType + '\'' + 
			"}";
		}
}