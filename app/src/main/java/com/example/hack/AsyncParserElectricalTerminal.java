package com.example.hack;

import android.os.AsyncTask;

import java.io.InputStream;

public class AsyncParserElectricalTerminal extends AsyncTask<InputStream, Void, Void>
{
    private final Listener listener;

    public AsyncParserElectricalTerminal(Listener listener)
    {
        if(listener == null)
        {
            throw new IllegalArgumentException("listener doesn't exist");
        }
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(InputStream... inputStreams)
    {
        ParserElectricalTerminal.Instance.Parse(inputStreams[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        listener.onParseElectricalTerminalComplete();
    }

    public interface Listener {
        void onParseElectricalTerminalComplete();
    }
}
