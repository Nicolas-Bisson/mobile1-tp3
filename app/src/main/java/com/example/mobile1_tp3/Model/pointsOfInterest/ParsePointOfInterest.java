package com.example.mobile1_tp3.Model.pointsOfInterest;

import android.content.res.Resources;

import com.example.mobile1_tp3.database.PointOfInterestRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;

public enum ParsePointOfInterest
{
    Instance;

    public static final int ID_ROW = 0;
    public static final int FR_NAME_ROW = 1;
    public static final int EN_NAME_ROW = 2;
    public static final int LATITUDE_ROW = 14;
    public static final int LONGITUDE_ROW = 15;

    private TreeMap<String, PointOfInterest> pointOfInterests;
    private PointOfInterestRepository pointOfInterestRepository;

    public void Parse(InputStream inputStreamInfo, InputStream inputStreamAddress, PointOfInterestRepository pointOfInterestRepository)
    {
        this.pointOfInterestRepository = pointOfInterestRepository;
        pointOfInterests = new TreeMap<>();
        chargeCSVInfo(inputStreamInfo);
        chargeCSVAdress(inputStreamAddress);
        pointOfInterests.clear();
        System.out.println("");
    }

    public boolean chargeCSVInfo(InputStream inputStreamInfo)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamInfo, "ISO-8859-1"));
            String ligne = bufferedReader.readLine();

            while (ligne != null)
            {

                String[] info = ligne.split(",");
                if(Resources.getSystem().getConfiguration().locale.getLanguage().equals("fr"))
                pointOfInterests.put(info[ID_ROW], new PointOfInterest(info[FR_NAME_ROW]));
                else
                pointOfInterests.put(info[ID_ROW], new PointOfInterest(info[EN_NAME_ROW]));
                ligne = bufferedReader.readLine();
            }

            bufferedReader.readLine();
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return false;
        }
    }

    public boolean chargeCSVAdress(InputStream inputStreamAddress)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamAddress, "ISO-8859-1"));
            String ligne = bufferedReader.readLine();
            ArrayList<String> subString;
            int countForReplacements = 0;

            while (ligne != null)
            {
                countForReplacements = 0;
                boolean containQuotes = ligne.contains("\"");
                subString = new ArrayList<>();
                while(ligne.contains("\""))
                {
                    int positionFirstQuote = ligne.indexOf('\"');
                    subString.add(ligne.substring(positionFirstQuote, ligne.indexOf('\"', positionFirstQuote + 1)+ 1));
                    ligne = ligne.replace(subString.get(countForReplacements), "=" + countForReplacements);
                    countForReplacements++;
                }
                ligne = ligne.replace(',', '/');
                countForReplacements = 0;
                while(containQuotes && ligne.contains("="))
                {
                    ligne = ligne.replace("=" + countForReplacements, subString.get(countForReplacements));
                    countForReplacements++;
                }
                String[] info = ligne.split("/");;
                if(pointOfInterests.containsKey(info[ID_ROW]))
                {
                    if(info.length >= 15)
                    {
                        try {
                            pointOfInterestRepository.create(new PointOfInterest(
                                    pointOfInterests.get(info[ID_ROW]).getName(),
                                    Double.parseDouble(info[LATITUDE_ROW]),
                                    Double.parseDouble(info[LONGITUDE_ROW])));
                        }
                        catch (NumberFormatException ex) {
                            //ex.printStackTrace();
                        }

                    }
                }
                ligne = bufferedReader.readLine();
            }

            for (TreeMap.Entry<String, PointOfInterest> entry : pointOfInterests.entrySet())
            {
                entry.getValue().getLatitude();
                entry.getValue().getLongitude();
            }
            bufferedReader.readLine();
            return true;



        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return false;
        }
    }
}
