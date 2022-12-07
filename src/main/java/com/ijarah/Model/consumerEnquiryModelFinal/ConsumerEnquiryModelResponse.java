package com.ijarah.Model.consumerEnquiryModelFinal;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ConsumerEnquiryModelResponse{

	@SerializedName("CONTACTS")
	private List<CONTACTSItem> cONTACTS;

	@SerializedName("PCNAM")
	private PCNAM pCNAM;

	@SerializedName("SCORE")
	private SCORE sCORE;

	@SerializedName("RSP_REPORT")
	private RSPREPORT rSPREPORT;

	@SerializedName("HEADER")
	private HEADER hEADER;

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

	@SerializedName("SUMMARY")
	private SUMMARY sUMMARY;

	@SerializedName("DISCLAIMER")
	private DISCLAIMER dISCLAIMER;

	@SerializedName("CONSUMER")
	private CONSUMER cONSUMER;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("CI_DETAILS")
	private List<CIDETAILSItem> cIDETAILS;

	@SerializedName("SC_REASON_CODES")
	private List<SCREASONCODESItem> sCREASONCODES;

	@SerializedName("ADDRESSES")
	private List<ADDRESSESItem> aDDRESSES;

	@SerializedName("RESPONSE")
	private RESPONSE rESPONSE;

	@SerializedName("DEFAULTS")
	private List<DEFAULTSItem> dEFAULTS;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	@SerializedName("CID")
	private CID cID;

	public List<CONTACTSItem> getCONTACTS(){
		return cONTACTS;
	}

	public PCNAM getPCNAM(){
		return pCNAM;
	}

	public SCORE getSCORE(){
		return sCORE;
	}

	public RSPREPORT getRSPREPORT(){
		return rSPREPORT;
	}

	public HEADER getHEADER(){
		return hEADER;
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

	public SUMMARY getSUMMARY(){
		return sUMMARY;
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

	public List<SCREASONCODESItem> getSCREASONCODES(){
		return sCREASONCODES;
	}

	public List<ADDRESSESItem> getADDRESSES(){
		return aDDRESSES;
	}

	public RESPONSE getRESPONSE(){
		return rESPONSE;
	}

	public List<DEFAULTSItem> getDEFAULTS(){
		return dEFAULTS;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}

	public CID getCID(){
		return cID;
	}
}