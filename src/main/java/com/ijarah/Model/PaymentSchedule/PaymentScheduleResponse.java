package com.ijarah.Model.PaymentSchedule;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PaymentScheduleResponse{

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("header")
	private Header header;

	@SerializedName("body")
	private List<BodyItem> body;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public int getOpstatus(){
		return opstatus;
	}

	public Header getHeader(){
		return header;
	}

	public List<BodyItem> getBody(){
		return body;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}