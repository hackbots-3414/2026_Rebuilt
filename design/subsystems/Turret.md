# Turret

## Assumptions

* Limited effective range of motion: not all angles are possible/usable.
* Know its angular position
* Some homing detection system

## Operations

`home()`
* Rotates the turret to assigned home location

`track(configuration)`
* Attempts to aim the turret at the correct angle, depending on `configuration`.

`calibrate()`
* Finds the real "zero" position and resets the encoder to that point.