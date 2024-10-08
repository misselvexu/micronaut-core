package io.micronaut.docs.ioc.injection.optional;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@MicronautTest(startApplication = false)
@Disabled("optional injection is not supported")
public class OptionalAutowiredTest {
    @Test
    void testVehicle(Vehicle vehicle) {
        Assertions.assertEquals(6, vehicle.getEngine().cylinders());
    }
}
