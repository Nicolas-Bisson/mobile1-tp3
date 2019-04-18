package com.example.mobile1_tp3.electricalTerminals;

public class ElectricalTerminal
{
    private Long id;
    private String name;
    private String latitude;
    private String longitude;

    public ElectricalTerminal(Long id, String nomBorne, String latitude, String longitude)
    {
        this.id = id;
        this.name = nomBorne;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName()
    {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String nomBorne) { this.name = nomBorne; }

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

