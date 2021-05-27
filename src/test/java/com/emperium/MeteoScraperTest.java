package com.emperium;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class MeteoScraperTest {

    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

    @Before
    public void prepare() throws Exception {
        Operation operation =
                sequenceOf(
                        DbOperations.DELETE_ALL,
                        DbOperations.INSERT_REFERENCE_DATA,
                        insertInto("Day")
                                .columns("id", "day", "city_id")
                                .values(5124, "2021-02-02", 773)
                                .build(),
                        insertInto("Measurement")
                                .columns("id", "time", "temperature", "humidity", "wind", "phenomeno", "day_id")
                                .values(27815, "05:00:00", 13, 88, "5 Μπφ Β22Δ", "ΣΥΝΝΕΦΙΑ", 5123)
                                .build()
                                );

        DbSetup dbSetup = new DbSetup(new DriverManagerDestination("jdbc:mysql://localhost:3306/weatherCopy", "USERNAME", "PASSWORD"), operation);

        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Ignore
    @Test
    public void test_CityExists_day_not_set() {
         dbSetupTracker.skipNextLaunch();
    }
}
