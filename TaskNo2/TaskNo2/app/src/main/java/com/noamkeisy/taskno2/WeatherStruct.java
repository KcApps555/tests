package com.noamkeisy.taskno2;

public class WeatherStruct {
    private String description;
    private int temp;
    private int tempMin;
    private int tempMax;
    private String city;
    private String dayNightIcon;

    public WeatherStruct() {
    }

    public WeatherStruct(String description, int temp, int tempMin, int tempMax, String city, String dayNightIcon) {
        this.description = description;
        this.temp = temp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.city = city;
        this.dayNightIcon = dayNightIcon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMan(int tempMax) {
        this.tempMax = tempMax;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDayNightIcon() {
        return dayNightIcon;
    }

    public void setDayNightIcon(String dayNightIcon) {
        this.dayNightIcon = dayNightIcon;
    }
}
