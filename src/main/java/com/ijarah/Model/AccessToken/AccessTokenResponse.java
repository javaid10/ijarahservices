package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class AccessTokenResponse{

	@SerializedName("provider_tokens")
	private ProviderTokens providerTokens;

	@SerializedName("refresh_token")
	private String refreshToken;

	@SerializedName("claims_token")
	private ClaimsToken claimsToken;

	@SerializedName("provider_token")
	private ProviderToken providerToken;

	@SerializedName("profiles")
	private Profiles profiles;

	public ProviderTokens getProviderTokens(){
		return providerTokens;
	}

	public String getRefreshToken(){
		return refreshToken;
	}

	public ClaimsToken getClaimsToken(){
		return claimsToken;
	}

	public ProviderToken getProviderToken(){
		return providerToken;
	}

	public Profiles getProfiles(){
		return profiles;
	}
}