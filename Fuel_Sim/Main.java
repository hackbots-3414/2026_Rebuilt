

public class Main {
  public int score = 0;
  public int maxSpeed = 100;
  public int counter = 0;
  public static void main(String[] args) {
    Robot robot = new Robot();
     Main main = new Main();
     while (robot.currentPosition[0] <= 3) {
      robot.move(Robot.Direction.East);
     }
     main.tick(robot);
     while (robot.currentPosition[0] <= 3) {
      robot.move(Robot.Direction.East);
     }
     main.tick(robot);

  }
  public void tick(Robot robot) {
    counter = 0;
     while (counter < robot.pickupSpeed) {
      robot.pickupFuel();
      counter++;
     }
     counter = 0;
     while (counter < robot.indexSpeed) {
      robot.indexFuel();
      counter++;
     }
     while (robot.currentPosition[0] >= 3) {
      robot.move(Robot.Direction.West);
     }
     counter = 0;
     while (counter < robot.shootSpeed) {
      robot.shootFuel();
      counter++;
     }
    System.out.println("Robot currently has "  + robot.getCurrentFuel() + " fuel");
  }
  
        
  }
       
  

