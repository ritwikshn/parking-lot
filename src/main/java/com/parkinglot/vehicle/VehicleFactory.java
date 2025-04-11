package com.parkinglot.vehicle;

import java.util.HashMap;
import java.util.function.Supplier;

public class VehicleFactory {
    public static final String VEHICLE_TYPE_CAR = Car.VEHICLE_TYPE;
    public static final String VEHICLE_TYPE_VAN = Van.VEHICLE_TYPE;
    public static final String VEHICLE_TYPE_MOTORCYCLE = MotorCycle.VEHICLE_TYPE;
    public static final String VEHICLE_TYPE_TRUCK = Truck.VEHICLE_TYPE;

    static HashMap<String, Supplier<Vehicle>> vehicleTypeRegistry = new HashMap<>();
    static{
        registerVehicle(VEHICLE_TYPE_MOTORCYCLE, MotorCycle::new);
        registerVehicle(VEHICLE_TYPE_CAR, Car::new);
        registerVehicle(VEHICLE_TYPE_VAN, Van::new);
        registerVehicle(VEHICLE_TYPE_TRUCK, Truck::new);
    }
    public static void registerVehicle(String vehicleType, Supplier<Vehicle> constructor){
        if(vehicleType == null){
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        if(constructor == null){
            throw new IllegalArgumentException("Constructor cannot be null");
        }
        vehicleTypeRegistry.put(vehicleType, constructor);
    }

    public static Vehicle createVehicle(String vehicleType, String vehicleLicenseNumber){
        if(vehicleType == null){
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }

        Supplier<Vehicle> constructor = vehicleTypeRegistry.get(vehicleType);
        if(constructor == null){
            throw new IllegalArgumentException("Unsupported vehicle type");
        }
        Vehicle newVehicle  = constructor.get();
        newVehicle.setLicenseNumber(vehicleLicenseNumber);
        return newVehicle;
    }
}
