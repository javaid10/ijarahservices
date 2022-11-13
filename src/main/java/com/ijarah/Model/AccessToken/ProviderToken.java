package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class ProviderToken{

	@SerializedName("provider")
	private String provider;

	@SerializedName("params")
	private Params params;

	@SerializedName("value")
	private String value;

	public String getProvider(){
		return provider;
	}

	public Params getParams(){
		return params;
	}

	public String getValue(){
		return value;
	}
}