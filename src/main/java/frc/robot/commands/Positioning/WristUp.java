package frc.robot.commands.Positioning;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Wrist;

public class WristUp extends Command {

  private Wrist wrist;

  public WristUp(Wrist wrist) {
    this.wrist = wrist;
    addRequirements(wrist);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    wrist.setSpeed(1);
  }

  @Override
  public void end(boolean interrupted) {
    wrist.stop();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
