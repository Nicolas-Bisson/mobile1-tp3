package com.example.hack;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

public enum ParserPointOfInterest
{
    Instance;

    public TreeMap<String, PointOfInterest> pointOfInterests;

    public void Parse(InputStream inputStreamInfo, InputStream inputStreamAddress)
    {
        pointOfInterests = new TreeMap<>();
        chargerCSVInfo(inputStreamInfo);
        chargerCSVAdresse(inputStreamAddress);
        System.out.println("");
    }
    private boolean chargerCSVInfo(InputStream inputStreamInfo)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamInfo, "ISO-8859-1"));
            String ligne = bufferedReader.readLine();
            ArrayList<String> subString = new ArrayList<>();

            while (ligne != null)
            {
                String[] info = ligne.split(",");
                if(Resources.getSystem().getConfiguration().locale.getLanguage().equals("fr"))
                pointOfInterests.put(info[0], new PointOfInterest(info[1]));
                else
                pointOfInterests.put(info[0], new PointOfInterest(info[2]));
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamAddress, "ISO-8859-1"));
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
                if(pointOfInterests.containsKey(info[0]))
                {
                    if(info.length >= 15)
                    {
                        pointOfInterests.get(info[0]).setLatitude(info[14]);
                        pointOfInterests.get(info[0]).setLongitude(info[15]);
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
