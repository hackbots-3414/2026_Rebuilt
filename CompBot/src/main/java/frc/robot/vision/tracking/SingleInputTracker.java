// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.vision.tracking;

import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Transform3d;

import java.util.List;
import java.util.Optional;

import org.photonvision.targeting.PhotonPipelineResult;

import frc.robot.vision.CameraIO;
import frc.robot.vision.CameraIO.CameraIOInputs;

/** Add your docs here. */
public class SingleInputTracker {
    private CameraIO io;
    private CameraIOInputs inputs;
    private Optional<Transform3d> transform;
    private Optional<Double> confidence = Optional.of(0.0);
    public SingleInputTracker(CameraIO io) {
        this.io = io;
    }
    
    public void refresh() {
        io.updateInputs(inputs);
        PhotonPipelineResult result = inputs.unreadResults.get(0);
        if (result.hasTargets()) {
            List<PhotonTrackedTarget> targets = result.getTargets();
            confidence =  Optional.of(targets.get(0).getPoseAmbiguity());
            transform = Optional.of(targets.get(0).getBestCameraToTarget());
        } else {
            confidence = null;
            transform = null;
        }
    }

    public Optional<Double> getConfidence() {
        return confidence;
    }

    public Optional<Transform3d> getTransform() {
        return transform; 
    }

}
