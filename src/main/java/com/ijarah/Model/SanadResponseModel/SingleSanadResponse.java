package com.ijarah.Model.SanadResponseModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SingleSanadResponse{

	@SerializedName("debtor_phone_number")
	private String debtorPhoneNumber;

	@SerializedName("reason")
	private String reason;

	@SerializedName("total_value")
	private String totalValue;

	@SerializedName("sanad")
	private List<SanadItem> sanad;

	@SerializedName("code")
	private int code;

	@SerializedName("reference_id")
	private String referenceId;

	@SerializedName("type")
	private String type;

	@SerializedName("issued_at")
	private String issuedAt;

	@SerializedName("country_of_issuance")
	private String countryOfIssuance;

	@SerializedName("debtor")
	private Debtor debtor;

	@SerializedName("number_of_sanads")
	private int numberOfSanads;

	@SerializedName("approved_at")
	private String approvedAt;

	@SerializedName("city_of_issuance")
	private String cityOfIssuance;

	@SerializedName("creditor")
	private Creditor creditor;

	@SerializedName("currency")
	private String currency;

	@SerializedName("max_approve_duration")
	private int maxApproveDuration;

	@SerializedName("id")
	private String id;

	@SerializedName("city_of_payment")
	private String cityOfPayment;

	@SerializedName("country_of_payment")
	private String countryOfPayment;

	@SerializedName("status")
	private String status;

	public String getDebtorPhoneNumber(){
		return debtorPhoneNumber;
	}

	public String getReason(){
		return reason;
	}

	public String getTotalValue(){
		return totalValue;
	}

	public List<SanadItem> getSanad(){
		return sanad;
	}

	public int getCode(){
		return code;
	}

	public String getReferenceId(){
		return referenceId;
	}

	public String getType(){
		return type;
	}

	public String getIssuedAt(){
		return issuedAt;
	}

	public String getCountryOfIssuance(){
		return countryOfIssuance;
	}

	public Debtor getDebtor(){
		return debtor;
	}

	public int getNumberOfSanads(){
		return numberOfSanads;
	}

	public String getApprovedAt(){
		return approvedAt;
	}

	public String getCityOfIssuance(){
		return cityOfIssuance;
	}

	public Creditor getCreditor(){
		return creditor;
	}

	public String getCurrency(){
		return currency;
	}

	public int getMaxApproveDuration(){
		return maxApproveDuration;
	}

	public String getId(){
		return id;
	}

	public String getCityOfPayment(){
		return cityOfPayment;
	}

	public String getCountryOfPayment(){
		return countryOfPayment;
	}

	public String getStatus(){
		return status;
	}
}