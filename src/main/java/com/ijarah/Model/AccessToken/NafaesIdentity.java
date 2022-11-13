package com.ijarah.Model.AccessToken;

import com.google.gson.annotations.SerializedName;

public class NafaesIdentity{

	@SerializedName("firstname")
	private String firstname;

	@SerializedName("profile_attributes")
	private ProfileAttributes profileAttributes;

	@SerializedName("userid")
	private String userid;

	@SerializedName("email")
	private String email;

	@SerializedName("lastname")
	private String lastname;

	public String getFirstname(){
		return firstname;
	}

	public ProfileAttributes getProfileAttributes(){
		return profileAttributes;
	}

	public String getUserid(){
		return userid;
	}

	public String getEmail(){
		return email;
	}

	public String getLastname(){
		return lastname;
	}
}