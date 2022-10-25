package com.ijarah.Model.SanadResponseModel;

import com.google.gson.annotations.SerializedName;

public class Creditor{

	@SerializedName("national_id")
	private String nationalId;

	@SerializedName("address")
	private String address;

	@SerializedName("birth_date")
	private String birthDate;

	@SerializedName("second_name")
	private String secondName;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("third_name")
	private String thirdName;

	@SerializedName("first_name")
	private String firstName;

	public String getNationalId(){
		return nationalId;
	}

	public String getAddress(){
		return address;
	}

	public String getBirthDate(){
		return birthDate;
	}

	public String getSecondName(){
		return secondName;
	}

	public String getLastName(){
		return lastName;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public String getThirdName(){
		return thirdName;
	}

	public String getFirstName(){
		return firstName;
	}
}