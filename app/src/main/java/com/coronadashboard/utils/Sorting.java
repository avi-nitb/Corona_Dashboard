package com.coronadashboard.utils;

import com.coronadashboard.model.Country;

import java.util.Comparator;

public class Sorting {

    public static class SortByTotalCases implements Comparator<Country>
    {
        // Used for sorting in ascending order of
        // Total Cases
        @Override
        public int compare(Country o1, Country o2) {
            return o1.getTotalConfirmed() - o2.getTotalConfirmed();
        }
    }

    public static class SortByRecovered implements Comparator<Country>
    {
        // Used for sorting in ascending order of Recovered
        @Override
        public int compare(Country o1, Country o2) {
            return o1.getTotalRecovered() - o2.getTotalRecovered();
        }
    }

    public static class SortByDeaths implements Comparator<Country>
    {
        // Used for sorting in ascending order of Deaths
        @Override
        public int compare(Country o1, Country o2) {
            return o1.getTotalDeaths() - o2.getTotalDeaths();
        }
    }
}
