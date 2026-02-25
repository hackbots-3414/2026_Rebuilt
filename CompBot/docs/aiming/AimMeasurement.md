# AimMeasurement

## Overview

`AimMeasurement` is an immutable record that captures a single experimentally obtained data point mapping a shot distance to its required shooter parameters. A list of these is used to build the interpolation tables inside `ToFAim`.

---

## Record Fields

| Field | Type | Description |
|---|---|---|
| `distance` | `Distance` | Distance from the shooter to the target at which this measurement was taken |
| `pitch` | `Rotation2d` | Hood angle that produced a successful shot at this distance |
| `shooterControl` | `double` | Shooter mechanism control value (speed/voltage) that produced a successful shot |
| `time` | `Time` | Measured time of flight for a shot at this distance |

---

## Usage

`AimMeasurement` objects are created at configuration time (typically in a constants file or `RobotContainer`) and passed as a `List<AimMeasurement>` to the `ToFAim` constructor. `ToFAim` then inserts each measurement into three separate `InterpolatingDoubleTreeMap` tables keyed by distance, enabling smooth interpolation between measured data points at runtime.
