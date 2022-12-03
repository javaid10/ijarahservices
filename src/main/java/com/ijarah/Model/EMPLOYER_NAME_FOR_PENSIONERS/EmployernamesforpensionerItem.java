package com.ijarah.Model.EMPLOYER_NAME_FOR_PENSIONERS;

import com.google.gson.annotations.SerializedName;

public class EmployernamesforpensionerItem{

	@SerializedName("employer_names_for_pensioners_value")
	private String employerNamesForPensionersValue;

	public String getEmployerNamesForPensionersValue(){
		return employerNamesForPensionersValue;
	}
}