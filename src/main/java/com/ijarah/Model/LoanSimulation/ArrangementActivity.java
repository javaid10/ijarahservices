package com.ijarah.Model.LoanSimulation;

import com.google.gson.annotations.SerializedName;

public class ArrangementActivity{

	@SerializedName("arrangementId")
	private String arrangementId;

	@SerializedName("simulationId")
	private String simulationId;

	public String getArrangementId(){
		return arrangementId;
	}

	public String getSimulationId(){
		return simulationId;
	}
}