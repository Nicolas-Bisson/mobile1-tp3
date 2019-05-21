package com.example.mobile1_tp3.Model.electricalTerminals;

import com.example.mobile1_tp3.database.ElectricalTerminalRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public enum ParseElectricalTerminal {
    Instance;

    public static final int NAME_ROW = 0;
    public static final int LATITUDE_ROW = 4;
    public static final int LONGITUDE_ROW = 5;
    public static final String CHARSET_NAME = "ISO-8859-1";

    public void Parse(InputStream inputStream, ElectricalTerminalRepository terminalRepository) {
        loadElectricalTerminalCSV(inputStream, terminalRepository);
        System.out.println("");
    }

    public boolean loadElectricalTerminalCSV(InputStream inputStream, ElectricalTerminalRepository terminalRepository) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, CHARSET_NAME));
            String line = bufferedReader.readLine();
            ArrayList<String> subString;
            int countForReplacements = 0;

            while (line != null) {
                countForReplacements = 0;
                boolean containQuotes = line.contains("\"");
                subString = new ArrayList<>();

                //Get the quote
                while (line.contains("\"")) {
                    int positionFirstQuote = line.indexOf('\"');
                    subString.add(line.substring(positionFirstQuote, line.indexOf('\"', positionFirstQuote + 1) + 1));
                    line = line.replace(subString.get(countForReplacements), "=" + countForReplacements);
                    countForReplacements++;
                }
                line = line.replace(',', '/');
                countForReplacements = 0;

                while (containQuotes && line.contains("=")) {
                    line = line.replace("=" + countForReplacements, subString.get(countForReplacements));
                    countForReplacements++;
                }
                String[] info = line.split("/");
                try {
                    terminalRepository.create(new ElectricalTerminal(info[NAME_ROW], Double.parseDouble(info[LATITUDE_ROW]), Double.parseDouble(info[LONGITUDE_ROW])));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                line = bufferedReader.readLine();
            }

            bufferedReader.readLine();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
