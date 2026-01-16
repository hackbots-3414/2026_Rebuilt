# Auto align and Climb

## Subsystems
* Drivetrain
* Climb

## Initial State
* Close to tower
* Not yet climbed
* Human triggers action
* Human selects climb spot (via button)

## Steps
1. Align climb with `drivetrain.driveToPoint(climbPose)`
1. Prepare climb with `climber.prepare()`
1. Climb with `climber.climb(desiredLevel)`

