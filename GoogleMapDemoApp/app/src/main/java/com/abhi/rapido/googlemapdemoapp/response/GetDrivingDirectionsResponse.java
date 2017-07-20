package com.abhi.rapido.googlemapdemoapp.response;

import com.abhi.rapido.googlemapdemoapp.model.Results;
import com.abhi.rapido.googlemapdemoapp.model.Route;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetDrivingDirectionsResponse {

    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }}