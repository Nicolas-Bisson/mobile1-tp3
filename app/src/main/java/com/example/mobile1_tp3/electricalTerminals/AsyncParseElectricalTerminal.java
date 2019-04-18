package com.example.mobile1_tp3.electricalTerminals;

import android.os.AsyncTask;

import java.io.InputStream;

public class AsyncParseElectricalTerminal extends AsyncTask<InputStream, Void, Void>
{
    private final Listener listener;

    public AsyncParseElectricalTerminal(Listener listener)
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
        ParseElectricalTerminal.Instance.Parse(inputStreams[0]);
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
