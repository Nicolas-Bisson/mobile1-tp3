package com.example.mobile1_tp3.Model.pointsOfInterest;

import android.os.AsyncTask;

import com.example.mobile1_tp3.database.PointOfInterestRepository;

import java.io.InputStream;

public class AsyncParsePointOfInterest extends AsyncTask<InputStream, Void, Void> {
    private final Listener listener;
    PointOfInterestRepository pointOfInterestRepository;

    public AsyncParsePointOfInterest(Listener listener, PointOfInterestRepository pointOfInterestRepository) {
        if (listener == null) {
            throw new IllegalArgumentException("listener doesn't exist");
        }
        this.listener = listener;
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    @Override
    protected Void doInBackground(InputStream... inputStreams) {
        ParsePointOfInterest.Instance.Parse(inputStreams[0], inputStreams[1], pointOfInterestRepository);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onParsePointOfInterestComplete();
    }

    public interface Listener {
        void onParsePointOfInterestComplete();
    }
}
