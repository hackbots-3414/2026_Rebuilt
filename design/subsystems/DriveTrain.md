# Drivetrain 
## Assumptions

* swerve drive
* 8 TalonFX motors
* 4 absolute encoders for steer
* accurate pose estimation
* gyro for reading the azimuth of robot
* Accurate drive-to-pose; within some reasonable tolerance

## Operations

`drive(velocityRef)`

* Sets PID reference for swerve drive with a given velocity reference


`driveToPoint(coordiates)`
* Autopilot drive to specific coordinates