package com.coronadashboard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.coronadashboard.adapters.CountryWiseDataAdapter;
import com.coronadashboard.model.Country;
import com.coronadashboard.model.DashboardData;
import com.coronadashboard.utils.AppConstants;
import com.coronadashboard.utils.SharedPrefOperation;
import com.coronadashboard.utils.Sorting;
import com.coronadashboard.viewModel.MainActivityViewModel;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView textViewTotalCases, textViewDeaths, textViewRecovered, labelTotalCases, labelRecovered,
            tv_search, tv_cancel, labelDeaths, textViewFilterInfo, textViewClearFilter;
    RecyclerView rvCountryWise;
    ViewModel mainActivityViewModel;
    CountryWiseDataAdapter countryWiseDataAdapter;
    List<Country> listData = new ArrayList<>(), filteredData = new ArrayList<>();
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    double latitude, longitude;
    String userCountryName;
    AlertDialog dialog;
    int maxConfirmed = 0, maxdeaths = 0, maxRecovered = 0, position;
    CrystalRangeSeekbar totalCasesSeekbar, recoveredSeekbar, deathsSeekbar;
    boolean isDescendingByTotalCases = true, isDescendingByDeaths = false, isDescendingByRecovered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Initializing ViewModel
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        ((MainActivityViewModel) mainActivityViewModel).getDashboardLiveData().observe(this, dashboardDataUpdateObserver);

    }

    Observer<DashboardData> dashboardDataUpdateObserver = new Observer<DashboardData>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        public void onChanged(DashboardData dashboardData) {
            textViewTotalCases.setText(dashboardData.getGlobal().getTotalConfirmed() + "");
            textViewDeaths.setText(dashboardData.getGlobal().getTotalDeaths() + "");
            textViewRecovered.setText(dashboardData.getGlobal().getTotalRecovered() + "");


            //Setting up Recycler View

            listData.clear();
            listData.addAll(dashboardData.getCountries());
            listData.sort(new Sorting.SortByTotalCases());
            Collections.reverse(listData);


            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getTotalConfirmed() > maxConfirmed) {
                    maxConfirmed = listData.get(i).getTotalConfirmed();
                }

                if (listData.get(i).getTotalDeaths() > maxdeaths) {
                    maxdeaths = listData.get(i).getTotalDeaths();
                }

                if (listData.get(i).getTotalRecovered() > maxRecovered) {
                    maxRecovered = listData.get(i).getTotalRecovered();
                }

                if (listData.get(i).getCountry().equalsIgnoreCase(userCountryName)) {
                    position = i;
                }
            }
            rvCountryWise.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            countryWiseDataAdapter = new CountryWiseDataAdapter(MainActivity.this, listData);
            rvCountryWise.setAdapter(countryWiseDataAdapter);
            rvCountryWise.scrollToPosition(position);


            labelDeaths.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDescendingByDeaths) {
                        listData.sort(new Sorting.SortByDeaths());
                        Collections.reverse(listData);
                        countryWiseDataAdapter.notifyDataSetChanged();
                        isDescendingByDeaths = !isDescendingByDeaths;
                    } else {
                        Collections.reverse(listData);
                        countryWiseDataAdapter.notifyDataSetChanged();
                        isDescendingByDeaths = !isDescendingByDeaths;
                    }
                }
            });

            labelRecovered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDescendingByRecovered) {
                        listData.sort(new Sorting.SortByRecovered());
                        Collections.reverse(listData);
                        countryWiseDataAdapter.notifyDataSetChanged();
                        isDescendingByRecovered = !isDescendingByRecovered;
                    } else {
                        Collections.reverse(listData);
                        countryWiseDataAdapter.notifyDataSetChanged();
                        isDescendingByRecovered = !isDescendingByRecovered;
                    }
                }
            });

            labelTotalCases.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDescendingByTotalCases) {
                        listData.sort(new Sorting.SortByTotalCases());
                        Collections.reverse(listData);
                        countryWiseDataAdapter.notifyDataSetChanged();
                        isDescendingByTotalCases = !isDescendingByTotalCases;
                    } else {
                        Collections.reverse(listData);
                        countryWiseDataAdapter.notifyDataSetChanged();
                        isDescendingByTotalCases = !isDescendingByTotalCases;
                    }
                }
            });

            textViewClearFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textViewFilterInfo.setText("");
                    textViewFilterInfo.setVisibility(View.GONE);
                    countryWiseDataAdapter = new CountryWiseDataAdapter(MainActivity.this, listData);
                    rvCountryWise.setAdapter(countryWiseDataAdapter);
                    textViewClearFilter.setVisibility(View.GONE);
                }
            });

        }
    };

    private void initViews() {
        //findViewByIds
        textViewTotalCases = findViewById(R.id.textViewTotalCases);
        textViewDeaths = findViewById(R.id.textViewDeaths);
        textViewRecovered = findViewById(R.id.textViewRecovered);
        labelTotalCases = findViewById(R.id.labelTotalCases);
        labelDeaths = findViewById(R.id.labelDeaths);
        labelRecovered = findViewById(R.id.labelRecovered);
        textViewFilterInfo = findViewById(R.id.textViewFilterInfo);
        textViewClearFilter = findViewById(R.id.textViewClearFilter);
        rvCountryWise = findViewById(R.id.rvCountryWise);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.app_bar_filter:
                final boolean[] isCaseOnly = {false};
                final boolean[] isRecoveredOnly = {false};
                final boolean[] isDeathsOnly = {false};
                final boolean[] isCaseAndRecovered = {false};
                final boolean[] isCaseAndDeath = {false};
                final boolean[] isRecoveredAndDeaths = {false};
                final boolean[] isAll = {false};
                LayoutInflater inflater = LayoutInflater.from(this);
                final View filterDialog = inflater.inflate(R.layout.dialog_filter, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(filterDialog);
                alertDialogBuilder.setTitle("Filter");

                totalCasesSeekbar = filterDialog.findViewById(R.id.seekbarTotalCases);
                deathsSeekbar = filterDialog.findViewById(R.id.seekbarDeaths);
                recoveredSeekbar = filterDialog.findViewById(R.id.seekbarRecovered);
                totalCasesSeekbar.setMaxValue(maxConfirmed);
                recoveredSeekbar.setMaxValue(maxRecovered);
                deathsSeekbar.setMaxValue(maxdeaths);

                final TextView textViewMinCases = filterDialog.findViewById(R.id.textViewMinCases);
                final TextView textViewMaxCases = filterDialog.findViewById(R.id.textViewMaxCases);
                final TextView textViewMaxRecovered = filterDialog.findViewById(R.id.textViewMaxRecovered);
                final TextView textViewMinRecovered = filterDialog.findViewById(R.id.textViewMinRecovered);
                final TextView textViewMaxDeaths = filterDialog.findViewById(R.id.textViewMaxDeaths);
                final TextView textViewMinDeaths = filterDialog.findViewById(R.id.textViewMinDeaths);
                tv_search = filterDialog.findViewById(R.id.tv_search);
                tv_cancel = filterDialog.findViewById(R.id.tv_cancel);

                if (!(SharedPrefOperation.getSharedPrefString(AppConstants.seekbarMinCaseValue, this) == null)) {
                    totalCasesSeekbar.offsetLeftAndRight(Integer.parseInt(SharedPrefOperation.getSharedPrefString(AppConstants.seekbarMinCaseValue, MainActivity.this)));
                    textViewMinCases.setText(SharedPrefOperation.getSharedPrefString(AppConstants.seekbarMinCaseValue, MainActivity.this));
                }


                totalCasesSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        textViewMinCases.setText(String.valueOf(minValue));
                        textViewMaxCases.setText(String.valueOf(maxValue));

                    }
                });

                deathsSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        textViewMinDeaths.setText(String.valueOf(minValue));
                        textViewMaxDeaths.setText(String.valueOf(maxValue));
                    }
                });

                recoveredSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        textViewMinRecovered.setText(String.valueOf(minValue));
                        textViewMaxRecovered.setText(String.valueOf(maxValue));
                    }
                });

                dialog = alertDialogBuilder.create();
                dialog.show();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                tv_search.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        filteredData.clear();
                        Log.d("Max", totalCasesSeekbar.getSelectedMaxValue().toString());
                        int totalCasesSeekbarMaxValue = totalCasesSeekbar.getSelectedMaxValue().intValue();
                        int totalCasesSeekbarMinValue = totalCasesSeekbar.getSelectedMinValue().intValue();
                        int deathsSeekbarMinValue = deathsSeekbar.getSelectedMinValue().intValue();
                        int deathsSeekbarMaxValue = deathsSeekbar.getSelectedMaxValue().intValue();
                        int recoveredSeekbarMinValue = recoveredSeekbar.getSelectedMinValue().intValue();
                        int recoveredSeekbarMaxValue = recoveredSeekbar.getSelectedMaxValue().intValue();
                        for (int i = 0; i < listData.size(); i++) {
                            int totalConfirmedValue = listData.get(i).getTotalConfirmed();
                            int totalDeathValue = listData.get(i).getTotalDeaths();
                            int totalRecValue = listData.get(i).getTotalRecovered();

                            //If filter applied only on Total Cases
                            if (((totalCasesSeekbarMinValue <= totalConfirmedValue && totalConfirmedValue <= totalCasesSeekbarMaxValue)
                                    && deathsSeekbarMinValue == 0 && deathsSeekbarMaxValue == maxdeaths
                                    && recoveredSeekbarMinValue == 0 && recoveredSeekbarMaxValue == maxRecovered)) {
                                isCaseOnly[0] = true;
                                filteredData.add(listData.get(i));
                            }
                            //If filter applied only on Deaths
                            else if (totalCasesSeekbarMinValue == 0 && totalCasesSeekbarMaxValue == maxConfirmed
                                    && recoveredSeekbarMinValue == 0 && recoveredSeekbarMaxValue == maxRecovered
                                    && ((deathsSeekbarMinValue <= totalDeathValue && totalDeathValue <= deathsSeekbarMaxValue))) {
                                isDeathsOnly[0] = true;
                                filteredData.add(listData.get(i));
                            }
                            //If filter is applied only on recovered cases
                            else if (totalCasesSeekbar.getSelectedMinValue().intValue() == 0 && totalCasesSeekbar.getSelectedMaxValue().intValue() == maxConfirmed
                                    && (recoveredSeekbarMinValue <= totalRecValue && totalRecValue <= recoveredSeekbarMaxValue
                                    && deathsSeekbarMinValue == 0 && deathsSeekbarMaxValue == maxdeaths)) {
                                isRecoveredOnly[0] = true;
                                filteredData.add(listData.get(i));
                            }
                            // If filter is applied on Total cases & Recovered
                            else if ((totalCasesSeekbarMinValue <= totalConfirmedValue && totalConfirmedValue <= totalCasesSeekbarMaxValue)
                                    && (recoveredSeekbarMinValue <= totalRecValue && totalRecValue <= recoveredSeekbarMaxValue)
                                    && deathsSeekbarMinValue == 0 && deathsSeekbarMaxValue == maxdeaths) {
                                isCaseAndRecovered[0] = true;
                                filteredData.add(listData.get(i));
                            }
                            //If filter is applied on Total Cases & Deaths
                            else if ((totalCasesSeekbarMinValue <= totalConfirmedValue && totalConfirmedValue <= totalCasesSeekbarMaxValue)
                                    && (deathsSeekbarMinValue <= totalDeathValue && totalDeathValue <= deathsSeekbarMaxValue)
                                    && recoveredSeekbarMinValue == 0 && recoveredSeekbarMaxValue == maxRecovered) {
                                isCaseAndDeath[0] = true;
                                filteredData.add(listData.get(i));
                            }
                            //If filter is applied on Recovered & Deaths
                            else if (totalCasesSeekbarMinValue == 0 && totalCasesSeekbarMaxValue == maxConfirmed
                                    && (recoveredSeekbarMinValue <= totalRecValue && totalRecValue <= recoveredSeekbarMaxValue)
                                    && (deathsSeekbarMinValue <= totalDeathValue && totalDeathValue <= deathsSeekbarMaxValue)) {
                                isRecoveredAndDeaths[0] = true;
                                filteredData.add(listData.get(i));
                            }
                            //If filter is applied on all 3 parameters
                            else if ((totalCasesSeekbarMinValue <= totalConfirmedValue && totalConfirmedValue <= totalCasesSeekbarMaxValue)
                                    && (recoveredSeekbarMinValue <= totalRecValue && totalRecValue <= recoveredSeekbarMaxValue)
                                    && (deathsSeekbarMinValue <= totalDeathValue && totalDeathValue <= deathsSeekbarMaxValue)) {
                                isAll[0] = true;
                                filteredData.add(listData.get(i));

                            }
                        }
                        countryWiseDataAdapter = new CountryWiseDataAdapter(MainActivity.this, filteredData);
                        rvCountryWise.setAdapter(countryWiseDataAdapter);


                        textViewClearFilter.setVisibility(View.VISIBLE);
                        textViewFilterInfo.setVisibility(View.VISIBLE);
                        if (isAll[0]) {
                            textViewFilterInfo.setText("Filters: Total Cases:"
                                    + (totalCasesSeekbar.getSelectedMinValue())
                                    + "-" + (totalCasesSeekbar.getSelectedMaxValue())
                                    + "; Deaths: " + deathsSeekbar.getSelectedMinValue()
                                    + "-" + deathsSeekbar.getSelectedMaxValue()
                                    + "; Recovered: " + recoveredSeekbar.getSelectedMinValue()
                                    + "-" + recoveredSeekbar.getSelectedMaxValue());
                        } else if (isCaseOnly[0]) {
                            textViewFilterInfo.setText("Filters: Total Cases:"
                                    + (totalCasesSeekbar.getSelectedMinValue())
                                    + "-" + (totalCasesSeekbar.getSelectedMaxValue()));
                        } else if (isDeathsOnly[0]) {
                            textViewFilterInfo.setText("Filters: Deaths: " + deathsSeekbar.getSelectedMinValue()
                                    + "-" + deathsSeekbar.getSelectedMaxValue());
                        } else if (isRecoveredOnly[0]) {
                            textViewFilterInfo.setText("Filters: Recovered: " + recoveredSeekbar.getSelectedMinValue()
                                    + "-" + recoveredSeekbar.getSelectedMaxValue());
                        } else if (isCaseAndDeath[0]) {
                            textViewFilterInfo.setText("Filters: Total Cases:"
                                    + (totalCasesSeekbar.getSelectedMinValue())
                                    + "-" + (totalCasesSeekbar.getSelectedMaxValue())
                                    + "; Deaths: " + deathsSeekbar.getSelectedMinValue()
                                    + "-" + deathsSeekbar.getSelectedMaxValue());
                        } else if (isCaseAndRecovered[0]) {
                            textViewFilterInfo.setText("Filters: Total Cases:"
                                    + (totalCasesSeekbar.getSelectedMinValue())
                                    + "-" + (totalCasesSeekbar.getSelectedMaxValue())
                                    + "; Recovered: " + recoveredSeekbar.getSelectedMinValue()
                                    + "-" + recoveredSeekbar.getSelectedMaxValue());
                        } else if (isRecoveredAndDeaths[0]) {
                            textViewFilterInfo.setText("Filters: Deaths: " + deathsSeekbar.getSelectedMinValue()
                                    + "-" + deathsSeekbar.getSelectedMaxValue()
                                    + "; Recovered: " + recoveredSeekbar.getSelectedMinValue()
                                    + "-" + recoveredSeekbar.getSelectedMaxValue());
                        }

                        dialog.dismiss();
                    }
                });


        }
        return super.onOptionsItemSelected(item);

    }


    private void getLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            switchOnGPS();
        } else {
            getLatLong();

            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address result;

                if (addresses != null && !addresses.isEmpty()) {
                    userCountryName = addresses.get(0).getCountryName();
                    Log.wtf("Country NAme", userCountryName);
                }
            } catch (IOException ignored) {
                //do something
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLatLong() {


        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }


    private void switchOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
