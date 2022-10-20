package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ITEMItem{

	@SerializedName("RSP_REPORT")
	private List<RSPREPORTItem> rSPREPORT;

	@SerializedName("ENQUIRY_REFERENCE")
	private String eNQUIRYREFERENCE;

	public void setRSPREPORT(List<RSPREPORTItem> rSPREPORT){
		this.rSPREPORT = rSPREPORT;
	}

	public List<RSPREPORTItem> getRSPREPORT(){
		return rSPREPORT;
	}

	public void setENQUIRYREFERENCE(String eNQUIRYREFERENCE){
		this.eNQUIRYREFERENCE = eNQUIRYREFERENCE;
	}

	public String getENQUIRYREFERENCE(){
		return eNQUIRYREFERENCE;
	}

	@Override
 	public String toString(){
		return 
			"ITEMItem{" + 
			"rSP_REPORT = '" + rSPREPORT + '\'' + 
			",eNQUIRY_REFERENCE = '" + eNQUIRYREFERENCE + '\'' + 
			"}";
		}
}