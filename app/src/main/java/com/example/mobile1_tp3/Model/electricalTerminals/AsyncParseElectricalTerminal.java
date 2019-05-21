package com.example.mobile1_tp3.Model.electricalTerminals;

import android.os.AsyncTask;

import com.example.mobile1_tp3.database.ElectricalTerminalRepository;

import java.io.InputStream;

public class AsyncParseElectricalTerminal extends AsyncTask<InputStream, Void, Void> {
    private final Listener listener;
    ElectricalTerminalRepository terminalRepository;

    public AsyncParseElectricalTerminal(Listener listener, ElectricalTerminalRepository terminalRepository) {
        if (listener == null) {
            throw new IllegalArgumentException("listener doesn't exist");
        }
        this.listener = listener;
        this.terminalRepository = terminalRepository;
    }

    @Override
    protected Void doInBackground(InputStream... inputStreams) {
        ParseElectricalTerminal.Instance.Parse(inputStreams[0], terminalRepository);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onParseElectricalTerminalComplete();
    }

    public interface Listener {
        void onParseElectricalTerminalComplete();
    }
}
