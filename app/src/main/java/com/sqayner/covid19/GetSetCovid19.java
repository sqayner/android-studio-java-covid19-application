package com.sqayner.covid19;

public class GetSetCovid19 {

    private String latitude;
    private String longitude;

    private String country;
    private String country_code;

    private String confirmed;
    private String deaths;
    private String recovered;

    private String province;

    GetSetCovid19(String mLatitude,
                  String mLongitude,
                  String mCountry,
                  String mCountry_code,
                  String mConfirmed,
                  String mDeaths,
                  String mRecovered,
                  String mProvince) {
        latitude = mLatitude;
        longitude = mLongitude;
        country = mCountry;
        country_code = mCountry_code;
        confirmed = mConfirmed;
        deaths = mDeaths;
        recovered = mRecovered;
        province = mProvince;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }
}
