package com.ijarah.Model.NationalAddressModelTemp;

import com.google.gson.annotations.SerializedName;

public class AddressListListItem{

	@SerializedName("streetName")
	private String streetName;

	@SerializedName("city")
	private String city;

	@SerializedName("additionalNumber")
	private int additionalNumber;

	@SerializedName("district")
	private String district;

	@SerializedName("buildingNumber")
	private int buildingNumber;

	@SerializedName("unitNumber")
	private int unitNumber;

	@SerializedName("postCode")
	private int postCode;

	@SerializedName("locationCoordinates")
	private String locationCoordinates;

	public String getStreetName(){
		return streetName;
	}

	public String getCity(){
		return city;
	}

	public int getAdditionalNumber(){
		return additionalNumber;
	}

	public String getDistrict(){
		return district;
	}

	public int getBuildingNumber(){
		return buildingNumber;
	}

	public int getUnitNumber(){
		return unitNumber;
	}

	public int getPostCode(){
		return postCode;
	}

	public String getLocationCoordinates(){
		return locationCoordinates;
	}
}