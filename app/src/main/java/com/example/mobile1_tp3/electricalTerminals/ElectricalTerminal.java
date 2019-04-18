package com.example.mobile1_tp3.electricalTerminals;

public class ElectricalTerminal
{
    private Long id;
    private String name;
    private Float latitude;
    private Float longitude;

    public ElectricalTerminal(Long id, String nomBorne, Float latitude, Float longitude)
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

    public Float getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Float latitude)
    {
        this.latitude = latitude;
    }

    public Float getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Float longitude)
    {
        this.longitude = longitude;
    }

}

