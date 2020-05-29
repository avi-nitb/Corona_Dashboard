package com.coronadashboard.viewModel;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coronadashboard.MainActivity;
import com.coronadashboard.R;
import com.coronadashboard.model.Country;
import com.coronadashboard.model.DashboardData;
import com.coronadashboard.network.NetworkCallsInterface;
import com.coronadashboard.network.RetrofitClientInstance;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {

    MutableLiveData<DashboardData> dashboardLiveData;
    MutableLiveData<ArrayList<Country>> countryLiveData;
    ArrayList<Country> countryWiseList = new ArrayList<>();

    public MainActivityViewModel() {
        dashboardLiveData = new MutableLiveData<>();
        countryLiveData = new MutableLiveData<>();

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAcyncTask = new TimerTask() {
            @Override
            public void run() {
                final NetworkCallsInterface networkCall = RetrofitClientInstance.getRetrofitInstance().create(NetworkCallsInterface.class);
                Call<JsonObject> call = networkCall.getData();

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.code() == 200) {
                                DashboardData dashboardData = new Gson().fromJson(response.body(), DashboardData.class);
                                dashboardLiveData.setValue(dashboardData);

                            }
                        } catch (JsonSyntaxException e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        };
        timer.schedule(doAcyncTask, 0, 120000);
    }


    public MutableLiveData<DashboardData> getDashboardLiveData() {
        return dashboardLiveData;
    }
}
