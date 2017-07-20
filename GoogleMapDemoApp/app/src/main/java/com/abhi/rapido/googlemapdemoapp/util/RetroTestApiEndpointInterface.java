package com.abhi.rapido.googlemapdemoapp.util;

import com.abhi.rapido.googlemapdemoapp.response.GetAddressResponse;
import com.abhi.rapido.googlemapdemoapp.response.GetDrivingDirectionsResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



public interface RetroTestApiEndpointInterface {
// Request method and URL specified in the annotation

    @GET("geocode/json")
    Call<GetAddressResponse> getAddress(@Query("latlng") String latlng, @Query("key") String key);

    @GET("directions/json")
    Call<GetDrivingDirectionsResponse> getDrivingDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("alternatives") boolean alternatives, @Query("key") String key);

}
