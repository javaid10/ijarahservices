package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class Params{

	@SerializedName("access_token")
	private String accessToken;

	@SerializedName("refresh_token")
	private String refreshToken;

	@SerializedName("_provider_token")
	private String providerToken;

	@SerializedName("content_type")
	private String contentType;

	@SerializedName("phone")
	private String phone;

	@SerializedName("scope")
	private String scope;

	@SerializedName("raw_response")
	private String rawResponse;

	@SerializedName("token_type")
	private String tokenType;

	@SerializedName("display_name")
	private String displayName;

	@SerializedName("expires_in")
	private int expiresIn;

	public String getAccessToken(){
		return accessToken;
	}

	public String getRefreshToken(){
		return refreshToken;
	}

	public String getProviderToken(){
		return providerToken;
	}

	public String getContentType(){
		return contentType;
	}

	public String getPhone(){
		return phone;
	}

	public String getScope(){
		return scope;
	}

	public String getRawResponse(){
		return rawResponse;
	}

	public String getTokenType(){
		return tokenType;
	}

	public String getDisplayName(){
		return displayName;
	}

	public int getExpiresIn(){
		return expiresIn;
	}
}