package com.example.mobile1_tp3.pointsOfInterest;

public class PointOfInterest
{
    private String nomAttrait;
    private String latitude;
    private String longitude;

    public PointOfInterest(String nomAttrait, String latitude, String longitude)
    {
        this.nomAttrait = nomAttrait;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointOfInterest(String nomAttrait)
    {
        this.nomAttrait = nomAttrait;
    }

    public String getNomAttrait()
    {
        return nomAttrait;
    }

    public void setNomAttrait(String nomAttrait)
    {
        this.nomAttrait = nomAttrait;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
}

