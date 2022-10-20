package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SCOREItem{

	@SerializedName("SC_SCORE")
	private String sCSCORE;

	@SerializedName("SC_MINIMUM")
	private String sCMINIMUM;

	@SerializedName("SC_REASON_CODES")
	private List<SCREASONCODESItem> sCREASONCODES;

	@SerializedName("SC_SCORECARD")
	private String sCSCORECARD;

	@SerializedName("SC_SCOREINDEX")
	private String sCSCOREINDEX;

	@SerializedName("SC_MAXIMUM")
	private String sCMAXIMUM;

	public void setSCSCORE(String sCSCORE){
		this.sCSCORE = sCSCORE;
	}

	public String getSCSCORE(){
		return sCSCORE;
	}

	public void setSCMINIMUM(String sCMINIMUM){
		this.sCMINIMUM = sCMINIMUM;
	}

	public String getSCMINIMUM(){
		return sCMINIMUM;
	}

	public void setSCREASONCODES(List<SCREASONCODESItem> sCREASONCODES){
		this.sCREASONCODES = sCREASONCODES;
	}

	public List<SCREASONCODESItem> getSCREASONCODES(){
		return sCREASONCODES;
	}

	public void setSCSCORECARD(String sCSCORECARD){
		this.sCSCORECARD = sCSCORECARD;
	}

	public String getSCSCORECARD(){
		return sCSCORECARD;
	}

	public void setSCSCOREINDEX(String sCSCOREINDEX){
		this.sCSCOREINDEX = sCSCOREINDEX;
	}

	public String getSCSCOREINDEX(){
		return sCSCOREINDEX;
	}

	public void setSCMAXIMUM(String sCMAXIMUM){
		this.sCMAXIMUM = sCMAXIMUM;
	}

	public String getSCMAXIMUM(){
		return sCMAXIMUM;
	}

	@Override
 	public String toString(){
		return 
			"SCOREItem{" + 
			"sC_SCORE = '" + sCSCORE + '\'' + 
			",sC_MINIMUM = '" + sCMINIMUM + '\'' + 
			",sC_REASON_CODES = '" + sCREASONCODES + '\'' + 
			",sC_SCORECARD = '" + sCSCORECARD + '\'' + 
			",sC_SCOREINDEX = '" + sCSCOREINDEX + '\'' + 
			",sC_MAXIMUM = '" + sCMAXIMUM + '\'' + 
			"}";
		}
}