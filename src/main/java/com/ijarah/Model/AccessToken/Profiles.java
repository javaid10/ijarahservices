package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class Profiles{

	@SerializedName("NafaesIdentity")
	private NafaesIdentity nafaesIdentity;

	public NafaesIdentity getNafaesIdentity(){
		return nafaesIdentity;
	}
}