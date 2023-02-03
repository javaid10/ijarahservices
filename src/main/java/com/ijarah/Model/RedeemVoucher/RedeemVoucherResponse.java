package com.ijarah.Model.RedeemVoucher;

import com.google.gson.annotations.SerializedName;

public class RedeemVoucherResponse{

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("header")
	private Header header;

	@SerializedName("body")
	private Body body;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public int getOpstatus(){
		return opstatus;
	}

	public Header getHeader(){
		return header;
	}

	public Body getBody(){
		return body;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}