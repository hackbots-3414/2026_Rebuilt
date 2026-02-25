# VisionTest

## Overview

`VisionTest` is a minimal diagnostic command used to verify vision/odometry behavior while the robot rotates in place. It implements `CommandBuilder`.

---

## `build(Subsystems, StateManager)` → `Command`

Delegates directly to `Drivetrain.rotate()`, which spins the robot robot-centrically at π/2 rad/s.

**Subsystems used:** `drivetrain`

---

## Purpose

Rotating the robot at a known constant rate lets developers visually confirm that vision pose estimates are arriving, that the field widget updates correctly, and that odometry stays consistent under rotation. No state or aim parameters are consumed.
