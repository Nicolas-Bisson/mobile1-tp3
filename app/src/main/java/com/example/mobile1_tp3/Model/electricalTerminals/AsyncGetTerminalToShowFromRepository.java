package com.example.mobile1_tp3.Model.electricalTerminals;

import android.os.AsyncTask;

import com.example.mobile1_tp3.database.ElectricalTerminalRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

//BEN_REVIEW : Le "FromRepository" est pas utile.
//BEN_REVIEW : "GetTerminalToShow" ? Pas clair.
public class AsyncGetTerminalToShowFromRepository extends AsyncTask<ElectricalTerminalRepository, Void, List<ElectricalTerminal>> {

    private final Listener listener;
    private LatLng cameraPosition;

    public AsyncGetTerminalToShowFromRepository(Listener listener, LatLng cameraPosition) {
        if (listener == null) {
            throw new IllegalArgumentException("listener doesn't exist");
        }
        this.listener = listener;
        this.cameraPosition = cameraPosition;
    }

    @Override
    protected List<ElectricalTerminal> doInBackground(ElectricalTerminalRepository... terminalRepositories) {
        return terminalRepositories[0].readByPosition(cameraPosition);
    }

    @Override
    protected void onPostExecute(List<ElectricalTerminal> electricalTerminals) {
        super.onPostExecute(electricalTerminals);
        listener.onGetTerminalToShowFromRepositoryComplete(electricalTerminals);
    }

    public interface Listener {
        void onGetTerminalToShowFromRepositoryComplete(List<ElectricalTerminal> electricalTerminals);
    }
}
