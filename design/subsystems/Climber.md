# Climb

## Assumptions

* Support the robots weight
* Climbing all three levels
* Capability to disengage from L1

## Operations

`prepare(level)`
* check that we're able to climb to this level
* prepares the climber to climb

`climb(level)`
* Climbing to a specific level of the tower
* Checks to ensure that we're allowed to climb to the specified level given the current game state

`disengage()`
* disengages from the first level