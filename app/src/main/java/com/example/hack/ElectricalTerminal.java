package com.example.hack;

public class ElectricalTerminal
{
    private String nameElectricalTerminal;
    private String namePark;
    private String address;
    private String levelOfCharge;
    private String latitude;
    private String longitude;
    private String cost;

    public ElectricalTerminal(String nomBorne, String namePark, String address, String levelOfCharge, String latitude, String longitude, String cost)
    {
        this.nameElectricalTerminal = nomBorne;
        this.namePark = namePark;
        this.address = address;
        this.levelOfCharge = levelOfCharge;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cost = cost;
    }

    public String getNameElectricalTerminal()
    {
        return nameElectricalTerminal;
    }

    public void setNameElectricalTerminal(String nomBorne) { this.nameElectricalTerminal = nomBorne; }

    public String getNamePark()
    {
        return namePark;
    }

    public void setNamePark(String namePark)
    {
        this.namePark = namePark;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getLevelOfCharge()
    {
        return levelOfCharge;
    }

    public void setLevelOfCharge(String levelOfCharge)
    {
        this.levelOfCharge = levelOfCharge;
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

    public String getCost()
    {
        return cost;
    }

    public void setCost(String cost)
    {
        this.cost = cost;
    }
}

