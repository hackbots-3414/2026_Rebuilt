# Auto Corral

## Subsystems
* Intake
* Drivetrain
* Indexer

## Initial State
* Have fuel
* Close to corralStation
* Triggered by human button

## Steps
1. `drivetrain.driveToPose(corralStation)`
1. `intake.reverse()` and `indexer.reverse()`