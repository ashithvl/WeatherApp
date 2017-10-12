package com.blueangles.weatherapp.model;

/**
 * Created by Ashith VL on 10/8/2017.
 */

public class DailyWeatherReport {
    private String cityName;
    private String country;
    private int currentTemp;
    private int minTemp;
    private int maaxTemp;
    private String weather;
    private String formattedDate;

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";

    public DailyWeatherReport() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaaxTemp() {
        return maaxTemp;
    }

    public void setMaaxTemp(int maaxTemp) {
        this.maaxTemp = maaxTemp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getFormattedDate() {

        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
