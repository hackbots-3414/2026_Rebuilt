import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;



public class Robot {
    public enum Zone {
        Blue, Red, Neutral
    }
    public enum Direction {
        North, East, South, West
    }
    public ArrayList<Fuel> Hopper = new ArrayList<Fuel>();
    public int currentFuel = 0;
    public Zone currentZone;
    public int[] currentPosition = {0, 0};
    public int pickupSpeed = 4; // fuel per unit of time
    public int shootSpeed = 2; //fuel per unit of time
    public int indexSpeed = 4;
    //going to represent field as a grid of squares and save current position as (x, y) pair
    
     public Robot() {

    }
    public void pickupFuel() {
        if (!canPickupFuel()) {
            System.out.println("Failed to pick up fuel, not in range");
            return;
        }
        Hopper.add(new Fuel(Fuel.Status.Stored));
        System.out.println("Fuel collected, number: " + Hopper.size());
    }
    public void indexFuel() {
        boolean fuelIndexed = false;
        if (Hopper.isEmpty()) {
            System.out.println("Failex to index fuel, hopper is empty");
            return;
        }
        if (!hasIndexableFuel()) {
            System.out.println("Failed to index fuel, no fuel is available to index");
        }
        getIndexableFuel().status = Fuel.Status.Readied;
        System.out.println("Sucessfully Readied Fuel");
    }
    public void shootFuel() {
        if (Hopper.isEmpty()) {
            System.out.println("Failed to shoot fuel, hopper is empty");
            return;
        } 
        if (!hasReadiedFuel()) {    
            System.out.println("Failed to shoot fuel, no fuel is available to shoot");
            return;
        }
        if (!inShootingRange()) {
            System.out.println("Failed to shoot fuel, not in range");
            return;
        }
        System.out.println("Shooting fuel");
        Hopper.remove(getReadiedFuel());
    
    }
    public int getCurrentFuel() {
        return Hopper.size();
    }

    public void move(Direction direction) {
        switch (direction) {
            case North:
            currentPosition[1] += 1;
            break;
            case East:
            currentPosition[0] += 1;
            break;
            case South:
            currentPosition[1] -= 1;
            break;
            case West:
            currentPosition[0] -= 1;
            break;
        }
        System.out.println("moved " + direction + ", current position is " + currentPosition[0] + ", " + currentPosition[1]);
    } 
    public boolean canPickupFuel() {
        return (currentPosition[0] < 6 && currentPosition[0] > 3);
    }
    public boolean inShootingRange() {
        return currentPosition[0] < 3;
    }
    public boolean hasReadiedFuel() {
        for (Fuel fuel : Hopper) {
            if (fuel.status == Fuel.Status.Readied) {
                return true;
            }
        }
        return false;
    }
    public boolean hasIndexableFuel() {
            for (Fuel fuel : Hopper) {
            if (fuel.status == Fuel.Status.Stored) {
                return true;
            }
        }
        return false;
    }
    public Fuel getReadiedFuel() {
        for (Fuel fuel : Hopper) {
            if (fuel.status == Fuel.Status.Readied) {
                return fuel;
            }
        }
        System.out.println("ERROR: attempted to retrieve readied fuel but found none.");
        return new Fuel(Fuel.Status.Stored);
    }
    public Fuel getIndexableFuel() {
        for (Fuel fuel : Hopper) {
            if (fuel.status == Fuel.Status.Stored) {
                return fuel;
            }
        }
        System.out.println("ERROR: attempted to retrieve indexable fuel but found none.");
        return new Fuel(Fuel.Status.Stored);
    }
    public void calculateNextMove() {

    }
}
