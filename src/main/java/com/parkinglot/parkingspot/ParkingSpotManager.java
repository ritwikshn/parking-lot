package com.parkinglot.parkingspot;


import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ParkingSpotManager {
    private final List<ParkingSpotObserver> spotObservers = new ArrayList<>();
    final static Map<Class<? extends ParkingSpot>, Integer> DEFAULT_INITIAL_PARKING_SPOT_COUNT = Map.of(
            Motorcycle.class, 3,
            Compact.class, 3,
            Handicapped.class, 3,
            Large.class, 3
    );
    final AtomicInteger parkingSpotCounter = new AtomicInteger(0);
    private final Map<Class<? extends ParkingSpot>, Set<ParkingSpot>> allParkingSpots = new HashMap<>();
    private final Map<Class<? extends ParkingSpot>, ConcurrentLinkedQueue<ParkingSpot>> vacantSpots = new HashMap<>();

    private static volatile ParkingSpotManager spotManager;
    private ParkingSpotManager(Map<Class<? extends ParkingSpot>, Integer> minInitialParkingSpotCount){
        for(Class<? extends ParkingSpot> spotType: minInitialParkingSpotCount.keySet()){
            resizeSpotCountToAMin(spotType, minInitialParkingSpotCount.get(spotType));
        }
    }
    private ParkingSpotManager(){
        this(DEFAULT_INITIAL_PARKING_SPOT_COUNT);
    }
    public void resizeSpotCountToAMin(Class<? extends ParkingSpot> spotType, int minParkingSpotCount){
        int desiredCount = minParkingSpotCount;
        int currentCount = getTotalSpotCountOfType(spotType);
        if(desiredCount > currentCount) {
            synchronized (this) {
                currentCount = getTotalSpotCountOfType(spotType);
                //possible optimization --> provide method for batch creation of parking spots
                while (desiredCount > currentCount) {
                    createNewSpotOfType(spotType);
                    currentCount = getTotalSpotCountOfType(spotType);
                }
            }
        }
    }
    private int getNextSpotId(){
        return parkingSpotCounter.incrementAndGet();
    }

    ParkingSpot createNewSpotOfType(Class<? extends ParkingSpot> spotType){
        int spotId = getNextSpotId();
        ParkingSpot newSpot = null;
        if(spotType == Motorcycle.class)
            newSpot =  new Motorcycle(spotId, "M" + spotId);
        else if(spotType == Compact.class)
            newSpot =  new Compact(spotId, "C" + spotId);
        else if(spotType == Handicapped.class)
            newSpot = new Handicapped(spotId, "H" + spotId);
        else if(spotType == Large.class)
            newSpot = new Large(spotId, "L" + spotId);
        else{
            throw new IllegalArgumentException("Unknown spot type");
        }
        if(!allParkingSpots.containsKey(spotType)) allParkingSpots.put(spotType, new HashSet<>());
        allParkingSpots.get(spotType).add(newSpot);

        if(!vacantSpots.containsKey(spotType)) vacantSpots.put(spotType, new ConcurrentLinkedQueue<>());
        vacantSpots.get(spotType).offer(newSpot);

        notifyObservers();
        return newSpot;
    }

    public ParkingSpot occupyAndGetEmptySpotOfType(Class<? extends ParkingSpot> spotType){
        if(!vacantSpots.containsKey(spotType)) return null;
        ParkingSpot emptySpot = vacantSpots.get(spotType).poll();
        if(emptySpot == null){
            return null;
        }
        emptySpot.occupySpot();

        notifyObservers();
        return emptySpot;
    }
    private boolean isARegisteredSpot(ParkingSpot spot){
        if(spot == null)
            return false;
        Set<ParkingSpot> set =  allParkingSpots.get(spot.getClass());
        if(set == null)
            return false;
        return set.contains(spot);
    }

    public boolean releaseSpot(ParkingSpot spot){
        if(spot == null)
            throw new IllegalArgumentException("Spot cannot be null");
        //validate that the spot is one of the registered spots
        if(!isARegisteredSpot(spot)) {
            System.out.println("not a registered spot");
            throw new IllegalStateException("Spot is not a registered parking spot");
        }
        //validate that the spot is not already vacated
        //if so just return true immediately to prevent duplicate insertion in queue
        if(spot.isVacant())
            return true;

        spot.vacateSpot();
        vacantSpots.get(spot.getClass()).add(spot);
        return true;
    }

    public static ParkingSpotManager getInstance(){
        if(spotManager == null){
            synchronized (ParkingSpotManager.class){
                if(spotManager == null){
                    spotManager = new ParkingSpotManager();
                }
            }
        }
        return spotManager;
    }

    public static ParkingSpotManager getInstance(Map<Class<? extends ParkingSpot>, Integer>  minInitialParkingSpotCount){
        if(spotManager == null){
            synchronized (ParkingSpotManager.class){
                if(spotManager == null){
                    spotManager = new ParkingSpotManager(minInitialParkingSpotCount);
                }
            }
        }
        return spotManager;
    }

    public int getTotalSpotCountOfType(Class<? extends ParkingSpot> spotType){
        if(!allParkingSpots.containsKey(spotType)) return 0;
        return allParkingSpots.get(spotType).size();
    }
    public int getVacantSpotCountOfType(Class<? extends ParkingSpot> spotType){
        if(!vacantSpots.containsKey(spotType)) return 0;
        return vacantSpots.get(spotType).size();
    }
    public boolean hasVacantSpotOfType(Class<? extends ParkingSpot> spotType){
        return !vacantSpots.get(spotType).isEmpty();
    }
    public void addSpotObserver(ParkingSpotObserver observer){
        spotObservers.add(observer);
        notifyObserver(observer);
    }

    private void notifyObserver(ParkingSpotObserver observer){
        observer.update(vacantSpots.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> entry.getValue().size())));
    }
    CompletableFuture<Void> notifyObservers(){
        //should be triggered whenever a new spot is added,
        //a spot is occupied or vacated

        //runs in the background so that calling method do not stall
        return CompletableFuture.runAsync(() -> {
            for(ParkingSpotObserver  observer : spotObservers){
                notifyObserver(observer);
            }
        });
    }

}
