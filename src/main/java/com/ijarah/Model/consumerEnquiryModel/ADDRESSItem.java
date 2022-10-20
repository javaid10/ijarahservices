package com.ijarah.Model.consumerEnquiryModel;

import com.google.gson.annotations.SerializedName;

public class ADDRESSItem{

	@SerializedName("CA_CAD8A")
	private String cACAD8A;

	@SerializedName("CA_CAD9")
	private String cACAD9;

	@SerializedName("CA_CAD7")
	private String cACAD7;

	@SerializedName("CA_CADT")
	private String cACADT;

	@SerializedName("CA_CAD1A")
	private String cACAD1A;

	@SerializedName("CA_LOAD_DT")
	private String cALOADDT;

	@SerializedName("CA_CAD6")
	private String cACAD6;

	@SerializedName("CA_CAD8E")
	private String cACAD8E;

	public void setCACAD8A(String cACAD8A){
		this.cACAD8A = cACAD8A;
	}

	public String getCACAD8A(){
		return cACAD8A;
	}

	public void setCACAD9(String cACAD9){
		this.cACAD9 = cACAD9;
	}

	public String getCACAD9(){
		return cACAD9;
	}

	public void setCACAD7(String cACAD7){
		this.cACAD7 = cACAD7;
	}

	public String getCACAD7(){
		return cACAD7;
	}

	public void setCACADT(String cACADT){
		this.cACADT = cACADT;
	}

	public String getCACADT(){
		return cACADT;
	}

	public void setCACAD1A(String cACAD1A){
		this.cACAD1A = cACAD1A;
	}

	public String getCACAD1A(){
		return cACAD1A;
	}

	public void setCALOADDT(String cALOADDT){
		this.cALOADDT = cALOADDT;
	}

	public String getCALOADDT(){
		return cALOADDT;
	}

	public void setCACAD6(String cACAD6){
		this.cACAD6 = cACAD6;
	}

	public String getCACAD6(){
		return cACAD6;
	}

	public void setCACAD8E(String cACAD8E){
		this.cACAD8E = cACAD8E;
	}

	public String getCACAD8E(){
		return cACAD8E;
	}

	@Override
 	public String toString(){
		return 
			"ADDRESSItem{" + 
			"cA_CAD8A = '" + cACAD8A + '\'' + 
			",cA_CAD9 = '" + cACAD9 + '\'' + 
			",cA_CAD7 = '" + cACAD7 + '\'' + 
			",cA_CADT = '" + cACADT + '\'' + 
			",cA_CAD1A = '" + cACAD1A + '\'' + 
			",cA_LOAD_DT = '" + cALOADDT + '\'' + 
			",cA_CAD6 = '" + cACAD6 + '\'' + 
			",cA_CAD8E = '" + cACAD8E + '\'' + 
			"}";
		}
}