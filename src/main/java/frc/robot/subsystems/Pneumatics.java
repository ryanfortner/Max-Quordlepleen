// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;

public class Pneumatics extends SubsystemBase {
  Compressor phCompressor = new Compressor(Constants.Pneumatics.pneumaticsHubId, PneumaticsModuleType.REVPH);
  DoubleSolenoid gripperSolenoid = new DoubleSolenoid(Constants.Pneumatics.pneumaticsHubId, PneumaticsModuleType.REVPH,
      0, 1);

  boolean gripperState;

  boolean compressorEnabled;
  boolean pressureSwitch;
  double compressorCurrent;

  /** Creates a new Pneumatics. */
  public Pneumatics() {
    // phCompressor.disable();
    phCompressor.enableDigital();
    gripperSolenoid.set(kReverse);

    gripperState = false;

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    compressorEnabled = phCompressor.isEnabled();
    pressureSwitch = phCompressor.getPressureSwitchValue();
    compressorCurrent = phCompressor.getCurrent();

    // SmartDashboard.putBoolean("Compressor Enabled", compressorEnabled);
    // SmartDashboard.putBoolean("Pressure Switch", pressureSwitch);
    // SmartDashboard.putNumber("Compressor Current", compressorCurrent);
    // SmartDashboard.putBoolean("Gripper State", gripperState);
    // SmartDashboard.putBoolean("Extender State", extenderState);
  }

  public void setGripperState(Value kDirection) {
    gripperSolenoid.set(kDirection);
    if (kDirection.equals(kForward)) {
      gripperState = true;
    } else {
      gripperState = false;
    }
    // gripperState = !gripperState;
  }

  public void toggleGripperSolenoid() {
    gripperSolenoid.toggle();
    gripperState = !gripperState;
  }

  public boolean getCompressorEnabled() {
    return compressorEnabled;
  }

  public boolean getGripperState() {
    return gripperState;
  }

  public Compressor getCompressor() {
    return phCompressor;
  }

  public DoubleSolenoid getGripperSolenoid() {
    return gripperSolenoid;
  }

}
