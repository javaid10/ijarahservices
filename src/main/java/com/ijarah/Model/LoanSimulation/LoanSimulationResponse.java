package com.ijarah.Model.LoanSimulation;

import com.google.gson.annotations.SerializedName;

public class LoanSimulationResponse{

	@SerializedName("arrangementActivity")
	private ArrangementActivity arrangementActivity;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public ArrangementActivity getArrangementActivity(){
		return arrangementActivity;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}