package com.ijarah.Model.consumerEnquiryModelFinal;

import com.google.gson.annotations.SerializedName;

public class SCORE{

	@SerializedName("SC_SCORE")
	private String sCSCORE;

	@SerializedName("SC_MINIMUM")
	private String sCMINIMUM;

	@SerializedName("SC_SCORECARD")
	private String sCSCORECARD;

	@SerializedName("SC_SCOREINDEX")
	private String sCSCOREINDEX;

	@SerializedName("SC_MAXIMUM")
	private String sCMAXIMUM;

	public String getSCSCORE(){
		return sCSCORE;
	}

	public String getSCMINIMUM(){
		return sCMINIMUM;
	}

	public String getSCSCORECARD(){
		return sCSCORECARD;
	}

	public String getSCSCOREINDEX(){
		return sCSCOREINDEX;
	}

	public String getSCMAXIMUM(){
		return sCMAXIMUM;
	}
}