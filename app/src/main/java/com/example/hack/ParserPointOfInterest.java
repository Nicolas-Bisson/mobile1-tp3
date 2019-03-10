package com.example.hack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;

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
            int countOfCreatedPointOfInterest = 0;

            while (ligne != null)
            {
                String[] info = ligne.split(",");
                pointOfInterests.add(new PointOfInterest(info[0]));
                pointOfInterests.get(countOfCreatedPointOfInterest).setNomAttrait(info[1]);
                countOfCreatedPointOfInterest++;
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
                for(int countVerification = 0; countVerification < pointOfInterests.size() - 1; countVerification++)
                {
                    PointOfInterest pointOfInterestAVerifier = pointOfInterests.get(countVerification);
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
