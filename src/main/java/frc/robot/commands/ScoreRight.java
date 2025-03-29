package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.endeffector.IntakeAndHoldCoral;
import frc.robot.commands.position.SetArmPosition;
import frc.robot.commands.position.SetElevatorPosition;
import frc.robot.commands.swerve.AlignToReefTagRelative;
import frc.robot.subsystems.StateMachine;
import frc.robot.subsystems.Swerve;
import frc.robot.subsystems.superstructure.Arm;
import frc.robot.subsystems.superstructure.Elevator;
import frc.robot.subsystems.superstructure.EndEffector;

public class ScoreRight extends SequentialCommandGroup {

  public ScoreRight(StateMachine stateMachine, Arm arm, Elevator elevator, Swerve swerve, EndEffector endEffector) {

    addCommands(
      new AlignToReefTagRelative("back", swerve).withTimeout(3),
      new SetArmPosition(arm, 2.31),
      new SetElevatorPosition(elevator, stateMachine.getDesiredLevel().getElevatorPosition()),
      new WaitCommand(1),
      new AlignToReefTagRelative("right", swerve).withTimeout(3),
      new WaitCommand(.5),
      new SetArmPosition(arm, stateMachine.getDesiredLevel().getArmPosition()),
      new WaitCommand(.3),
      new IntakeAndHoldCoral(endEffector).withTimeout(.3),
      new WaitCommand(1),
      new AutoShootCoral(endEffector),
      new WaitCommand(.5),
      new AlignToReefTagRelative("back", swerve).withTimeout(3),
      new InstantCommand(() -> stateMachine.requestState(StateMachine.STOW))
    );

  }

}
