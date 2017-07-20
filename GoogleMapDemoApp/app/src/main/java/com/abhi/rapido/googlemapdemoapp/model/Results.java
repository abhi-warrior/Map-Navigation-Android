package com.abhi.rapido.googlemapdemoapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Results {
    @SerializedName("formatted_address")
    @Expose
    public String formatted_address;
    @SerializedName("place_id")
    @Expose
    public String place_id;
}
