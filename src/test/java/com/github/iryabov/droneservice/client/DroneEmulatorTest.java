package com.github.iryabov.droneservice.client;

import com.github.iryabov.droneservice.client.impl.StubDroneClient;
import com.github.iryabov.droneservice.entity.DroneModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DroneEmulatorTest {
    public static final String STUB_DRONE = "LIGHTWEIGHT-1";
    private StubDroneClient client;

    @BeforeEach
    void setUp() throws Exception {
        client = new StubDroneClient();
        client.afterPropertiesSet();
    }

    @Test
    void simpleDelivery() {
        var drone = client.lookup(STUB_DRONE, DroneModel.LIGHTWEIGHT);
        assertThat(drone.getBatteryLevel(), is(100));
        assertThat(drone.isOnBase(), is(true));
        assertThat(drone.hasLoad(), is(false));

        System.out.println("Loading 0.5 kg package...");
        drone.load(0.5);
        assertThat(drone.hasLoad(), is(true));
        waitUntil(() -> drone.getLoadingPercentage() == 100);
        assertThat(drone.getLoadingPercentage(), is(100));
        System.out.println("Loaded.");

        System.out.println("Flying to the delivery address...");
        drone.flyTo(new DroneClient.Point(100, 100));
        waitUntil(drone::isReachedDestination);
        assertThat(drone.isReachedDestination(), is(true));
        System.out.println("Arrived at destination.");

        System.out.println("Unloading...");
        drone.unload();
        waitUntil(() -> drone.getLoadingPercentage() == 0);
        assertThat(drone.hasLoad(), is(false));
        System.out.println("Package has delivered.");

        System.out.println("Returning to the base...");
        drone.flyToBase();
        waitUntil(drone::isOnBase);
        assertThat(drone.isOnBase(), is(true));
        System.out.println("Returned.");

        System.out.println("Waiting until charged...");
        waitUntil(() -> drone.getBatteryLevel() == 100);
        assertThat(drone.getBatteryLevel(), is(100));
        System.out.println("Charged.");
    }

    private static void waitUntil(Supplier<Boolean> condition) {
        while (!condition.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
