package trains;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
//import java.util.concurrent.TimeUnit;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Timeout;

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
        tests.testNormalTrainStuckOnBrokenTrack();
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

        System.out.println("\n=== FIRST TRIP (5 ticks) ===");
        controller.simulate(5);

        TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");
        System.out.println("After first trip, durability: " + trackInfo.getDurability());
        assertEquals(2, trackInfo.getDurability(), "After first trip: should be 2");

        System.out.println("\n=== RETURN TRIP (4 ticks - stop BEFORE auto-repair) ===");
        // Return trip: simulate exactly enough ticks to reach s1 but not start next journey
        // Empty speed = 3.0 km/min, needs 10/3.0 = 3.33 ticks → 4 ticks ensures arrival
        controller.simulate(4);

        trackInfo = controller.getTrackInfo("t1-2");
        System.out.println("After return trip (before repair), durability: " + trackInfo.getDurability());
        assertEquals(0, trackInfo.getDurability(), "After return trip: should be 0");
        assertEquals(TrackType.BROKEN, trackInfo.getType(), "Track should be BROKEN");

        System.out.println("\n=== REPAIR TICK ===");
        // Next tick: auto-repair happens
        controller.simulate(1);

        trackInfo = controller.getTrackInfo("t1-2");
        System.out.println("After repair tick, durability: " + trackInfo.getDurability());
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

        // First trip - with 5000kg, speed = 1.5 km/min
        System.out.println("\n=== FIRST TRIP (7 ticks) ===");
        for (int tick = 1; tick <= 7; tick++) {
            controller.simulate(1);
            TrainInfoResponse train = controller.getTrainInfo("train1");
            TrackInfoResponse track = controller.getTrackInfo("t1-2");
            System.out.println("Tick " + tick + ":");
            System.out.println("  Train1 position: " + train.getPosition());
            System.out.println("  Train1 location: " + train.getLocation());
            System.out.println("  Train1 load: " + train.getLoads().size());
            System.out.println("  Track durability: " + track.getDurability());
        }

        TrackInfoResponse trackInfo = controller.getTrackInfo("t1-2");
        System.out.println("\nAfter first trip, durability: " + trackInfo.getDurability());
        assertEquals(4, trackInfo.getDurability(), "Track should be at 4 after first trip");
        assertEquals(TrackType.UNBROKEN, trackInfo.getType());
        assertEquals("s2", controller.getTrainInfo("train1").getLocation());

        // Create second train
        controller.createCargo("s1", "s2", "c2", 4000);
        assertDoesNotThrow(() -> {
            controller.createTrain("train2", "CargoTrain", "s1", List.of("s1", "s2"));
        });

        System.out.println("\n=== SECOND PHASE (5 ticks) ===");
        for (int tick = 1; tick <= 5; tick++) {
            controller.simulate(1);
            TrainInfoResponse train1 = controller.getTrainInfo("train1");
            TrainInfoResponse train2 = controller.getTrainInfo("train2");
            TrackInfoResponse track = controller.getTrackInfo("t1-2");

            System.out.println("Tick " + tick + ":");
            System.out.println("  Train1 location: " + train1.getLocation() + ", pos: " + train1.getPosition());
            System.out.println("  Train2 location: " + train2.getLocation() + ", pos: " + train2.getPosition());
            System.out.println("  Train2 load: " + train2.getLoads().size());
            System.out.println("  Track durability: " + track.getDurability());
        }

        trackInfo = controller.getTrackInfo("t1-2");
        System.out.println("\nAfter 5 ticks, durability: " + trackInfo.getDurability());
        assertEquals(3, trackInfo.getDurability(), "After return + departure");

        // Continue with rest of test...
    }

    @Test
    public void testRepairTrainCanMoveOnBrokenTrack() {

    }

    @Test
    public void testMechanicsBoostTrackRepair() {

    }

    @Test
    public void testRepairTrainDoesNotDamageTrack() {

    }

    @Test
    public void testNormalPassengersOnRepairTrain() {

    }

    @Test
    public void testAutoRepairOfBrokenTrack() {

    }

    @Test
    public void testMultipleMechanicsStack() {

    }
}
