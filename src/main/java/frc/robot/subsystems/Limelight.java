// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.LimelightConstants;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.common.hardware.VisionLEDMode;

public class Limelight extends SubsystemBase {

  private PhotonCamera camera;
  AprilTagFieldLayout aprilTagFieldLayout;
  PhotonPoseEstimator photonPoseEstimator;

  public static Limelight instance;

  public static Limelight getInstance() {
    if (instance == null) {
      instance = new Limelight();
    }
    return instance;
  }

  /** Creates a new Limelight. */
  public Limelight() {

    camera = new PhotonCamera("hhCam");

    // Cam mounted facing forward, half a meter forward of center, half a meter up
    // from center.
    Transform3d robotToCam = new Transform3d(new Translation3d(0.5, 0.0, 0.5), new Rotation3d(0, 0, 0));

    try {
      aprilTagFieldLayout = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2024Crescendo.m_resourceFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Construct PhotonPoseEstimator
    photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout,
        PoseStrategy.CLOSEST_TO_REFERENCE_POSE, camera, robotToCam);

  }

  public Optional<EstimatedRobotPose> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
    photonPoseEstimator.setReferencePose(prevEstimatedRobotPose);
    return photonPoseEstimator.update();
  }

  public PhotonCamera getCamera() {
    return camera;
  }

  public BooleanSupplier hasTargetBooleanSupplier() {
    return () -> camera.getLatestResult().hasTargets();
  }

  public void takeSnapshot() {
    camera.takeInputSnapshot();
  }

  public void enableLED() {
    camera.setLED(VisionLEDMode.kOn);
  }

  public void disableLED() {
    camera.setLED(VisionLEDMode.kOff);
  }

  public void setPipeline(int pipelineIndex) {
    camera.setPipelineIndex(pipelineIndex);
  }

  public void setTagMode() {
    setPipeline(0);
    disableLED();
  }

  public void setTapeMode() {
    setPipeline(1);
    enableLED();
  }

  @Override
  public void periodic() {
    // Query the latest result from PhotonVision
    var result = camera.getLatestResult(); // returns a PhotoPipeLine Container

    // Check if the latest result has any targets.
    boolean hasTargets = result.hasTargets();

    SmartDashboard.putBoolean("Has target", hasTargets);

    if (hasTargets) {

      SmartDashboard.putNumber("tag ID", result.getBestTarget().getFiducialId());
      SmartDashboard.putNumber("pose ambiguity", result.getBestTarget().getPoseAmbiguity());

      Transform3d bestCameraToTarget = result.getBestTarget().getBestCameraToTarget();

      SmartDashboard.putNumber("x (roll)",
          Units.radiansToDegrees(bestCameraToTarget.getRotation().getX()));
      SmartDashboard.putNumber("y (pitch)",
          Units.radiansToDegrees(bestCameraToTarget.getRotation().getY()));
      SmartDashboard.putNumber("z (yaw)",
          Units.radiansToDegrees(bestCameraToTarget.getRotation().getZ()));

      SmartDashboard.putNumber("x inches",
          Units.metersToInches(bestCameraToTarget.getX()));
      SmartDashboard.putNumber("y inches",
          Units.metersToInches(bestCameraToTarget.getY()));
      SmartDashboard.putNumber("z inches",
          Units.metersToInches(bestCameraToTarget.getZ()));

    }

  }
}