package com.example.hack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public enum ParserElectricalTerminal
{
    Instance;

    public ArrayList<ElectricalTerminal> electricalTerminals;

    public void Parse(InputStream inputStream)
    {
        electricalTerminals = new ArrayList<>();
        loadCSV(inputStream);
        System.out.println("");
    }
    private boolean loadCSV(InputStream inputStream)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
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
                String[] info = ligne.split("/");
                electricalTerminals.add(new ElectricalTerminal(info[0], info[4],info[5]));
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
