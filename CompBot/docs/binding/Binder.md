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
| `DriverBindings` | PS5 controller — drive, aim, intake, and climb buttons |
| `RobotBindings` | Autonomous robot behavior — auto-fire on aim lock, teleop re-home after climb |
