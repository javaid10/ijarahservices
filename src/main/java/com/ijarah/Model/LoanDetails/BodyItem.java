package com.ijarah.Model.LoanDetails;

import com.google.gson.annotations.SerializedName;

public class BodyItem{

	@SerializedName("arrangementId")
	private String arrangementId;

	@SerializedName("repaymentAmount")
	private int repaymentAmount;

	@SerializedName("loanStartDate")
	private String loanStartDate;

	@SerializedName("roleDisplayName")
	private String roleDisplayName;

	@SerializedName("sabbId")
	private String sabbId;

	@SerializedName("loanAmount")
	private int loanAmount;

	@SerializedName("loanInterestType")
	private String loanInterestType;

	@SerializedName("productName")
	private String productName;

	@SerializedName("loanBalance")
	private int loanBalance;

	@SerializedName("loanNextPayDate")
	private String loanNextPayDate;

	@SerializedName("customerShortName")
	private String customerShortName;

	@SerializedName("sadadId")
	private String sadadId;

	@SerializedName("loanProduct")
	private String loanProduct;

	@SerializedName("loanAccountId")
	private String loanAccountId;

	@SerializedName("curCommitment")
	private int curCommitment;

	@SerializedName("loanStatus")
	private String loanStatus;

	@SerializedName("totCommitment")
	private int totCommitment;

	@SerializedName("loanCurrency")
	private String loanCurrency;

	@SerializedName("loanEndDate")
	private String loanEndDate;

	public String getArrangementId(){
		return arrangementId;
	}

	public int getRepaymentAmount(){
		return repaymentAmount;
	}

	public String getLoanStartDate(){
		return loanStartDate;
	}

	public String getRoleDisplayName(){
		return roleDisplayName;
	}

	public String getSabbId(){
		return sabbId;
	}

	public int getLoanAmount(){
		return loanAmount;
	}

	public String getLoanInterestType(){
		return loanInterestType;
	}

	public String getProductName(){
		return productName;
	}

	public int getLoanBalance(){
		return loanBalance;
	}

	public String getLoanNextPayDate(){
		return loanNextPayDate;
	}

	public String getCustomerShortName(){
		return customerShortName;
	}

	public String getSadadId(){
		return sadadId;
	}

	public String getLoanProduct(){
		return loanProduct;
	}

	public String getLoanAccountId(){
		return loanAccountId;
	}

	public int getCurCommitment(){
		return curCommitment;
	}

	public String getLoanStatus(){
		return loanStatus;
	}

	public int getTotCommitment(){
		return totCommitment;
	}

	public String getLoanCurrency(){
		return loanCurrency;
	}

	public String getLoanEndDate(){
		return loanEndDate;
	}
}