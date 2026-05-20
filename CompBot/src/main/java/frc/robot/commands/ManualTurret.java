// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.superstructure.StateManager;
import frc.robot.superstructure.Superstructure.Subsystems;

/** Add your docs here. */
public class ManualTurret implements CommandBuilder {
    private final DoubleSupplier velocity;

    public ManualTurret(DoubleSupplier velocity) {
        this.velocity = velocity;
    }

    @Override
    public Command build(Subsystems subsystems, StateManager state) {
        return subsystems.turret().manualControl(velocity);
    }}
