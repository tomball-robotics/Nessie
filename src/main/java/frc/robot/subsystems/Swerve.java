package frc.robot.subsystems;

import frc.robot.SwerveModule;
import frc.robot.Constants;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Swerve extends SubsystemBase {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveModule[] swerveModules;
    public Pigeon2 gyro;
    public static Field2d odometry;

    public Swerve() {
        odometry = new Field2d();
        gyro = new Pigeon2(Constants.Swerve.pigeonID, "cani");
        gyro.getConfigurator().apply(new Pigeon2Configuration());
        gyro.setYaw(180);

        swerveModules = new SwerveModule[] {
            new SwerveModule(0, Constants.Swerve.Mod0.constants),
            new SwerveModule(1, Constants.Swerve.Mod1.constants),
            new SwerveModule(2, Constants.Swerve.Mod2.constants),
            new SwerveModule(3, Constants.Swerve.Mod3.constants)
        };

        swerveOdometry = new SwerveDriveOdometry(Constants.Swerve.swerveKinematics, getGyroYaw(), getModulePositions());
    }

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {

        SwerveModuleState[] swerveModuleStates =
            Constants.Swerve.swerveKinematics.toSwerveModuleStates(fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation, 
                                    getHeading()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation));
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.Swerve.maxSpeed);

        for(SwerveModule mod : swerveModules){
            mod.setDesiredState(swerveModuleStates[mod.moduleNumber], isOpenLoop);
        }
    }    

    public ChassisSpeeds getRobotRelativeSpeeds(){
        return Constants.Swerve.swerveKinematics.toChassisSpeeds(getModuleStates());
    }

    public void driveRobotRelative(ChassisSpeeds speeds){
        SwerveModuleState[] states = Constants.Swerve.swerveKinematics.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, Constants.AutoConstants.kMaxSpeedMetersPerSecond);
        setModuleStates(states);
    }

    /* Used by SwerveControllerCommand in Auto */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.AutoConstants.kMaxSpeedMetersPerSecond);
        
        for(SwerveModule mod : swerveModules){
            mod.setDesiredState(desiredStates[mod.moduleNumber], false);
        }
    }

    public SwerveModuleState[] getModuleStates(){
        SwerveModuleState[] states = new SwerveModuleState[4];
        for(SwerveModule mod : swerveModules){
            states[mod.moduleNumber] = mod.getState();
        }
        return states;
    }

    public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for(SwerveModule mod : swerveModules){
            positions[mod.moduleNumber] = mod.getPosition();
        }
        return positions;
    }

    public Pose2d getPose() {
        return swerveOdometry.getPoseMeters();
    }

    public void setPose(Pose2d pose) {
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), pose);
    }
    
    public void resetOdometry(Pose2d pose) {
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), pose);
    }

    public Rotation2d getHeading(){
        return getPose().getRotation();
    }
    

    public void setHeading(Rotation2d heading){
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), heading));
    }

    public void zeroHeading(){
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), new Rotation2d()));
    }

    public void autoHeadingFix(){
          var alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            if(alliance.get() == DriverStation.Alliance.Red){
                double angle = getHeading().getDegrees() > 0? getHeading().getDegrees()-180 : getHeading().getDegrees()+180 ;
                setHeading(new Rotation2d(Units.degreesToRadians(angle)));
             }
         }
    }

    public Rotation2d getGyroYaw() {
        return Rotation2d.fromDegrees(gyro.getYaw().getValueAsDouble());
    }

    public void resetModulesToAbsolute(){
        for(SwerveModule mod : swerveModules){
            mod.resetToAbsolute();
        }
    }

    public void setX(){
      swerveModules[0].setDesiredState(new SwerveModuleState(0.0, Rotation2d.fromDegrees(45.0)),true);
      swerveModules[1].setDesiredState(new SwerveModuleState(0.0, Rotation2d.fromDegrees(315.0)),true);
      swerveModules[2].setDesiredState(new SwerveModuleState(0.0, Rotation2d.fromDegrees(315.0)),true);
      swerveModules[3].setDesiredState(new SwerveModuleState(0.0, Rotation2d.fromDegrees(45.0)),true);
    }

    @Override
    public void periodic(){
        odometry.setRobotPose(swerveOdometry.getPoseMeters());
        swerveOdometry.update(getGyroYaw(), getModulePositions());

        
    }

}

/*
                    _____  _____
                <     `/     |
                    >          (
                |   _     _  |
                |  |_) | |_) |
                |  | \ | |   |
                |            |
    ______.______%_|            |__________  _____
_/                                       \|     |
|                   S E B A S                    <
|_____.-._________              ____/|___________|
                |    2006    |
                |    2025    |
                |            |
                |            |
                |   _        <
                |__/         |
                    / `--.    |
                %|            |%
            |/.%%|          -< @%%%
            `\%`@|     v      |@@%@%%
            .%%%@@@|%    |    % @@@%%@%%%%
    _.%%%%%%@@@@@@%%_/%\_%@@%%@@@@@@@%%%%%%
 */