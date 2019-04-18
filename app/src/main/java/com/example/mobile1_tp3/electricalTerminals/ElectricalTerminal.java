package com.example.mobile1_tp3.electricalTerminals;

public class ElectricalTerminal
{
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;

    public ElectricalTerminal(String nomBorne, Double latitude, Double longitude)
    {
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

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

}

