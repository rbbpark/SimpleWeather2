package com.roby.simpleweather2.model;

import com.roby.simpleweather2.R;

/**
 * Created by Roby on 1/5/2016.
 */
public class Forecast {
    private Current mCurrent;
    private Hour[] mHourlyForecast;
    private Day[] mDailyForecast;

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHourlyForecast() {
        return mHourlyForecast;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public Day[] getDailyForecast() {
        return mDailyForecast;
    }

    public void setDailyForecast(Day[] dailyForecast) {
        mDailyForecast = dailyForecast;
    }

    public static int getIconId(String iconString) {
        int iconId = R.drawable.ic_weather_sunny_white_48dp;
        if (iconString.equals("clear-day")) {
            iconId = R.drawable.ic_weather_sunny_white_48dp;
        } else if (iconString.equals("clear-night")) {
            iconId = R.drawable.ic_weather_night_white_48dp;
        } else if (iconString.equals("rain")) {
            iconId = R.drawable.ic_weather_rainy_white_48dp;
        } else if (iconString.equals("snow")) {
            iconId = R.drawable.ic_weather_snowy_white_48dp;
        } else if (iconString.equals("sleet")) {
            iconId = R.drawable.ic_weather_hail_white_48dp;
        } else if (iconString.equals("wind")) {
            iconId = R.drawable.ic_weather_windy_white_48dp;
        } else if (iconString.equals("fog")) {
            iconId = R.drawable.ic_weather_fog_white_48dp;
        } else if (iconString.equals("cloudy")) {
            iconId = R.drawable.ic_weather_cloudy_white_48dp;
        } else if (iconString.equals("partly-cloudy-day")) {
            iconId = R.drawable.ic_weather_partlycloudy_white_48dp;
        } else if (iconString.equals("partly-cloudy-night")) {
            iconId = R.drawable.ic_weather_cloudy_white_48dp;
        }
        return iconId;
    }
}
