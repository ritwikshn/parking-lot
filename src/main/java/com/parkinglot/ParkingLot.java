package com.parkinglot;

import com.parkinglot.parkingrate.FlatParkingRate;
import com.parkinglot.parkingrate.ParkingRate;
import com.parkinglot.parkingspot.*;
import com.parkinglot.vehicle.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingLot {
    private final static String DEFAULT_ADDRESS = "3-SAVILE-ROW";
    private final static String DEFAULT_NAME = "Neighbourhood Parking Lot";
    private int id;
    private String name;
    private String address;
    private static volatile ParkingLot parkingLot;
    private AtomicInteger ticketCounter = new AtomicInteger(0);
    private ParkingSpotManager spotManager;
    private ParkingRate parkingRate;
    private Queue<Exit> exitGatesQueue = new ConcurrentLinkedQueue<>();
    private List<Entrance> entranceList= new ArrayList<>();

    private ParkingLot(){
        this((int) (1000*Math.random()), ParkingLot.DEFAULT_NAME, ParkingLot.DEFAULT_ADDRESS);
    }
    private ParkingLot(int id, String name, String address){
        this(id, name, address, new FlatParkingRate());
    }
    private ParkingLot(int id, String name, String address, ParkingRate parkingRate){
        this.id = id;
        this.name = name;
        this.address = address;
        this.parkingRate = parkingRate;
    }

    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
    public String getAddress(){
        return address;
    }

    ParkingSpotManager getSpotManager() {
        if(this.spotManager == null){
            this.spotManager = ParkingSpotManager.getInstance();
        }
        return this.spotManager;
    }

    private int getNextTicketCounter(){
        return ticketCounter.incrementAndGet();
    }

    Class<? extends ParkingSpot> getParkingSpotTypeForVehicleType(Class<? extends Vehicle> vehicleType){
        if(vehicleType == MotorCycle.class){
            return Motorcycle.class;
        }else if(vehicleType == Car.class){
            return Compact.class;
        }else if(vehicleType == Truck.class){
            return Large.class;
        }else if(vehicleType == Van.class){
            return Handicapped.class;
        }else{
            throw new IllegalArgumentException("Unknown vehicle type");
        }
    }
    public Optional<ParkingTicket> getParkingTicket(Class<? extends Vehicle> vehicleType){
        ZonedDateTime entryTime = ZonedDateTime.now();
        return getParkingTicket(vehicleType, entryTime);
    }
    public Optional<ParkingTicket> getParkingTicket(Class<? extends Vehicle> vehicleType, ZonedDateTime entryTime) {
        //this may later be achieved using a parking strategy
        //that is the one to determine which type of vehicle
        //can use which type(s) of spot
        Class<? extends ParkingSpot> applicableParkingSpotType = getParkingSpotTypeForVehicleType(vehicleType);

        ParkingSpot designatedSpot = getSpotManager().occupyAndGetEmptySpotOfType(applicableParkingSpotType);
        if(designatedSpot == null){
            //logging required here
            return Optional.empty();
        }

        if(entryTime == null){
            entryTime = ZonedDateTime.now();
        }
        return Optional.of(new ParkingTicket(getNextTicketCounter(), entryTime, designatedSpot));
    }
    public boolean hasVacancyForVehicleType(Class<? extends Vehicle> vehicleType){
        Class<? extends ParkingSpot> applicableParkingSpot = getParkingSpotTypeForVehicleType(vehicleType);
        return getSpotManager().hasVacantSpotOfType(applicableParkingSpot);
    }
    public Entrance spawnNewEntrance(){
        Entrance newEntrance = new Entrance((int) (1000*Math.random()));
        entranceList.add(newEntrance);
        return newEntrance;
    }
    public Exit spawnNewExit(){
        Exit newExit = new Exit((int) (1000*Math.random()));
        exitGatesQueue.offer(newExit);
        return newExit;
    }
    public Exit getARegisteredExit(){
        //load-balancing among various exits
        Exit exit =  exitGatesQueue.poll();
        if(exit != null) exitGatesQueue.offer(exit);
        return exit;
    }
    public Entrance getARegisteredEntrance(){
        if(entranceList.size() == 0) return null;
        Entrance entryGate =  entranceList.get((int) (entranceList.size()*Math.random()));
        return entryGate;
    }
    public DisplayBoard spawnNewDisplayBoard(){
        DisplayBoard newBoard = new DisplayBoard((int) (1000*Math.random()));
        getSpotManager().addSpotObserver(newBoard);
        return newBoard;
    }
    public static ParkingLot getInstance(int id, String name, String address, ParkingRate parkingRate){
        if(parkingLot == null){
            synchronized (ParkingLot.class){
                if(parkingLot == null){
                    parkingLot = new ParkingLot(id, name, address, parkingRate);
                }
            }
        }
        if((parkingLot.name != null && !parkingLot.name.equals(name)) ||
                (parkingLot.address != null && !parkingLot.address.equals(address)) ||
                parkingLot.id!=id ||
                (parkingLot.parkingRate != null && !parkingLot.parkingRate.equals(parkingRate))){
            throw new IllegalStateException("Parking lot is already initialized with different properties");
        }
        return parkingLot;
    }

    public static ParkingLot getInstance(int id, String name, String address){
        if(parkingLot == null){
            synchronized (ParkingLot.class){
                if(parkingLot == null){
                    parkingLot = new ParkingLot(id, name, address);
                }
            }
        }

        if((parkingLot.name != null && !parkingLot.name.equals(name)) ||
                (parkingLot.address != null && !parkingLot.address.equals(address)) ||
                parkingLot.id!=id){
            throw new IllegalStateException("Parking lot is already initialized with different properties");
        }

        return parkingLot;
    }

    public static ParkingLot getInstance(){
        if(parkingLot == null){
            synchronized (ParkingLot.class){
                if(parkingLot == null){
                    parkingLot = new ParkingLot();
                }
            }
        }
        return parkingLot;
    }

    void setParkingRate(ParkingRate parkingRate) {
        this.parkingRate = parkingRate;
    }
    public ParkingRate getParkingRate(){
        return parkingRate;
    }
    boolean releaseParkingSpot(ParkingSpot spot){
        return getSpotManager().releaseSpot(spot);
    }

}
