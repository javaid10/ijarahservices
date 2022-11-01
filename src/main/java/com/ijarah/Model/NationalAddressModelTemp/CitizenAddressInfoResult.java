package com.ijarah.Model.NationalAddressModelTemp;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CitizenAddressInfoResult{

	@SerializedName("addressListList")
	private List<AddressListListItem> addressListList;

	@SerializedName("logId")
	private int logId;

	public List<AddressListListItem> getAddressListList(){
		return addressListList;
	}

	public int getLogId(){
		return logId;
	}
}