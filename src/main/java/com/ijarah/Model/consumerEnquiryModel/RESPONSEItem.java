package com.ijarah.Model.consumerEnquiryModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RESPONSEItem{

	@SerializedName("MESSAGE")
	private List<MESSAGEItem> mESSAGE;

	@SerializedName("STATUS")
	private String sTATUS;

	@SerializedName("ACTION")
	private String aCTION;

	@SerializedName("SERVICE")
	private String sERVICE;

	@SerializedName("HEADER")
	private List<HEADERItem> hEADER;

	@SerializedName("VERSION_NO")
	private String vERSIONNO;

	public void setMESSAGE(List<MESSAGEItem> mESSAGE){
		this.mESSAGE = mESSAGE;
	}

	public List<MESSAGEItem> getMESSAGE(){
		return mESSAGE;
	}

	public void setSTATUS(String sTATUS){
		this.sTATUS = sTATUS;
	}

	public String getSTATUS(){
		return sTATUS;
	}

	public void setACTION(String aCTION){
		this.aCTION = aCTION;
	}

	public String getACTION(){
		return aCTION;
	}

	public void setSERVICE(String sERVICE){
		this.sERVICE = sERVICE;
	}

	public String getSERVICE(){
		return sERVICE;
	}

	public void setHEADER(List<HEADERItem> hEADER){
		this.hEADER = hEADER;
	}

	public List<HEADERItem> getHEADER(){
		return hEADER;
	}

	public void setVERSIONNO(String vERSIONNO){
		this.vERSIONNO = vERSIONNO;
	}

	public String getVERSIONNO(){
		return vERSIONNO;
	}

	@Override
 	public String toString(){
		return 
			"RESPONSEItem{" + 
			"mESSAGE = '" + mESSAGE + '\'' + 
			",sTATUS = '" + sTATUS + '\'' + 
			",aCTION = '" + aCTION + '\'' + 
			",sERVICE = '" + sERVICE + '\'' + 
			",hEADER = '" + hEADER + '\'' + 
			",vERSION_NO = '" + vERSIONNO + '\'' + 
			"}";
		}
}