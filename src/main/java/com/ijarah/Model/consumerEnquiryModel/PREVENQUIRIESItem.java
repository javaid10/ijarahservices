package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PREVENQUIRIESItem{

	@SerializedName("PREV_ENQUIRY")
	private List<PREVENQUIRYItem> pREVENQUIRY;

	public void setPREVENQUIRY(List<PREVENQUIRYItem> pREVENQUIRY){
		this.pREVENQUIRY = pREVENQUIRY;
	}

	public List<PREVENQUIRYItem> getPREVENQUIRY(){
		return pREVENQUIRY;
	}

	@Override
 	public String toString(){
		return 
			"PREVENQUIRIESItem{" + 
			"pREV_ENQUIRY = '" + pREVENQUIRY + '\'' + 
			"}";
		}
}