package frc.robot.subsystems;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.Swerve;

public class SwerveModule extends SubsystemBase {

  /**
   * Class to represent and handle a swerve module
   * A module's state is measured by a CANCoder for the absolute position,
   * integrated CANEncoder for relative position
   * for both rotation and linear movement
   */

  public PIDController rotationController;
  private final int moduleID;

  private static final double rotationkP = 0.5;
  private static final double rotationkD = 0.0;

  public static double angularSetPoint;

  public static double DrivePIDOutput = 0;
  public static double feedForwardOutputVoltage = 0;
  public static double driveOutput = 0;
  public static double velolictySetpoint = 0;
  public static double currentDriveVelocity = 0;

  private static final double drivekP = 0.015;

  private final CANSparkMax driveMotor;
  private final CANSparkMax rotationMotor;

  public CANSparkMax getDriveMotor() {
    return driveMotor;
  }

  public CANSparkMax getRotationMotor() {
    return rotationMotor;
  }

  private final RelativeEncoder driveEncoder;
  private final RelativeEncoder rotationEncoder;

  private final CANcoder canCoder;

  // absolute offset for the CANCoder so that the wheels can be aligned when the
  // robot is turned on
  private final Rotation2d offset;
  private final SparkPIDController driveController;

  public SwerveModule(int moduleID,
      int driveMotorId,
      int rotationMotorId,
      int canCoderId,
      double measuredOffsetRadians) {

    driveMotor = new CANSparkMax(driveMotorId, com.revrobotics.CANSparkLowLevel.MotorType.kBrushless);
    rotationMotor = new CANSparkMax(rotationMotorId, com.revrobotics.CANSparkLowLevel.MotorType.kBrushless);

    driveEncoder = driveMotor.getEncoder();
    rotationEncoder = rotationMotor.getEncoder();
    rotationController = new PIDController(0.8, 0, 0.0);
    rotationController.enableContinuousInput(-Math.PI, Math.PI);

    canCoder = new CANcoder(canCoderId);

    offset = new Rotation2d(measuredOffsetRadians);

    driveMotor.setIdleMode(IdleMode.kBrake);
    rotationMotor.setIdleMode(IdleMode.kBrake);

    driveController = driveMotor.getPIDController();

    rotationController.setP(rotationkP);
    rotationController.setD(rotationkD);
    this.moduleID = moduleID;

    driveController.setP(drivekP);

    // set the output of the drive encoder to be in radians for linear measurement
    // driveEncoder.setPositionConversionFactor(
    // 2.0 * Math.PI / Swerve.driveGearRatio);
    driveEncoder.setPositionConversionFactor(0.047286787200699704);

    // set the output of the drive encoder to be in radians per second for velocity
    // measurement
    // driveEncoder.setVelocityConversionFactor(
    // 2.0 * Math.PI / 60 / Swerve.driveGearRatio);
    driveEncoder.setVelocityConversionFactor(0.047286787200699704 / 60);

    // set the output of the rotation encoder to be in radians
    rotationEncoder.setPositionConversionFactor(2 * Math.PI / Swerve.angleGearRatio);

    // configure the CANCoder to output in unsigned (wrap around from 360 to 0
    // degrees)
    // canCoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);
    CANcoderConfiguration config = new CANcoderConfiguration();
    config.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1;
    config.MagnetSensor.SensorDirection = SensorDirectionValue.CounterClockwise_Positive;
    canCoder.getConfigurator().apply(config);

  }

  public void resetDistance() {

    driveEncoder.setPosition(0.0);

  }

  /** Returns the module state (turn angle and drive velocity). */
  public SwerveModuleState getState() {
    return new SwerveModuleState(getCurrentVelocityMetersPerSecond(), getCanCoderAngle());
  }

  public double getDriveDistanceRadians() {

    return driveEncoder.getPosition();

  }

  public Rotation2d getCanCoderAngle() {

    // double unsignedAngle =
    // (Units.degreesToRadians(canCoder.getAbsolutePosition()) -
    // offset.getRadians())
    // % (2 * Math.PI);

    double unsignedAngle = (Math.PI * 2 *
        canCoder.getAbsolutePosition().getValueAsDouble()) - offset.getRadians()
            % (2 * Math.PI);

    return new Rotation2d(unsignedAngle);

  }

  public Rotation2d getIntegratedAngle() {

    double unsignedAngle = rotationEncoder.getPosition() % (2 * Math.PI);

    if (unsignedAngle < 0)
      unsignedAngle += 2 * Math.PI;

    return new Rotation2d(unsignedAngle);

  }

  // public double getCurrentVelocityRadiansPerSecond() {

  // return driveEncoder.getVelocity();

  // }

  public double getCurrentVelocityMetersPerSecond() {

    // return driveEncoder.getVelocity() * (Swerve.wheelDiameter / 2.0);
    return driveEncoder.getVelocity();

  }

  public double getCurrentDistanceMetersPerSecond() {
    return driveEncoder.getPosition();
    // return driveEncoder.getPosition() * (Swerve.wheelDiameter / 2.0);
  }

  // unwraps a target angle to be [0,2π]
  // public static double placeInAppropriate0To360Scope(double unwrappedAngle) {

  // double modAngle = unwrappedAngle % (2.0 * Math.PI);

  // if (modAngle < 0.0)
  // modAngle += 2.0 * Math.PI;

  // double wrappedAngle = modAngle;

  // return wrappedAngle;

  // }

  public static double placeInAppropriate0To360Scope(double scopeReference, double newAngle) {
    double lowerBound;
    double upperBound;
    double lowerOffset = scopeReference % (2.0 * Math.PI);
    if (lowerOffset >= 0) {
      lowerBound = scopeReference - lowerOffset;
      upperBound = scopeReference + ((2.0 * Math.PI) - lowerOffset);
    } else {
      upperBound = scopeReference - lowerOffset;
      lowerBound = scopeReference - ((2.0 * Math.PI) + lowerOffset);
    }
    while (newAngle < lowerBound) {
      newAngle += (2.0 * Math.PI);
    }
    while (newAngle > upperBound) {
      newAngle -= (2.0 * Math.PI);
    }
    if (newAngle - scopeReference > (Math.PI)) {
      newAngle -= (2.0 * Math.PI);
    } else if (newAngle - scopeReference < -(Math.PI)) {
      newAngle += (2.0 * Math.PI);
    }
    return newAngle;
  }

  /**
   * Minimize the change in heading the desired swerve module state would require
   * by potentially
   * reversing the direction the wheel spins. Customized from WPILib's version to
   * include placing in
   * appropriate scope for CTRE and REV onboard control as both controllers as of
   * writing don't have
   * support for continuous input.
   *
   * @param desiredState The desired state.
   * @param currentAngle The current module angle.
   */
  public static SwerveModuleState optimize(
      SwerveModuleState desiredState, Rotation2d currentAngle) {

    // double targetAngle =
    // placeInAppropriate0To360Scope(desiredState.angle.getRadians());
    double targetAngle = placeInAppropriate0To360Scope(currentAngle.getRadians(), desiredState.angle.getRadians());

    double targetSpeed = desiredState.speedMetersPerSecond;
    double delta = (targetAngle - currentAngle.getRadians());
    if (Math.abs(delta) > (Math.PI / 2)) {
      targetSpeed = -targetSpeed;
      targetAngle = delta > Math.PI / 2 ? (targetAngle -= Math.PI) : (targetAngle += Math.PI);
    }
    return new SwerveModuleState(targetSpeed, new Rotation2d(targetAngle));
  }

  /**
   * Returns the module ID.
   *
   * @return The ID number of the module (0-3).
   */
  public int getModuleID() {
    return moduleID;
  }

  // initialize the integrated NEO encoder to the offset (relative to home
  // position)
  // measured by the CANCoder
  public void initRotationOffset() {

    rotationEncoder.setPosition(getCanCoderAngle().getRadians());

  }

  /**
   * Method to set the desired state of the swerve module
   * Parameter:
   * SwerveModuleState object that holds a desired linear and rotational setpoint
   * Uses PID and a feedforward to control the output
   */
  public void setDesiredStateClosedLoop(SwerveModuleState unoptimizedDesiredState) {
    if (Math.abs(unoptimizedDesiredState.speedMetersPerSecond) < 0.001) {
      stop();
      return;
    }

    SwerveModuleState optimizedDesiredState = optimize(unoptimizedDesiredState, getIntegratedAngle());

    angularSetPoint = placeInAppropriate0To360Scope(
        optimizedDesiredState.angle.getRadians(), optimizedDesiredState.angle.getRadians());

    rotationMotor.set(rotationController.calculate(getIntegratedAngle().getRadians(), angularSetPoint));

    velolictySetpoint = optimizedDesiredState.speedMetersPerSecond;

    if (RobotState.isAutonomous()) {
      driveMotor.setVoltage(-Swerve.driveFF.calculate(velolictySetpoint));
    } else {
      // Swerve.driveFF.calculate(angularVelolictySetpoint);
      // driveMotor.getPIDController().setP(0.0020645);
      // driveMotor.getPIDController().setI(0.0);
      // driveMotor.getPIDController().setD(0.0);

      currentDriveVelocity = getCurrentVelocityMetersPerSecond();

      DrivePIDOutput = new PIDController(0.0020645, 0, 0).calculate(currentDriveVelocity,
          velolictySetpoint);
      // DrivePIDOutput = 0;
      feedForwardOutputVoltage = (new SimpleMotorFeedforward(0.012, 0.2, 0)
          .calculate(velolictySetpoint));
      driveOutput = (DrivePIDOutput + feedForwardOutputVoltage);

      driveMotor.set(driveOutput);

      // driveMotor.getPIDController().setReference(angularVelolictySetpoint,
      // ControlType.kVelocity);

      // driveMotor.set(
      // new PIDController(0.01, 0, 0).calculate(getCurrentVelocityMetersPerSecond(),
      // angularVelolictySetpoint));

      // driveMotor.setVoltage(Swerve.driveFF.calculate(angularVelolictySetpoint));
      // driveMotor.set(optimizedDesiredState.speedMetersPerSecond /
      // Swerve.maxSpeed);
    }
  }

  public void resetEncoders() {

    driveEncoder.setPosition(0);
    rotationEncoder.setPosition(0);

  }

  public void stop() {
    driveMotor.set(0);
    rotationMotor.set(0);
  }
}