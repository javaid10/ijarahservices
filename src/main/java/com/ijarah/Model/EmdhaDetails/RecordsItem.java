package com.ijarah.Model.EmdhaDetails;

import com.google.gson.annotations.SerializedName;

public class RecordsItem{

	@SerializedName("currentAppId")
	private String currentAppId;

	@SerializedName("FullName")
	private String fullName;

	@SerializedName("ArFullName")
	private String arFullName;

	@SerializedName("Value")
	private String value;

	@SerializedName("id")
	private String id;

	public String getCurrentAppId(){
		return currentAppId;
	}

	public String getFullName(){
		return fullName;
	}

	public String getArFullName(){
		return arFullName;
	}

	public String getValue(){
		return value;
	}

	public String getId(){
		return id;
	}
}