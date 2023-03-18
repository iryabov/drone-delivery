package com.github.iryabov.droneservice.client;

import com.github.iryabov.droneservice.client.impl.StubDroneClient;
import com.github.iryabov.droneservice.entity.DroneModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.spel.spi.Function;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DroneEmulatorTest {
    public static final String STUB_DRONE = "123";
    private StubDroneClient client;

    @BeforeEach
    void setUp() {
        client = new StubDroneClient();
        client.add(STUB_DRONE, DroneModel.LIGHTWEIGHT);
    }

    @Test
    void simpleDelivery() {
        var drone = client.lookup(STUB_DRONE);
        assertThat(drone.getBatteryLevel(), is(100));
        assertThat(drone.isOnBase(), is(true));
        assertThat(drone.hasLoad(), is(false));

        drone.load(0.5);
        assertThat(drone.hasLoad(), is(true));
        waitUntil(() -> drone.getLoadingPercentage() == 100);
        assertThat(drone.getLoadingPercentage(), is(100));

        drone.flyTo(new DroneClient.Point(100, 100));
        waitUntil(drone::isReachedDestination);
        assertThat(drone.isReachedDestination(), is(true));

        drone.unload();
        assertThat(drone.hasLoad(), is(false));

        drone.returnToBase();
        waitUntil(drone::isOnBase);
        assertThat(drone.isOnBase(), is(true));
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
