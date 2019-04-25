package com.example.mobile1_tp3.pointsOfInterest;

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

    private TreeMap<String, PointOfInterest> pointOfInterests;
    private PointOfInterestRepository pointOfInterestRepository;

    public void Parse(InputStream inputStreamInfo, InputStream inputStreamAddress, PointOfInterestRepository pointOfInterestRepository)
    {
        this.pointOfInterestRepository = pointOfInterestRepository;
        pointOfInterests = new TreeMap<>();
        chargerCSVInfo(inputStreamInfo);
        chargerCSVAdresse(inputStreamAddress);
        pointOfInterests.clear();
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
                        try {
                            pointOfInterestRepository.create(new PointOfInterest(
                                    pointOfInterests.get(info[0]).getName(),
                                    Double.parseDouble(info[14]),
                                    Double.parseDouble(info[15])));
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
