package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.NessieState;
import frc.robot.commands.position.SetArmPosition;
import frc.robot.commands.position.SetElevatorPosition;
import frc.robot.commands.position.SetIntakePivotPosition;
import frc.robot.subsystems.intake.IntakePivot;
import frc.robot.subsystems.superstructure.Arm;
import frc.robot.subsystems.superstructure.Elevator;

public class StateMachine extends SubsystemBase {

  // nessie's states

  public static final NessieState START = new NessieState(
    "Start", 0, 0, 0);
  public static final NessieState STOW = new NessieState( // done
    "Stow", .5, 0, 0);
  public static final NessieState L1 = new NessieState(
    "L1", .3, 0, .08);
  public static final NessieState L2 = new NessieState( // done
    "L2", 1.37799072265625, 0, 0);
  public static final NessieState L3 = new NessieState( // done
    "L3", 1.66351318359375, 0.86962890625, 0);
  public static final NessieState L4 = new NessieState( // done
    "L4", 1.85430908203125, 4.68536376953125, 0);
  public static final NessieState INTAKE = new NessieState(
    "Intake", 0.12, 0, 0);
  public static final NessieState INTAKE_CLEARANCE = new NessieState(
    "Intake Clearance", .12, 1.86, 0);
  public static final NessieState ALGAE_TAXI= new NessieState(
    "Algae Stow", 1.93, 0, 0);
  public static final NessieState ALGAE_SHOOT = new NessieState(
    "Algae Shoot", 1.93, 5.0327880859375, 0);
  public static final NessieState ALGAE_INTAKE_LOW = new NessieState(
    "Algae Intake Low", .83, 0, 0);
  public static final NessieState ALGAE_INTAKE_HIGH = new NessieState(
    "Algae Intake High", 1.01, 1.41, 0);
  public static final NessieState ALGAE_PROCESS = new NessieState(
    "Algae Process", 0, 0, 0);
  public static final NessieState DISENGAGED = new NessieState(
    "Disengaged", -1, -1, -1);

  private NessieState currentState;

  private Elevator elevator;
  private Arm arm;
  private IntakePivot intakePivot;

  public StateMachine(Elevator elevator, Arm arm, IntakePivot intakePivot) {
    this.elevator = elevator;
    this.arm = arm;
    this.intakePivot = intakePivot;

    currentState = DISENGAGED;
  }

  public void requestState(NessieState desiredState) {
    if(desiredState == DISENGAGED) {
      new InstantCommand(() -> arm.disengage()).schedule();
      new InstantCommand(() -> elevator.disengage()).schedule();
      new InstantCommand(() -> intakePivot.disengage()).schedule();
    }else {
      new SetArmPosition(arm, desiredState.getArmPosition()).schedule();
      new SetElevatorPosition(elevator, desiredState.getElevatorPosition()).schedule();
      new SetIntakePivotPosition(intakePivot, desiredState.getIntakePosition()).schedule();
    }
    
    currentState = desiredState;
  }

  public boolean stateReached() {
    return elevator.isFinished() && arm.isFinished() && intakePivot.isFinished();
  }

  @Override
  public void periodic() {
    SmartDashboard.putString("StateMachine/Current State", currentState.getName());
    SmartDashboard.putBoolean("StateMachine/State Reached", stateReached());
  }

}