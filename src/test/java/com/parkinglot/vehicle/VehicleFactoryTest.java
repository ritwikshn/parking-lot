package com.parkinglot.vehicle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleFactoryTest {

    public class Jeep extends Vehicle {
        public static final String VEHICLE_TYPE = "jeep";

        Jeep() {
            super(VEHICLE_TYPE);
        }

        @Override
        public String toString(){
            return "this is a jeep for testing purpose";
        }

        @Override
        public void exit() {
                System.out.println("Jeep is now exiting.....");
        }
    }

    class Cycle extends Vehicle{
        Cycle() {
            super(VEHICLE_TYPE);
        }
        public static final String VEHICLE_TYPE = "cycle";
        @Override
        public void exit(){
            System.out.println("Cycle on its way out...");
        }
    }


    @Test
    void VehicleCreationTest(){
        Car car = (Car) VehicleFactory.createVehicle(VehicleFactory.VEHICLE_TYPE_CAR, "RJ01CB8193");
        assertEquals(car.getClass(), Car.class);
        assertNotNull(car);
    }
    @Test
    void newVehicleTypeCreationBeforeRegistrationTest(){
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> VehicleFactory.createVehicle(Cycle.VEHICLE_TYPE, "Cycle124"));
        assertEquals("Unsupported vehicle type", e.getMessage());
    }

    @Test
    void newVehicleTypeCreationAfterRegistrationTest(){
        VehicleFactory.registerVehicle(Jeep.VEHICLE_TYPE, () -> { return new Jeep(); });
        Jeep jeep = (Jeep) VehicleFactory.createVehicle(Jeep.VEHICLE_TYPE, "Jeep123");
        assertNotNull(jeep);
        assertEquals("this is a jeep for testing purpose", jeep.toString());
    }
}