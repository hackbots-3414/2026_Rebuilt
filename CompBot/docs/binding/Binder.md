# Binder

## Overview

`Binder` is the interface that all controller/event binding classes implement. It defines a single method that wires triggers and commands to the robot's `Superstructure`, keeping binding logic separated from robot initialization.

---

## Interface

```java
public interface Binder {
    void bind(Superstructure superstructure);
}
```

**Parameters**

| Parameter | Type | Description |
|---|---|---|
| `superstructure` | `Superstructure` | The top-level robot object — provides access to all subsystems, state, and command building |

---

## Purpose

Each `Binder` implementation owns one logical group of bindings (e.g., driver controls, robot-level automation). They are instantiated and called from `RobotContainer`, which passes in the `Superstructure`. This keeps button maps and trigger logic out of `RobotContainer` itself and makes each binding group independently testable and replaceable.

## Implementations

| Class | Responsibility |
|---|---|
| `DriverXboxBindings` | Xbox driver controller — drive, aim, intake, reset |
| `OperatorPS5Bindings` | PS5 operator controller — index, eject, agitate, utility |
| `RobotBindings` | Robot automation — auto-fire, agitate, teleop re-home |
| `MultiBindings` | Delegates to multiple binders (used on real hardware) |
| `KeyboardBindings` | Simulation keyboard input |
| `AutogenBindings` | PathPlanner named command registration |
| `NamedCommandBindings` | Named commands for autonomous routines |
