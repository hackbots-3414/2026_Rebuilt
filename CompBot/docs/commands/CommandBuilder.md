# CommandBuilder

## Overview

`CommandBuilder` is a simple functional interface that defines the contract for all command factory classes in this project. Rather than subclassing `Command` directly, commands are built as factory objects that receive the full set of subsystems and the `StateManager` at build time.

---

## Interface

```java
public interface CommandBuilder {
    public Command build(Subsystems subsystems, StateManager state);
}
```

**Parameters**

| Parameter | Type | Description |
|---|---|---|
| `subsystems` | `Superstructure.Subsystems` | Record/container holding all robot subsystem references |
| `state` | `StateManager` | Provides shared runtime state: pose, aim params, vision, etc. |

**Returns** — a fully constructed WPILib `Command` ready to be scheduled.

---

## Purpose

Implementing `CommandBuilder` decouples command construction from command scheduling. Each implementing class encapsulates one robot action, and the `Superstructure` or `RobotContainer` can build and compose them without importing subsystem internals directly.

All concrete commands in the `frc.robot.commands` package implement this interface.
