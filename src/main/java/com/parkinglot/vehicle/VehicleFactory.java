package com.parkinglot.vehicle;

import java.util.HashMap;
import java.util.function.Supplier;

public class VehicleFactory {
    public static final String VEHICLE_TYPE_CAR = "car";
    public static final String VEHICLE_TYPE_VAN = "van";
    public static final String VEHICLE_TYPE_MOTORCYCLE = "motorcycle";
    public static final String VEHICLE_TYPE_TRUCK = "truck";

    static HashMap<Class<? extends Vehicle>, Supplier<Vehicle>> vehicleTypeRegistry = new HashMap<>();
    static{
        registerVehicle(MotorCycle.class, MotorCycle::new);
        registerVehicle(Car.class, Car::new);
        registerVehicle(Van.class, Van::new);
        registerVehicle(Truck.class, Truck::new);
    }
    public static void registerVehicle(Class<? extends Vehicle> vehicleType, Supplier<Vehicle> constructor){
        if(vehicleType == null){
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        if(constructor == null){
            throw new IllegalArgumentException("Constructor cannot be null");
        }
        vehicleTypeRegistry.put(vehicleType, constructor);
    }
    public static Vehicle createVehicle(Class<? extends Vehicle> vehicleType, String licenseNumber){
        if(vehicleType == null){
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }

        Supplier<Vehicle> constructor = vehicleTypeRegistry.get(vehicleType);
        if(constructor == null){
            throw new IllegalArgumentException("Unsupported vehicle type");
        }
        Vehicle newVehicle  = constructor.get();
        newVehicle.setLicenseNumber(licenseNumber);
        return newVehicle;
    }
}
