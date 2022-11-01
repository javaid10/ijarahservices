package com.ijarah.Model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NafaesDBResponse{

	@SerializedName("nafaes")
	private List<NafaesItem> nafaes;

	@SerializedName("opstatus")
	private int opstatus;

	@SerializedName("httpStatusCode")
	private int httpStatusCode;

	public List<NafaesItem> getNafaes(){
		return nafaes;
	}

	public int getOpstatus(){
		return opstatus;
	}

	public int getHttpStatusCode(){
		return httpStatusCode;
	}
}