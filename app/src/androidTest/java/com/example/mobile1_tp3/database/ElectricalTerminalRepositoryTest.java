package com.example.mobile1_tp3.database;

import android.content.Context;

import com.example.mobile1_tp3.electricalTerminals.ElectricalTerminal;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class ElectricalTerminalRepositoryTest {

    private ElectricalTerminalRepository repository;

    @Before
    public void before() {
        DbConnectionFactory database = new DbConnectionFactory(InstrumentationRegistry.getTargetContext());
        database.onUpgrade(database.getWritableDatabase(), DbConnectionFactory.DB_VERSION, DbConnectionFactory.DB_VERSION);

        repository = new ElectricalTerminalRepository(database.getWritableDatabase());
    }

    @Test
    public void canAddNewTerminal() {
        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        List<ElectricalTerminal> terminals = repository.readAll();
        assertEquals("No terminal inserted", 1, terminals.size());

        ElectricalTerminal insertedTerminal = terminals.get(0);
        assertEquals("Names aren't Equals", "terminal", insertedTerminal.getName());
        assertEquals("Latitude aren't Equals", 50.0, insertedTerminal.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 50.0, insertedTerminal.getLongitude(), 0.1f);
    }

    @Test
    public void canAddMultipleTerminals() {
        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);
        ElectricalTerminal terminal2 = new ElectricalTerminal("terminal2", 5.0, 5.0);

        repository.create(terminal);
        repository.create(terminal2);

        List<ElectricalTerminal> terminals = repository.readAll();
        assertEquals("Not all terminals were inserted", 2, terminals.size());
    }

    @Test
    public void cannotAddNMultipleTerminalsAtSamePosition() {
        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);
        ElectricalTerminal terminal2 = new ElectricalTerminal("terminal2", 50.0, 50.0);

        repository.create(terminal);
        repository.create(terminal2);

        List<ElectricalTerminal> terminals = repository.readAll();
        assertEquals("Terminal unique position constraint was not followed", 1, terminals.size());
    }

    @Test
    public void shouldGetNewIdWhenCreated() {
        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        assertNotNull("Country id is null.", terminal.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantInsertNullTerminal() {
        repository.create(null);
    }

    @Test
    public void canReadTerminalWithId() {
        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        ElectricalTerminal insertedTerminal = repository.readById(1);

        assertEquals("Names aren't Equals", "terminal", insertedTerminal.getName());
        assertEquals("Latitude aren't Equals", 50.0, insertedTerminal.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 50.0, insertedTerminal.getLongitude(), 0.1f);
    }

    @Test
    public void cantReadTerminalWithUnexistingId() {
        ElectricalTerminal terminal = repository.readById(1);

        assertNull("Can get unexisting id", terminal);
    }

    @Test
    public void canReadMultipleTerminals() {
        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);
        ElectricalTerminal terminal2 = new ElectricalTerminal("terminal2", 5.0, 5.0);

        repository.create(terminal);
        repository.create(terminal2);

        List<ElectricalTerminal> terminals = repository.readAll();

        ElectricalTerminal insertedTerminal = terminals.get(0);
        assertEquals("Names aren't Equals", "terminal", insertedTerminal.getName());
        assertEquals("Latitude aren't Equals", 50.0, insertedTerminal.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 50.0, insertedTerminal.getLongitude(), 0.1f);

        insertedTerminal = terminals.get(1);
        assertEquals("Names aren't Equals", "terminal2", insertedTerminal.getName());
        assertEquals("Latitude aren't Equals", 5.0, insertedTerminal.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 5.0, insertedTerminal.getLongitude(), 0.1f);
    }

    @Test
    public void canReadTerminalOnPosition() {
        LatLng position = new LatLng(50.0, 50.0);

        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        List<ElectricalTerminal> terminals = repository.readByPosition(position);
        assertEquals("No terminal read", 1, terminals.size());
    }

    @Test
    public void canReadTerminalCloseToPosition() {
        LatLng position = new LatLng(49.99, 49.99);

        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        List<ElectricalTerminal> terminals = repository.readByPosition(position);
        assertEquals("No terminal read", 1, terminals.size());
    }

    @Test
    public void canReadTerminalAtMaxDetectionRange() {
        LatLng position = new LatLng(50.0 - MarkerRepository.DETECTION_RANGE, 50.0 - MarkerRepository.DETECTION_RANGE);

        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        List<ElectricalTerminal> terminals = repository.readByPosition(position);
        assertEquals("No terminal read", 1, terminals.size());
    }

    @Test
    public void cantReadTerminalOverMaxDetectionRange() {
        LatLng position = new LatLng(50.0 - MarkerRepository.DETECTION_RANGE - 0.01, 50.0 - MarkerRepository.DETECTION_RANGE - 0.01);

        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        List<ElectricalTerminal> terminals = repository.readByPosition(position);
        assertEquals("No terminal read", 0, terminals.size());
    }

    @Test
    public void canReadMultipleTerminalInDetectionRange() {
        LatLng position = new LatLng(50.0, 50.0);

        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);
        ElectricalTerminal terminal2 = new ElectricalTerminal("terminal2", 50.03, 50.03);

        repository.create(terminal);
        repository.create(terminal2);

        List<ElectricalTerminal> terminals = repository.readByPosition(position);
        assertEquals("No terminal read", 2, terminals.size());
    }

    @Test
    public void canUpdateTerminalInformation() {

        ElectricalTerminal terminal = new ElectricalTerminal("terminal", 50.0, 50.0);

        repository.create(terminal);

        terminal.setName("new");
        terminal.setLatitude(5.0);
        terminal.setLongitude(25.0);

        repository.update(terminal);


        List<ElectricalTerminal> terminals = repository.readAll();

        ElectricalTerminal insertedTerminal = terminals.get(0);
        assertEquals("Names aren't Equals", "new", insertedTerminal.getName());
        assertEquals("Latitude aren't Equals", 5.0, insertedTerminal.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 25.0, insertedTerminal.getLongitude(), 0.1f);
    }
}
