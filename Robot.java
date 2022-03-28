// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
 
package frc.robot;
 
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  
  // Right Motors
  private final int right_Drive_forward_ID = 1;
  private final int right_Drive_backward_ID = 2;
 
  private final CANSparkMax right_Drive_forward = new CANSparkMax(right_Drive_forward_ID, MotorType.kBrushed);
  private final CANSparkMax right_Drive_backward = new CANSparkMax(right_Drive_backward_ID, MotorType.kBrushed);
 
  private final SpeedControllerGroup right_Drive = new SpeedControllerGroup(right_Drive_forward, right_Drive_backward);
 
  // Left Motors 
  private final int left_Drive_forward_ID = 3;
  private final int left_Drive_backward_ID = 4;
 
  private final CANSparkMax left_Drive_forward = new CANSparkMax(left_Drive_forward_ID, MotorType.kBrushed);
  private final CANSparkMax left_Drive_backward = new CANSparkMax(left_Drive_backward_ID, MotorType.kBrushed);
 
  private final SpeedControllerGroup left_Drive = new SpeedControllerGroup(left_Drive_forward, left_Drive_backward);
 
  // Shooter Motor
  private final int shooter_ID = 5;
  private final CANSparkMax shooter = new CANSparkMax(shooter_ID, MotorType.kBrushed);
 
// Ball Collector Motor
  private final int ball_collector_ID = 6;
  private final CANSparkMax ball_collector = new CANSparkMax(ball_collector_ID, MotorType.kBrushed);
 
  // Drive Base
  private final DifferentialDrive drive_base = new DifferentialDrive(left_Drive, right_Drive);
 
  //Xbox Controller
  private final XboxController driver = new XboxController(0);
 
//Solenoid
public static DoubleSolenoid solenoid = new DoubleSolenoid(8, PneumaticsModuleType.REVPH, 12, 13);
//COMPESO
public static Compressor comp = new Compressor( PneumaticsModuleType.REVPH);
//enabel command
public static void solenoidEnableCommand (){
solenoid.set(Value.kForward);
}
public static void solenoiDisableCommand (){
  solenoid.set(Value.kOff);
}
  public static void solenoidRevrseCommand (){
    solenoid.set(Value.kReverse);
}
 
  @Override
  public void robotInit() {
    left_Drive_backward.follow(left_Drive_forward);
    right_Drive_backward.follow(right_Drive_forward);
    left_Drive_forward.setInverted(true);
    right_Drive_backward.setInverted(true);
    comp.enableDigital();
  }
 
  @Override
  public void robotPeriodic() {}

  // autonomous variables
  public AutoState autostate = AutoState.stop;
  public double AUTO_SHOOT_POWER = 0.8;
  public double AUTO_SHOOT_TIME = 3;
  public double AUTO_BACKUP_POWER = -0.5;
  public double AUTO_BACKUP_TIME = 5;
  public Timer timer = new Timer();

  enum AutoState {
    ballDump,
    tarmac,
    stop
  }
 
  @Override
  public void autonomousInit() {
    autostate = AutoState.ballDump;
    timer.reset();
    timer.start();
  }
 
  @Override
  public void autonomousPeriodic() {
    switch (autostate)
    {
      case ballDump:
        drive_base.stopMotor();
        shooter.set(AUTO_SHOOT_POWER);
        ball_collector.stopMotor();
        if (timer.get() > AUTO_SHOOT_TIME) {
          shooter.stopMotor();
          timer.reset();
          timer.start();
          autostate = AutoState.tarmac;
        }
        break;
      case tarmac:
        drive_base.tankDrive(AUTO_BACKUP_POWER, AUTO_BACKUP_POWER);
        shooter.stopMotor();
        ball_collector.stopMotor();
        if (timer.get() > AUTO_BACKUP_TIME) {
          drive_base.stopMotor();
          timer.reset();
          timer.start();
          autostate = AutoState.stop;
        }
        break;
      case stop:
      default:
        drive_base.stopMotor();
        shooter.stopMotor();
        ball_collector.stopMotor();
        break;
    }
  }
 
  @Override
  public void teleopInit() {
    timer.stop();
    timer.reset();
  }
 
  @Override
  public void teleopPeriodic() {
 
    double multiplier = 1;
    drive_base.tankDrive(-driver.getLeftY() * multiplier,  -driver.getRightY() * multiplier);
 
 
    if (driver.getLeftBumper())
    {
      shooter.set(0.8);
    }
 
    else if (driver.getLeftTriggerAxis() > 0)
    {
      shooter.set(-0.8);
    }
 
    else
    {
      shooter.stopMotor();
    }
  
    if (driver.getRightBumper())
    {
      ball_collector.set(0.8);
    }
 
    else if (driver.getRightTriggerAxis() > 0)
    {
      ball_collector.set(-0.8);
    }
 
    else
    {
      ball_collector.stopMotor();
    }
  
    //Ball Collector Code
    if (driver.getAButton())
    {
      ball_collector.set(0.8);
    }
 
    else if (driver.getBButton())
    
    {
      ball_collector.set(-0.8);
    }

     //climb code

    if (driver.getXButton()){

      solenoidEnableCommand();
    }
    else if (driver.getYButton()){

      solenoidRevrseCommand();
   }
  
  
  }
  @Override
  public void disabledInit() {}
 
  @Override
  public void disabledPeriodic() {}
 
  @Override
  public void testInit() {}
 
  @Override
  public void testPeriodic() {}
}
 
