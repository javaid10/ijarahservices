package com.ijarah.Model;

import com.google.gson.annotations.SerializedName;

public class NafaesItem{

    @SerializedName("nationalid")
    private String nationalid;

    @SerializedName("purchaseorder")
    private String purchaseorder;

    @SerializedName("id")
    private String id;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("createdts")
    private String createdts;

    public String getNationalid(){
        return nationalid;
    }

    public String getPurchaseorder(){
        return purchaseorder;
    }

    public String getId(){
        return id;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public String getCreatedts(){
        return createdts;
    }
}