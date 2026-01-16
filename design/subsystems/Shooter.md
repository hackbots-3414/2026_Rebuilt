# Shooter

## Assuptions

* Controllable pitch angle of fuel
* Controlling the speed of the flywheel
* Accurate control over gamepiece final location
* Read motor position/velocities to determine if shooting is ready.

## Operations

`shoot(configuration)`
* Ensure that we're at the correct shooting configuration
* shooting the fuel

`reverse()`
* Reverses the shooter to potentially unjam a fuel

`prepare(configuration)`
* changing the launch angle and speed of the motors
