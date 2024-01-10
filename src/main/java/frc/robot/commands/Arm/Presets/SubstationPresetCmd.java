// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Arm.Presets;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.Arm.ExtendToSetpointSequenceCmd;
import frc.robot.commands.Arm.PIDTiltArmCmd;
import frc.robot.subsystems.TiltArm;
import frc.robot.subsystems.ExtendArm;
import frc.robot.subsystems.Pneumatics;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SubstationPresetCmd extends SequentialCommandGroup {

  /** Creates a new MidCubeCmd. */
  public SubstationPresetCmd(TiltArm tiltArm, ExtendArm extendArm, Pneumatics pneumatics) {

    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(new PIDTiltArmCmd(tiltArm, Constants.Presets.SUBSTATION_TILT),
        new InstantCommand(() -> pneumatics.setGripperState(Value.kForward)),
        new ExtendToSetpointSequenceCmd(extendArm, Constants.Presets.SUBSTATION_EXTEND));
  }
}
