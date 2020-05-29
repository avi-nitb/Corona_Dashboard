package com.coronadashboard.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.coronadashboard.network.NetworkCallsInterface;
import com.coronadashboard.network.RetrofitClientInstance;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkCallIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NetworkCallIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


    }
}
