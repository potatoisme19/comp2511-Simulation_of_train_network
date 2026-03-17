package trains;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
//import java.util.concurrent.TimeUnit;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Timeout;

import unsw.response.models.LoadInfoResponse;
//import unsw.response.models.LoadInfoResponse;
import unsw.response.models.StationInfoResponse;
//import unsw.response.models.StationInfoResponse;
import unsw.response.models.TrackInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.trains.TrainsController;
import unsw.utils.TrackType;

//@Timeout(value = 5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)

public class MyTests {

    public static void main(String[] args) {
        MyTests tests = new MyTests();
        tests.testNormalPassengersOnRepairTrain();
    }

    @Test
    public void testBreakableTrackDamages() {
        TrainsController controller = new TrainsController();

        // Use CargoStation for cargo
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", true);

        // Create cargo first
        controller.createCargo("s1", "s2", "c1", 2000);

        // Verify cargo is at station
        StationInfoResponse s1Info = controller.getStationInfo("s1");
        assertEquals(1, s1Info.getLoads().size(), "Cargo should be at station s1");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // Initial state
        TrackInfoResponse initialTrack = controller.getTrackInfo("t1-2");
        assertEquals(10, initialTrack.getDurability());
        assertEquals(TrackType.UNBROKEN, initialTrack.getType());

        // Run simulation until train reaches s2
        // Distance = 10km, speed = 3km/min, so takes ~3.33 ticks + boarding
        controller.simulate(5);

        // Check track state after journey
        TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");

        // Damage = 1 + ceil(2000/1000) = 1 + 2 = 3
        assertEquals(7, trackInfo.getDurability(), "Track should have lost 3 durability");
        assertEquals(TrackType.UNBROKEN, trackInfo.getType());

        // Train should be at s2
        TrainInfoResponse trainInfo = controller.getTrainInfo("train1");
        assertEquals("s2", trainInfo.getLocation());

        // Cargo should be delivered (not at station and not on train)
        s1Info = controller.getStationInfo("s1");
        assertEquals(0, s1Info.getLoads().size(), "Cargo should be delivered from s1");

        StationInfoResponse s2Info = controller.getStationInfo("s2");
        assertEquals(0, s2Info.getLoads().size(), "Cargo should not be at s2 (delivered)");

        assertEquals(0, trainInfo.getLoads().size(), "Cargo should not be on train (delivered)");
    }

    @Test
    public void testTrackBreaksWithTwoTrains() {
        TrainsController controller = new TrainsController();

        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", true);

        // Create two trains with 3000kg each
        controller.createCargo("s1", "s2", "c1", 3000);
        controller.createCargo("s1", "s2", "c2", 3000);

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
            controller.createTrain("train2", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        controller.simulate(5);

        TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(2, trackInfo.getDurability(), "After first trip: should be 2");

        // Return trip: simulate exactly enough ticks to reach s1 but not start next journey
        // Empty speed = 3.0 km/min, needs 10/3.0 = 3.33 ticks → 4 ticks ensures arrival
        controller.simulate(4);

        trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(0, trackInfo.getDurability(), "After return trip: should be 0");
        assertEquals(TrackType.BROKEN, trackInfo.getType(), "Track should be BROKEN");

        // Next tick: auto-repair happens
        controller.simulate(1);

        trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(1, trackInfo.getDurability(), "After repair: should be 1");
        assertEquals(TrackType.UNBROKEN, trackInfo.getType(), "Track should be UNBROKEN");
    }

    @Test
    public void testNormalTrainStuckOnBrokenTrack() {
        TrainsController controller = new TrainsController();

        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", true);

        // Create first train with 5000kg cargo
        controller.createCargo("s1", "s2", "c1", 5000);

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // === PART 1: First train travels to s2 ===
        controller.simulate(7);

        TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(4, trackInfo.getDurability(), "Track should be at 4 after first trip");
        assertEquals(TrackType.UNBROKEN, trackInfo.getType());
        assertEquals("s2", controller.getTrainInfo("train1").getLocation());

        // === PART 2: Create second train while first returns ===
        controller.createCargo("s1", "s2", "c2", 4000);
        assertDoesNotThrow(() -> {
            controller.createTrain("train2", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // Train1 returns empty, Train2 departs
        controller.simulate(4);

        trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(3, trackInfo.getDurability(), "After return + departure");

        // === PART 3: Train2 continues to s2 and breaks track ===
        controller.simulate(2); // Train2 arrives and track breaks

        trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(0, trackInfo.getDurability(), "Track should break");
        assertEquals(TrackType.BROKEN, trackInfo.getType(), "Track should be BROKEN");
        assertEquals("s2", controller.getTrainInfo("train2").getLocation());

        // === PART 4: Test that normal train gets stuck on broken track ===
        // Create a third train at s1
        controller.createCargo("s1", "s2", "c3", 1000);
        assertDoesNotThrow(() -> {
            controller.createTrain("train3", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // Get initial position
        TrainInfoResponse beforeMove = controller.getTrainInfo("train3");
        assertEquals("s1", beforeMove.getLocation());

        // Try to move - should be stuck (track is broken)
        controller.simulate(1);

        TrainInfoResponse afterMove = controller.getTrainInfo("train3");
        assertEquals(beforeMove.getPosition(), afterMove.getPosition(),
                "Normal train should not move on broken track");
        assertEquals("s1", afterMove.getLocation(), "Train should still be at s1");

        // Track should have auto-repaired to 1 after this tick
        trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(1, trackInfo.getDurability(), "Track should auto-repair to 1");
        assertEquals(TrackType.UNBROKEN, trackInfo.getType(), "Track should be UNBROKEN");
    }

    @Test
    public void testRepairTrainCanMoveOnBrokenTrack() {
        TrainsController controller = new TrainsController();

        // Create stations and track
        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", true);

        // === PART 1: Break the track using two cargo trains ===
        controller.createCargo("s1", "s2", "c1", 3000);
        controller.createCargo("s1", "s2", "c2", 3000);

        assertDoesNotThrow(() -> {
            controller.createTrain("cargo1", "CargoTrain", "s1", List.of("s1", "s2"));
            controller.createTrain("cargo2", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        // First trip: both with 3000kg → damage 4+4=8 → 10→2
        controller.simulate(5);

        // Return trip: both empty → damage 1+1=2 → 2→0 (track breaks)
        controller.simulate(4); // 4 ticks for empty trains to return

        // VERIFY TRACK IS BROKEN IMMEDIATELY
        TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(0, trackInfo.getDurability(), "Track should be broken");
        assertEquals(TrackType.BROKEN, trackInfo.getType(), "Track should be BROKEN");

        // === PART 2: Test repair train movement on broken track ===

        // Create repair train at s1
        assertDoesNotThrow(() -> {
            controller.createTrain("repair1", "RepairTrain", "s1", List.of("s1", "s2"));
        });

        // Get initial position
        TrainInfoResponse beforeMove = controller.getTrainInfo("repair1");
        assertEquals("s1", beforeMove.getLocation());

        double startX = beforeMove.getPosition().getX();

        // Repair train should move even though track is broken
        controller.simulate(1);

        TrainInfoResponse afterMove = controller.getTrainInfo("repair1");
        double newX = afterMove.getPosition().getX();

        // Verify train moved
        assertTrue(newX > startX, "Repair train should move forward on broken track");
        assertNotEquals(beforeMove.getPosition(), afterMove.getPosition(),
                "Repair train should move on broken track");

        // Location should be null (on track, not at station)
        assertNull(afterMove.getLocation(), "Repair train should be on track");

        // Track should now have auto-repaired to 1
        trackInfo = controller.getTrackInfo("t1-2");
        assertEquals(1, trackInfo.getDurability(), "Track should auto-repair to 1");
        assertEquals(TrackType.UNBROKEN, trackInfo.getType(), "Track should be UNBROKEN");

        // === PART 3: Verify repair train can complete journey ===
        // Continue simulating until repair train reaches s2
        controller.simulate(4); // Repair train speed = 2.0 km/min, needs 5 ticks total

        afterMove = controller.getTrainInfo("repair1");
        assertEquals("s2", afterMove.getLocation(), "Repair train should reach s2");
    }

    @Test
    public void testMechanicsBoostTrackRepair() {
        TrainsController controller = new TrainsController();

        // Create CentralStation that accepts both passengers and cargo
        controller.createStation("central", "CentralStation", 0, 0);
        controller.createStation("central2", "CentralStation", 10, 0);
        controller.createTrack("test_track", "central", "central2", true);

        // === PART 1: Break the track ===
        // Use a cargo train to break it
        controller.createCargo("central", "central2", "heavy1", 5000);
        StationInfoResponse stationInfo = controller.getStationInfo("central");
        assertDoesNotThrow(() -> {
            controller.createTrain("breaker", "CargoTrain", "central",
                    List.of("central", "central2"));
        });

        // First trip
        controller.simulate(7);
        TrackInfoResponse track = controller.getTrackInfo("test_track");
        assertEquals(4, track.getDurability(), "Should be 4 after first trip");

        // Create new cargo for second trip
        controller.createCargo("central", "central2", "heavy2", 5000);
        stationInfo = controller.getStationInfo("central");

        // Return empty and second trip
        controller.simulate(4); // Return empty
        track = controller.getTrackInfo("test_track");
        assertEquals(3, track.getDurability(), "Should be 3 after return");

        controller.simulate(7); // Second trip
        track = controller.getTrackInfo("test_track");

        assertEquals(0, track.getDurability(), "Track should be broken");
        assertEquals(TrackType.BROKEN, track.getType());

        // === PART 2: Create repair train and mechanics ===

        // Create repair train
        assertDoesNotThrow(() -> {
            controller.createTrain("repair1", "RepairTrain", "central",
                    List.of("central", "central2"));
        });

        // Create mechanics
        controller.createPassenger("central", "central2", "m1", true);
        controller.createPassenger("central", "central2", "m2", true);

        // Check station before boarding
        stationInfo = controller.getStationInfo("central");
        for (LoadInfoResponse load : stationInfo.getLoads()) {
            System.out.println("  - " + load.getLoadId() + " (" + load.getType() + ")");
        }

        // Board mechanics
        controller.simulate(1);

        // Check train after boarding
        TrainInfoResponse trainInfo = controller.getTrainInfo("repair1");

        assertEquals(2, trainInfo.getLoads().size(), "Train should have 2 mechanics");

        // === PART 3: Test repair boost ===
        // Move repair train on broken track
        controller.simulate(1);

        track = controller.getTrackInfo("test_track");

        // With 2 mechanics: repair rate = 1 + (2*2) = 5
        assertEquals(5, track.getDurability(), "Track should repair by 5");
        assertEquals(TrackType.UNBROKEN, track.getType());
    }

    @Test
    public void testRepairTrainDoesNotDamageTrack() {
        TrainsController controller = new TrainsController();

        // Create stations and track
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "CentralStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", true);

        // Get initial durability
        int initialDurability = controller.getTrackInfo("t1-2").getDurability();
        assertEquals(10, initialDurability);

        // Create repair train (no mechanics needed)
        assertDoesNotThrow(() -> {
            controller.createTrain("repair1", "RepairTrain", "s1", List.of("s1", "s2"));
        });

        // Run repair train back and forth multiple times
        // Each trip should NOT decrease durability
        for (int trip = 1; trip <= 3; trip++) {
            // Travel to s2
            controller.simulate(5); // Repair train speed = 2.0 km/min, needs 5 ticks

            TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");
            assertEquals(initialDurability, trackInfo.getDurability(),
                    "Track durability should not change after trip " + trip + " to s2");
            assertEquals(TrackType.UNBROKEN, trackInfo.getType());

            // Travel back to s1
            controller.simulate(5);

            trackInfo = controller.getTrackInfo("t1-2");
            assertEquals(initialDurability, trackInfo.getDurability(),
                    "Track durability should not change after trip " + trip + " back to s1");
            assertEquals(TrackType.UNBROKEN, trackInfo.getType());
        }

        // Verify train positions
        assertEquals("s1", controller.getTrainInfo("repair1").getLocation());
    }

    @Test
    public void testNormalPassengersOnRepairTrain() {
        TrainsController controller = new TrainsController();

        // Create passenger stations
        controller.createStation("s1", "PassengerStation", 0, 0);
        controller.createStation("s2", "PassengerStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", false);

        // Create repair train
        assertDoesNotThrow(() -> {
            controller.createTrain("repair1", "RepairTrain", "s1", List.of("s1", "s2"));
        });

        // Create passengers
        controller.createPassenger("s1", "s2", "p1", false);
        controller.createPassenger("s1", "s2", "p2", false);
        controller.createPassenger("s1", "s2", "m1", true);
        controller.createPassenger("s1", "s2", "m2", true);

        // Board passengers
        controller.simulate(1);

        // Verify boarding
        TrainInfoResponse trainInfo = controller.getTrainInfo("repair1");
        assertEquals(4, trainInfo.getLoads().size(), "Train should have 4 passengers");

        // Travel to s2 - simulate until arrival
        controller.simulate(3); // Move to position 8.0

        trainInfo = controller.getTrainInfo("repair1");
        assertNotEquals("s2", trainInfo.getLocation(), "Should not be at s2 yet");

        controller.simulate(1); // This tick causes arrival at s2

        // VERIFY IMMEDIATELY AFTER ARRIVAL
        trainInfo = controller.getTrainInfo("repair1");
        assertEquals("s2", trainInfo.getLocation(), "Train should be at s2");
        assertEquals(0, trainInfo.getLoads().size(), "Train should be empty");

        // Check station s2 has no passengers (they're removed, not stored)
        StationInfoResponse s2Info = controller.getStationInfo("s2");
        assertEquals(0, s2Info.getLoads().size(), "Station s2 should have no passengers");

        // Return to s1 - empty train needs 5 ticks (10km / 2.0 km/min = 5 ticks)
        controller.simulate(5);

        trainInfo = controller.getTrainInfo("repair1");
        assertEquals("s1", trainInfo.getLocation(), "Train should be back at s1");
    }

    @Test
    public void testAutoRepairOfBrokenTrack() {
        TrainsController controller = new TrainsController();

        controller.createStation("s1", "CargoStation", 0, 0);
        controller.createStation("s2", "CargoStation", 10, 0);
        controller.createTrack("t1-2", "s1", "s2", true);

        // Create first cargo
        controller.createCargo("s1", "s2", "heavy1", 5000);

        assertDoesNotThrow(() -> {
            controller.createTrain("breaker", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        controller.simulate(7);
        TrackInfoResponse track = controller.getTrackInfo("t1-2");

        // Create new cargo for second trip
        controller.createCargo("s1", "s2", "heavy2", 5000);

        controller.simulate(4);
        track = controller.getTrackInfo("t1-2");

        controller.simulate(7);
        track = controller.getTrackInfo("t1-2");

        assertEquals(0, track.getDurability(), "Track should be broken");
        assertEquals(TrackType.BROKEN, track.getType());
    }

    @Test
    public void testMultipleMechanicsStack() {
        TrainsController controller = new TrainsController();

        // Create CentralStation
        controller.createStation("central", "CentralStation", 0, 0);
        controller.createStation("central2", "CentralStation", 10, 0);
        controller.createTrack("test_track", "central", "central2", true);

        // === TEST WITH 1 MECHANIC ===
        breakTrack(controller);

        // Create repair train with 1 mechanic
        assertDoesNotThrow(() -> {
            controller.createTrain("repair1", "RepairTrain", "central",
                    List.of("central", "central2"));
        });
        controller.createPassenger("central", "central2", "m1", true);

        controller.simulate(1); // Board
        controller.simulate(1); // Move 1 tick

        TrackInfoResponse trackInfo = controller.getTrackInfo("test_track");
        assertEquals(3, trackInfo.getDurability(), "With 1 mechanic: should repair to 3");

        // === TEST WITH 2 MECHANICS ===
        breakTrack(controller);

        // Create repair train with 2 mechanics
        assertDoesNotThrow(() -> {
            controller.createTrain("repair2", "RepairTrain", "central",
                    List.of("central", "central2"));
        });
        controller.createPassenger("central", "central2", "m2", true);
        controller.createPassenger("central", "central2", "m3", true);

        controller.simulate(1); // Board
        controller.simulate(1); // Move 1 tick

        trackInfo = controller.getTrackInfo("test_track");
        assertEquals(5, trackInfo.getDurability(), "With 2 mechanics: should repair to 5");

        // === TEST WITH 3 MECHANICS ===
        breakTrack(controller);

        // Create repair train with 3 mechanics
        assertDoesNotThrow(() -> {
            controller.createTrain("repair3", "RepairTrain", "central",
                    List.of("central", "central2"));
        });
        controller.createPassenger("central", "central2", "m4", true);
        controller.createPassenger("central", "central2", "m5", true);
        controller.createPassenger("central", "central2", "m6", true);

        controller.simulate(1); // Board
        controller.simulate(1); // Move 1 tick

        trackInfo = controller.getTrackInfo("test_track");
        assertEquals(7, trackInfo.getDurability(), "With 3 mechanics: should repair to 7");
    }

    /**
     * Helper to guarantee a track is broken
     * @param controller
     */
    private void breakTrack(TrainsController controller) {
        TrackInfoResponse track = controller.getTrackInfo("test_track");
        int currentDurability = track.getDurability();

        System.out.println("Current durability before breaking: " + currentDurability);

        // Keep breaking until durability is 0
        while (controller.getTrackInfo("test_track").getDurability() > 0) {
            String uniqueId = "breaker" + System.currentTimeMillis() + Math.random();

            // Create enough cargo to break the track
            controller.createCargo("central", "central2", uniqueId + "_c1", 5000);

            assertDoesNotThrow(() -> {
                controller.createTrain(uniqueId, "CargoTrain", "central",
                        List.of("central", "central2"));
            });

            // First trip
            controller.simulate(7);

            // Check if already broken
            if (controller.getTrackInfo("test_track").getDurability() <= 0)
                break;

            // Create second cargo if needed
            controller.createCargo("central", "central2", uniqueId + "_c2", 5000);

            // Return empty
            controller.simulate(4);

            // Check if already broken
            if (controller.getTrackInfo("test_track").getDurability() <= 0)
                break;

            // Second trip
            controller.simulate(7);
        }

        track = controller.getTrackInfo("test_track");
        System.out.println("Durability after breaking: " + track.getDurability());
        assertEquals(0, track.getDurability(), "Track should be broken");
        assertEquals(TrackType.BROKEN, track.getType());
    }
}
