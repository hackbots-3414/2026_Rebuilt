# Intake

## Assumptions
* Picking the ball
* Runs at a non bottlenecking speed; fast
* Trackable change in current
* Not passive
* We can read motor velocity
* Ability to detect gamepieces in the mechanism
* Can eject pieces that have fully passed through the subsystem

## Operations
`intakeFuel`
* Picking up fuel into hopper

`reverse`
* Ejecting fuel from intake/hopper
  * For filling the corral
  * Incase the intake gets jammed

`detectJam`
* Tells us whether all of the following conditions are met:
  * Current is higher than expected
  * Velocity is lower than expected
  * Gamepieces are found in the mechanism