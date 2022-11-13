package com.ijarah.Model.ScorecardS2;

import com.google.gson.annotations.SerializedName;

public class ScoreCardS2{

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