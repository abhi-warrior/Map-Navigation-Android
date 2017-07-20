package com.abhi.rapido.googlemapdemoapp.response;

import com.abhi.rapido.googlemapdemoapp.model.Results;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetAddressResponse {

    @SerializedName("results")
    @Expose
    public List<Results> results;
}
