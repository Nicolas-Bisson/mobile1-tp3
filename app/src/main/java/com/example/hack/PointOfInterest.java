package com.example.hack;

public class PointOfInterest
{
    private String ID;
    private String nomAttrait;
    private String latitude;
    private String longitude;

    public PointOfInterest(String ID, String nomBorne, String latitude, String longitude)
    {
        this.ID = ID;
        this.nomAttrait = nomBorne;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointOfInterest(String ID)
    {
        this.ID = ID;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
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

