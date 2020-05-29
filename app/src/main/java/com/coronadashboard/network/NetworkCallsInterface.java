package com.coronadashboard.network;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NetworkCallsInterface {

    @GET("summary")
    Call<JsonObject> getData();
}
