package com.ijarah.Model.NafaithSignatureData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NafaithSignatureData{

	@SerializedName("debtor_phone_number")
	private String debtorPhoneNumber;

	@SerializedName("total_value")
	private int totalValue;

	@SerializedName("sanad")
	private List<SanadItem> sanad;

	@SerializedName("debtor")
	private Debtor debtor;

	@SerializedName("reference_id")
	private String referenceId;

	@SerializedName("city_of_issuance")
	private String cityOfIssuance;

	@SerializedName("currency")
	private String currency;

	@SerializedName("max_approve_duration")
	private int maxApproveDuration;

	@SerializedName("city_of_payment")
	private String cityOfPayment;

	@SerializedName("country_of_issuance")
	private String countryOfIssuance;

	@SerializedName("country_of_payment")
	private String countryOfPayment;

	public void setDebtorPhoneNumber(String debtorPhoneNumber){
		this.debtorPhoneNumber = debtorPhoneNumber;
	}

	public String getDebtorPhoneNumber(){
		return debtorPhoneNumber;
	}

	public void setTotalValue(int totalValue){
		this.totalValue = totalValue;
	}

	public double getTotalValue(){
		return totalValue;
	}

	public void setSanad(List<SanadItem> sanad){
		this.sanad = sanad;
	}

	public List<SanadItem> getSanad(){
		return sanad;
	}

	public void setDebtor(Debtor debtor){
		this.debtor = debtor;
	}

	public Debtor getDebtor(){
		return debtor;
	}

	public void setReferenceId(String referenceId){
		this.referenceId = referenceId;
	}

	public String getReferenceId(){
		return referenceId;
	}

	public void setCityOfIssuance(String cityOfIssuance){
		this.cityOfIssuance = cityOfIssuance;
	}

	public String getCityOfIssuance(){
		return cityOfIssuance;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setMaxApproveDuration(int maxApproveDuration){
		this.maxApproveDuration = maxApproveDuration;
	}

	public int getMaxApproveDuration(){
		return maxApproveDuration;
	}

	public void setCityOfPayment(String cityOfPayment){
		this.cityOfPayment = cityOfPayment;
	}

	public String getCityOfPayment(){
		return cityOfPayment;
	}

	public void setCountryOfIssuance(String countryOfIssuance){
		this.countryOfIssuance = countryOfIssuance;
	}

	public String getCountryOfIssuance(){
		return countryOfIssuance;
	}

	public void setCountryOfPayment(String countryOfPayment){
		this.countryOfPayment = countryOfPayment;
	}

	public String getCountryOfPayment(){
		return countryOfPayment;
	}

	@Override
 	public String toString(){
		return 
			"NafaithSignatureData{" + 
			"debtor_phone_number = '" + debtorPhoneNumber + '\'' + 
			",total_value = '" + totalValue + '\'' + 
			",sanad = '" + sanad + '\'' + 
			",debtor = '" + debtor + '\'' + 
			",reference_id = '" + referenceId + '\'' + 
			",city_of_issuance = '" + cityOfIssuance + '\'' + 
			",currency = '" + currency + '\'' + 
			",max_approve_duration = '" + maxApproveDuration + '\'' + 
			",city_of_payment = '" + cityOfPayment + '\'' + 
			",country_of_issuance = '" + countryOfIssuance + '\'' + 
			",country_of_payment = '" + countryOfPayment + '\'' + 
			"}";
		}
}