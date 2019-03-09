package com.example.hack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public enum ParserPointOfInterest
{
    Instance;

    public ArrayList<PointOfInterest> pointOfInterests;

    public void Parse(InputStream inputStreamInfo, InputStream inputStreamAddress)
    {
        pointOfInterests = new ArrayList<>();
        chargerCSVInfo(inputStreamInfo);
        chargerCSVAdresse(inputStreamAddress);
        System.out.println("");
    }
    private boolean chargerCSVInfo(InputStream inputStreamInfo)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamInfo));
            String ligne = bufferedReader.readLine();
            ArrayList<String> subString = new ArrayList<>();
            int i = 0;

            while (ligne != null)
            {
                String[] info = ligne.split(",");
                pointOfInterests.add(new PointOfInterest(info[0]));
                pointOfInterests.get((pointOfInterests.size() - 1)).setNomAttrait(info[1]);
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
    private boolean chargerCSVAdresse(InputStream inputStreamAddress)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamAddress));
            String ligne = bufferedReader.readLine();
            ArrayList<String> subString = new ArrayList<>();
            int i = 0;

            while (ligne != null)
            {
                i = 0;
                boolean containQuotes = ligne.contains("\"");
                subString = new ArrayList<>();
                while(ligne.contains("\""))
                {
                    subString.add(ligne.substring(ligne.indexOf('\"'), ligne.indexOf('\"', ligne.indexOf('\"') + 1)+ 1));
                    ligne = ligne.replace(subString.get(i), "=" + i);
                    i++;
                }
                ligne = ligne.replace(',', '/');
                i = 0;
                while(containQuotes && ligne.contains("="))
                {
                    ligne = ligne.replace("=" + i, subString.get(i));
                    i++;
                }
                String[] info = ligne.split("/");;
                for(int j = 0; j < pointOfInterests.size() - 1; j++)
                {
                    PointOfInterest pointOfInterestAVerifier = pointOfInterests.get(j);
                    if(pointOfInterestAVerifier.getID().equals(info[0]))
                    {
                        if(info.length >= 15)
                        {
                            pointOfInterestAVerifier.setLatitude(info[14]);
                            pointOfInterestAVerifier.setLongitude(info[15]);
                        }
                    }
                }
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
}
