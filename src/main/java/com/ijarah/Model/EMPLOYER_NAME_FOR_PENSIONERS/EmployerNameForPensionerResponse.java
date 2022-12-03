package com.ijarah.Model.EMPLOYER_NAME_FOR_PENSIONERS;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EmployerNameForPensionerResponse{

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("employernamesforpensioner")
	private List<EmployernamesforpensionerItem> employernamesforpensioner;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public int getOpstatus(){
		return opstatus;
	}

	public List<EmployernamesforpensionerItem> getEmployernamesforpensioner(){
		return employernamesforpensioner;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}