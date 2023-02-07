package com.ijarah.Model.consumerEnquiryModelFinal;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ConsumerEnquiryModelResponse{

	@SerializedName("CONTACTS")
	private List<CONTACTSItem> cONTACTS;

	@SerializedName("SCORE")
	private SCORE sCORE;

	@SerializedName("HEADER")
	private HEADER hEADER;

	@SerializedName("BOUNCED_CHECKS")
	private List<BOUNCEDCHECKSItem> bOUNCEDCHECKS;

	@SerializedName("SUMMARY")
	private SUMMARY sUMMARY;

	@SerializedName("SC_REASON_CODES")
	private List<SCREASONCODESItem> sCREASONCODES;

	@SerializedName("RESPONSE")
	private RESPONSE rESPONSE;

	@SerializedName("DEFAULTS")
	private List<DEFAULTSItem> dEFAULTS;

	@SerializedName("CID")
	private CID cID;

	@SerializedName("PCNAM")
	private PCNAM pCNAM;

	@SerializedName("RSP_REPORT")
	private RSPREPORT rSPREPORT;

	@SerializedName("JUDGEMENTS")
	private List<JUDGEMENTSItem> jUDGEMENTS;

	@SerializedName("PROVIDED")
	private PROVIDED pROVIDED;

	@SerializedName("EMPLOYERS")
	private List<EMPLOYERSItem> eMPLOYERS;

	@SerializedName("ACNAM")
	private ACNAM aCNAM;

	@SerializedName("PREV_ENQUIRIES")
	private List<PREVENQUIRIESItem> pREVENQUIRIES;

	@SerializedName("AVAILABLE")
	private AVAILABLE aVAILABLE;

	@SerializedName("ITEM")
	private ITEM iTEM;

	@SerializedName("DISCLAIMER")
	private DISCLAIMER dISCLAIMER;

	@SerializedName("CONSUMER")
	private CONSUMER cONSUMER;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("CI_DETAILS")
	private List<CIDETAILSItem> cIDETAILS;

	@SerializedName("ADDRESSES")
	private List<ADDRESSESItem> aDDRESSES;

	@SerializedName("status")
	private String status;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public List<CONTACTSItem> getCONTACTS(){
		return cONTACTS;
	}

	public SCORE getSCORE(){
		return sCORE;
	}

	public HEADER getHEADER(){
		return hEADER;
	}

	public List<BOUNCEDCHECKSItem> getBOUNCEDCHECKS(){
		return bOUNCEDCHECKS;
	}

	public SUMMARY getSUMMARY(){
		return sUMMARY;
	}

	public List<SCREASONCODESItem> getSCREASONCODES(){
		return sCREASONCODES;
	}

	public RESPONSE getRESPONSE(){
		return rESPONSE;
	}

	public List<DEFAULTSItem> getDEFAULTS(){
		return dEFAULTS;
	}

	public CID getCID(){
		return cID;
	}

	public PCNAM getPCNAM(){
		return pCNAM;
	}

	public RSPREPORT getRSPREPORT(){
		return rSPREPORT;
	}

	public List<JUDGEMENTSItem> getJUDGEMENTS(){
		return jUDGEMENTS;
	}

	public PROVIDED getPROVIDED(){
		return pROVIDED;
	}

	public List<EMPLOYERSItem> getEMPLOYERS(){
		return eMPLOYERS;
	}

	public ACNAM getACNAM(){
		return aCNAM;
	}

	public List<PREVENQUIRIESItem> getPREVENQUIRIES(){
		return pREVENQUIRIES;
	}

	public AVAILABLE getAVAILABLE(){
		return aVAILABLE;
	}

	public ITEM getITEM(){
		return iTEM;
	}

	public DISCLAIMER getDISCLAIMER(){
		return dISCLAIMER;
	}

	public CONSUMER getCONSUMER(){
		return cONSUMER;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public List<CIDETAILSItem> getCIDETAILS(){
		return cIDETAILS;
	}

	public List<ADDRESSESItem> getADDRESSES(){
		return aDDRESSES;
	}

	public String getStatus(){
		return status;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}