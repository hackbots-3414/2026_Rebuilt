# Drivetrain 
## Assumptions

* swerve drive
* 8 motors
* 4 motors
* accurate pose position
* gyro for reading the azimuth of robot
* error tolerence for position

## Operations


`drive(velocityRef)`

* Sets PID reference for swerve drive with a given velocity reference


`driveToPoint(coordiates)`
* Autopilot drive to specific coordinates