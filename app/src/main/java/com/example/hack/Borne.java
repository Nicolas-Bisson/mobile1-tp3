public class Borne
{
    private String nomBorne;
    private String nomParc;
    private String adresse;
    private String niveauRecharge;
    private String latitude;
    private String longitude;
    private String cout;

    public Borne(String nomBorne, String nomParc, String adresse, String niveauRecharge, String latitude, String longitude, String cout)
    {
        this.nomBorne = nomBorne;
        this.nomParc = nomParc;
        this.adresse = adresse;
        this.niveauRecharge = niveauRecharge;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cout = cout;
    }

    public String getNomBorne()
    {
        return nomBorne;
    }

    public void setNomBorne(String nomBorne)
    {
        this.nomBorne = nomBorne;
    }

    public String getNomParc()
    {
        return nomParc;
    }

    public void setNomParc(String nomParc)
    {
        this.nomParc = nomParc;
    }

    public String getAdresse()
    {
        return adresse;
    }

    public void setAdresse(String adresse)
    {
        this.adresse = adresse;
    }

    public String getNiveauRecharge()
    {
        return niveauRecharge;
    }

    public void setNiveauRecharge(String niveauRecharge)
    {
        this.niveauRecharge = niveauRecharge;
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

    public String getCout()
    {
        return cout;
    }

    public void setCout(String cout)
    {
        this.cout = cout;
    }
}

