package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class ProviderTokens{

	@SerializedName("NafaesIdentity")
	private String nafaesIdentity;

	public String getNafaesIdentity(){
		return nafaesIdentity;
	}
}