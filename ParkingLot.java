// Question: Parking Lot
// Requirements:
// 1. Contains n levels, m rows each level, k spots every row
// 2. Vehicle type contains Motorcycle, normal car, bus
// 3. Spot type contains Motorcycle, compact and large
// 4. For each row, number range [0, k / 4) belongs to motorcycle, range [k / 4, k / 4 * 3)
//    belongs to compact, range [k / 4 * 3, k) belongs to large
// 5. motorcycle can park in any spot
// 6. normal car can park in compact or large
// 7. bus can only park in large spot

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
  List<Level> levels;
  int numOfLevels;

  public ParkingLot(int numOfLevels, int rowsPerLevel, int spotsPerRow) {
    this.numOfLevels = numOfLevels;
    levels = new ArrayList<>();
    for (int i = 0; i < numOfLevels; i++) {
      levels.add(new Level(i, rowsPerLevel, spotsPerRow));
    }
  }

  public boolean park(Vehicle vehicle) {
    for (int i = 0; i < numOfLevels; i++) {
      if (levels.get(i).park(vehicle)) {
        return true;
      }
    }
    return false;
  }

  public void remove(Vehicle vehicle) {
    vehicle.spot.remove();
    vehicle.clearSpot();
  }
}

enum VehicleSize {
  MOTORCYCLE,
  COMPACT,
  LARGE
}

// an abstract class to present all types of vehicles
abstract class Vehicle {
  protected VehicleSize size;
  protected String plateNumber;
  protected ParkingSpot spot;

  public void parkInSpot(ParkingSpot spot) {
    this.spot = spot;
  }

  public void clearSpot() {
    this.spot = null;
  }

  public abstract boolean canFitInSpot(ParkingSpot spot);
}

class MotorCycle extends Vehicle {
  public MotorCycle(String plateNum) {
    plateNumber = plateNum;
    size = VehicleSize.MOTORCYCLE;
  }

  @Override
  public boolean canFitInSpot(ParkingSpot spot) {
    return true;
  }
}

class Car extends Vehicle {
  public Car(String plateNum) {
    plateNumber = plateNum;
    size = VehicleSize.COMPACT;
  }

  @Override
  public boolean canFitInSpot(ParkingSpot spot) {
    return spot.size == VehicleSize.LARGE || spot.size == VehicleSize.COMPACT;
  }
}

class Bus extends Vehicle {
  public Bus(String plateNum) {
    plateNumber = plateNum;
    size = VehicleSize.LARGE;
  }

  @Override
  public boolean canFitInSpot(ParkingSpot spot) {
    return spot.size == VehicleSize.LARGE;
  }
}

class Level {
  int floor;
  List<ParkingSpot> spots;
  int freeSpots;
  int rowsPerLevel;
  int spotsPerRow;

  public Level(int floor, int rowsPerLevel, int spotsPerRow) {
    this.floor = floor;
    this.rowsPerLevel = rowsPerLevel;
    this.spotsPerRow = spotsPerRow;
    this.freeSpots = rowsPerLevel * spotsPerRow;
    this.spots = new ArrayList<>();
    for (int i = 0; i < rowsPerLevel; i++) {
      for (int j = 0; j < spotsPerRow; j++) {
        if (j < spotsPerRow / 4) {
          spots.add(new ParkingSpot(this, i, j, VehicleSize.MOTORCYCLE));
        } else if (j < spotsPerRow / 4 * 3) {
          spots.add(new ParkingSpot(this, i, j, VehicleSize.COMPACT));
        } else {
          spots.add(new ParkingSpot(this, i, j, VehicleSize.LARGE));
        }
      }
    }
  }

  public boolean park(Vehicle vehicle) {
    if (getFreeSpots() == 0) {
      return false;
    }

    for (int i = 0; i < rowsPerLevel; i++) {
      for (int j = 0; j < spotsPerRow; j++) {
        ParkingSpot spot = spots.get(i * spotsPerRow + j);
        if (spot.isAvailable() && spot.canFitVehicle(vehicle)) {
          spot.park(vehicle);
          vehicle.parkInSpot(spot);
          freeSpots--;
          return true;
        }
      }
    }

    return false;
  }

  public int getFreeSpots() {
    return freeSpots;
  }
}

class ParkingSpot {
  Vehicle vehicle;
  VehicleSize size;
  Level level;
  int row;
  int spotNumber;

  public ParkingSpot(Level level, int row, int spotNumber, VehicleSize size) {
    this.level = level;
    this.row = row;
    this.spotNumber = spotNumber;
    this.size = size;
  }

  public boolean isAvailable() {
    return vehicle == null;
  }

  public boolean canFitVehicle(Vehicle vehicle) {
    return isAvailable() && vehicle.canFitInSpot(this);
  }

  public boolean park(Vehicle vehicle) {
    if (!canFitVehicle(vehicle)) {
      return false;
    }
    this.vehicle = vehicle;
    this.vehicle.parkInSpot(this);
    return true;
  }

  public void remove() {
    vehicle = null;
  }
}
