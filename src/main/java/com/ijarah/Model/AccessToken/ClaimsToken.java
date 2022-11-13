package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class ClaimsToken{

	@SerializedName("mfa_meta")
	private Object mfaMeta;

	@SerializedName("integrity_check_required")
	private boolean integrityCheckRequired;

	@SerializedName("is_mfa_enabled")
	private boolean isMfaEnabled;

	@SerializedName("session_id")
	private String sessionId;

	@SerializedName("exp")
	private long exp;

	@SerializedName("value")
	private String value;

	public Object getMfaMeta(){
		return mfaMeta;
	}

	public boolean isIntegrityCheckRequired(){
		return integrityCheckRequired;
	}

	public boolean isIsMfaEnabled(){
		return isMfaEnabled;
	}

	public String getSessionId(){
		return sessionId;
	}

	public long getExp(){
		return exp;
	}

	public String getValue(){
		return value;
	}
}