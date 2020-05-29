package com.coronadashboard.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coronadashboard.R;
import com.coronadashboard.model.Country;
import com.coronadashboard.model.DashboardData;

import java.util.ArrayList;
import java.util.List;

public class CountryWiseDataAdapter extends RecyclerView.Adapter<CountryWiseDataAdapter.MyViewHolder> {

    private Context context;
    private List<Country> listData;

    public CountryWiseDataAdapter(Context context, List<Country> listData){
        this.context = context;
        this.listData = listData;
    }
    @NonNull
    @Override
    public CountryWiseDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_country, parent, false);
        return (new MyViewHolder(itemView));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CountryWiseDataAdapter.MyViewHolder holder, int position) {

        Country currentData = listData.get(position);

        if (currentData.getTotalConfirmed() != 0){
            holder.textViewCountry.setText(currentData.getCountry());
            holder.textViewTotalCases.setText(currentData.getTotalConfirmed().toString());
            holder.textViewDeaths.setText(currentData.getTotalDeaths().toString());
            holder.textViewRecovered.setText(currentData.getTotalRecovered().toString());
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCountry, textViewTotalCases, textViewDeaths, textViewRecovered;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCountry = itemView.findViewById(R.id.textViewCountry);
            textViewTotalCases = itemView.findViewById(R.id.textViewTotalCases);
            textViewDeaths = itemView.findViewById(R.id.textViewDeaths);
            textViewRecovered = itemView.findViewById(R.id.textViewRecovered);

        }
    }
}
