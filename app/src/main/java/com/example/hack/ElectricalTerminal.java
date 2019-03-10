package com.example.hack;

public class ElectricalTerminal
{
    private String nameElectricalTerminal;
    private String latitude;
    private String longitude;

    public ElectricalTerminal(String nomBorne, String latitude, String longitude)
    {
        this.nameElectricalTerminal = nomBorne;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNameElectricalTerminal()
    {
        return nameElectricalTerminal;
    }

    public void setNameElectricalTerminal(String nomBorne) { this.nameElectricalTerminal = nomBorne; }

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

