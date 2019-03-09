package com.example.hack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public enum Parser
{
    Instance;

    public static final String nomFichier = "R.raw.bornes.csv";

    public ArrayList<Borne> bornes;

    public void Parse()
    {
        bornes = new ArrayList<>();
        chargerCSV(nomFichier);
        System.out.println("");
    }
    private boolean chargerCSV(String nomFichier)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(nomFichier));
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
                String[] info = ligne.split("/");
                bornes.add(new Borne(info[0], info[1],info[2],info[3],info[4],info[5],info[6]));
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
