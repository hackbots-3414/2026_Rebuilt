# Auto Intake

## Subsystems
* Drivetrain
* Intake

## Initial State
* Vision detects fuel nearby
* Human-triggered

## Steps
1. Find fuel using vision
1. drive to fuel with `drivetrain.driveVelocity()` with calculated velocity (some speed towards fuel).
1. `intake.intake()` in parallel
