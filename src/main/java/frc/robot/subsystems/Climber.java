// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ClimberConstants;

public class Climber extends SubsystemBase {

  public CANSparkMax climberMotor;
  public DutyCycleEncoder climberThruBoreEncoder;
  public PIDController climberPIDController;

  /** Creates a new Climber. */
  public Climber() {

    // climberMotor = new CANSparkMax(ClimberConstants.climberMotorID,
    // com.revrobotics.CANSparkLowLevel.MotorType.kBrushless);
    // climberThruBoreEncoder = new
    // DutyCycleEncoder(ClimberConstants.climberThruBoreDIO);

    // climberPIDController = new PIDController(ClimberConstants.climbkP, 0.00,
    // ClimberConstants.climbkD);

    // climberMotor.restoreFactoryDefaults();
    // climberMotor.setIdleMode(IdleMode.kBrake);
    // climberMotor.setInverted(false);

  }

  @Override
  public void periodic() {
    // SmartDashboard.putNumber("climber thrubore pos",
    // climberThruBoreEncoder.getAbsolutePosition());

    // This method will be called once per scheduler run
  }
}
