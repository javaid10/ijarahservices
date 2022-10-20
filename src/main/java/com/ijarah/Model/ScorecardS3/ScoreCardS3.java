package com.ijarah.Model.ScorecardS3;

import com.google.gson.annotations.SerializedName;

public class ScoreCardS3{

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("header")
	private Header header;

	@SerializedName("body")
	private Body body;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public void setOpstatus(int opstatus){
		this.opstatus = opstatus;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setBody(Body body){
		this.body = body;
	}

	public Body getBody(){
		return body;
	}

	public void setHttpStatusCode(int httpStatusCode){
		this.httpStatusCode = httpStatusCode;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}

	@Override
 	public String toString(){
		return 
			"ScoreCardS3{" + 
			"opstatus = '" + opstatus + '\'' + 
			",header = '" + header + '\'' + 
			",body = '" + body + '\'' + 
			",httpStatusCode = '" + httpStatusCode + '\'' + 
			"}";
		}
}