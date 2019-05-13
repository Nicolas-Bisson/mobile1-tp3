package com.example.mobile1_tp3.database;

import com.google.android.gms.maps.model.LatLng;
import com.example.mobile1_tp3.Model.pointsOfInterest.PointOfInterest;

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
public class PointOfInterestRepositoryTest {

    private PointOfInterestRepository repository;

    @Before
    public void before() {
        DbConnectionFactory database = new DbConnectionFactory(InstrumentationRegistry.getTargetContext());
        database.onUpgrade(database.getWritableDatabase(), DbConnectionFactory.DB_VERSION, DbConnectionFactory.DB_VERSION);

        repository = new PointOfInterestRepository(database.getWritableDatabase());
    }

    @Test
    public void canAddNewPointOfInterest() {
        PointOfInterest pointOfInterest = new PointOfInterest("terminal", 50.0, 50.0);

        repository.create(pointOfInterest);

        List<PointOfInterest> pointsOfInterest = repository.readAll();
        assertEquals("No point of interest inserted", 1, pointsOfInterest.size());

        PointOfInterest insertedPointOfInterest = pointsOfInterest.get(0);
        assertEquals("Names aren't Equals", "terminal", insertedPointOfInterest.getName());
        assertEquals("Latitude aren't Equals", 50.0, insertedPointOfInterest.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 50.0, insertedPointOfInterest.getLongitude(), 0.1f);
    }

    @Test
    public void canAddMultiplePointsOfInterest() {
        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);
        PointOfInterest pointOfInterest2 = new PointOfInterest("pointOfInterest2", 5.0, 5.0);

        repository.create(pointOfInterest);
        repository.create(pointOfInterest2);

        List<PointOfInterest> pointsOfInterest = repository.readAll();
        assertEquals("Not all points of interest were inserted", 2, pointsOfInterest.size());
    }

    @Test
    public void shouldGetNewIdWhenCreated() {
        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        assertNotNull("Point of interest id is null.", pointOfInterest.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantInsertNullPointOfInterest() {
        repository.create(null);
    }

    @Test
    public void canReadPointOfInterestWithId() {
        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        PointOfInterest insertedPointOfInterest = repository.readById(1);

        assertEquals("Names aren't Equals", "pointOfInterest", insertedPointOfInterest.getName());
        assertEquals("Latitude aren't Equals", 50.0, insertedPointOfInterest.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 50.0, insertedPointOfInterest.getLongitude(), 0.1f);
    }

    @Test
    public void cantReadPointOfInterestWithUnexistingId() {
        PointOfInterest pointOfInterest = repository.readById(1);

        assertNull("Can get unexisting id", pointOfInterest);
    }

    @Test
    public void canReadMultiplePointsOfInterest() {
        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);
        PointOfInterest pointOfInterest2 = new PointOfInterest("pointOfInterest2", 5.0, 5.0);

        repository.create(pointOfInterest);
        repository.create(pointOfInterest2);

        List<PointOfInterest> pointsOfInterest = repository.readAll();

        PointOfInterest insertedPointOfInterest = pointsOfInterest.get(0);
        assertEquals("Names aren't Equals", "pointOfInterest", insertedPointOfInterest.getName());
        assertEquals("Latitude aren't Equals", 50.0, insertedPointOfInterest.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 50.0, insertedPointOfInterest.getLongitude(), 0.1f);

        insertedPointOfInterest = pointsOfInterest.get(1);
        assertEquals("Names aren't Equals", "pointOfInterest2", insertedPointOfInterest.getName());
        assertEquals("Latitude aren't Equals", 5.0, insertedPointOfInterest.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 5.0, insertedPointOfInterest.getLongitude(), 0.1f);
    }

    @Test
    public void canReadPointOfInterestOnPosition() {
        LatLng position = new LatLng(50.0, 50.0);

        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        List<PointOfInterest> pointsOfInterest = repository.readByPosition(position);
        assertEquals("No terminal read", 1, pointsOfInterest.size());
    }

    @Test
    public void canReadPointOfInterestCloseToPosition() {
        LatLng position = new LatLng(49.99, 49.99);

        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        List<PointOfInterest> pointsOfInterest = repository.readByPosition(position);
        assertEquals("No terminal read", 1, pointsOfInterest.size());
    }

    @Test
    public void canReadPointOfInterestAtMaxDetectionRange() {
        LatLng position = new LatLng(50.0 - MarkerRepository.DETECTION_RANGE, 50.0 - MarkerRepository.DETECTION_RANGE);

        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        List<PointOfInterest> pointsOfInterest = repository.readByPosition(position);
        assertEquals("No terminal read", 1, pointsOfInterest.size());
    }

    @Test
    public void cantReadPointOfInterestOverMaxDetectionRange() {
        LatLng position = new LatLng(50.0 - MarkerRepository.DETECTION_RANGE - 0.01, 50.0 - MarkerRepository.DETECTION_RANGE - 0.01);

        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        List<PointOfInterest> pointsOfInterest = repository.readByPosition(position);
        assertEquals("No terminal read", 0, pointsOfInterest.size());
    }

    @Test
    public void canReadMultiplePointOfInterestInDetectionRange() {
        LatLng position = new LatLng(50.0, 50.0);

        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);
        PointOfInterest pointOfInterest2 = new PointOfInterest("pointOfInterest2", 50.03, 50.03);

        repository.create(pointOfInterest);
        repository.create(pointOfInterest2);

        List<PointOfInterest> pointsOfInterest = repository.readByPosition(position);
        assertEquals("No terminal read", 2, pointsOfInterest.size());
    }

    @Test
    public void canUpdatePointOfInterestInformation() {

        PointOfInterest pointOfInterest = new PointOfInterest("pointOfInterest", 50.0, 50.0);

        repository.create(pointOfInterest);

        pointOfInterest.setName("new");
        pointOfInterest.setLatitude(5.0);
        pointOfInterest.setLongitude(25.0);

        repository.update(pointOfInterest);


        List<PointOfInterest> terminals = repository.readAll();

        PointOfInterest insertedTerminal = terminals.get(0);
        assertEquals("Names aren't Equals", "new", insertedTerminal.getName());
        assertEquals("Latitude aren't Equals", 5.0, insertedTerminal.getLatitude(), 0.1f);
        assertEquals("Longitude aren't Equals", 25.0, insertedTerminal.getLongitude(), 0.1f);
    }
}
