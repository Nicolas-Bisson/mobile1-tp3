package com.example.mobile1_tp3.pointsOfInterest;

import android.os.AsyncTask;

import java.io.InputStream;

public class AsyncParsePointOfInterest extends AsyncTask<InputStream, Void, Void>
{
    private final Listener listener;

    public AsyncParsePointOfInterest(Listener listener)
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
        ParsePointOfInterest.Instance.Parse(inputStreams[0], inputStreams[1]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        listener.onParsePointOfInterestComplete();
    }

    public interface Listener {
        void onParsePointOfInterestComplete();
    }
}
