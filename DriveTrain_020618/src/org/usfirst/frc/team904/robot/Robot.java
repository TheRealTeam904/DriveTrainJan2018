/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team904.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kBaselineAuto = "Baseline";
	private static final String kVisionAutoFarLeftRed = "Red";
	private static final String kVisionAutoFarLeftBlue = "Blue";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();


	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Do Nothing", kDefaultAuto);
		m_chooser.addObject("Baseline", kBaselineAuto);
		//m_chooser.addObject("Turn Right Red", kVisionAutoRightRed);
		//m_chooser.addObject("Turn Right Blue", kVisionAutoRightBlue);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		for(WPI_TalonSRX motor : RobotMap.leftMotors)
		{
			motor.setNeutralMode(NeutralMode.Brake);
			motor.setInverted(false);
			motor.set(0);
		}
		for(WPI_TalonSRX motor : RobotMap.rightMotors)
		{
			motor.setNeutralMode(NeutralMode.Brake);
			motor.setInverted(true);
			motor.set(0);
		}

		RobotMap.climber.setNeutralMode(NeutralMode.Brake);
		RobotMap.climber.set(0);

		RobotMap.arms.setNeutralMode(NeutralMode.Brake);
		RobotMap.arms.set(0);
		
		RobotMap.shift.set(RobotMap.shiftLow);
		RobotMap.grabber.set(RobotMap.grabberClose);
		
		RobotMap.camera  = CameraServer.getInstance().startAutomaticCapture();
		RobotMap.camera.setResolution(640, 480);
		
		RobotMap.cvSink = CameraServer.getInstance().getVideo();
		RobotMap.outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
		
		RobotMap.camera.setExposureAuto();
		
		RobotMap.highGear = false;
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
		// System.out.println("Auto selected: " + m_autoSelected);
		
		RobotMap.leftMotors[0].configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		RobotMap.rightMotors[0].configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		
		RobotMap.camera.setExposureManual(50);
		
		RobotMap.leftMotors[0].setSelectedSensorPosition(0, 0, 100);
		
		RobotMap.hitBaseline = false;
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		String gameData;
		switch (m_autoSelected) {
			case kBaselineAuto:
				baseline();
				break;
			case kVisionAutoFarLeftRed:
				baseline();
				turn(RobotMap.right);
				gameData = DriverStation.getInstance().getGameSpecificMessage();
                if(gameData.length() > 0) {
				  if(gameData.charAt(0) == 'L') {
					//place cube on switch
				  } else if(gameData.charAt(1) == 'R') {
					  //place cube on scale
					  /*turn(RobotMap.left);
						toScale();
						turn(RobotMap.right);
						if(pixVal()) {
							// place cube on scale
							RobotMap.shift.set(RobotMap.shiftHigh);
						}*/
				  }
                }
				break;
			case kVisionAutoFarLeftBlue:
				baseline();
				turn(RobotMap.right);
				gameData = DriverStation.getInstance().getGameSpecificMessage();
                if(gameData.length() > 0) {
				  if(gameData.charAt(0) == 'L') {
					//place cube on switch
				  } else if(gameData.charAt(1) == 'R') {
					  //place cube on scale
					  /*turn(RobotMap.left);
						toScale();
						turn(RobotMap.right);
						if(pixVal()) {
							// place cube on scale
							RobotMap.shift.set(RobotMap.shiftHigh);
						}*/
				  }
                }
                break;
			case kDefaultAuto:
			default:
				// Do Nothing
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		
		// Drivetrain
		///////////////////
		double[] xy = deadzone(
				RobotMap.driveStick.getRawAxis(RobotMap.driveStickTurnAxis),
				RobotMap.driveStick.getRawAxis(RobotMap.driveStickForwardAxis));
		drive(xy[0], xy[1], 1);
		
		// Accessory motors
		/////////////////////
		RobotMap.arms.set(deadzone(RobotMap.controller.getRawAxis(RobotMap.accessoryStickArmsAxis)));
		RobotMap.climber.set(deadzone(RobotMap.controller.getRawAxis(RobotMap.accessoryStickClimbAxis)));
		
		// Grabber - right open, left close
		////////////////////
		if(RobotMap.controller.getRawAxis(RobotMap.accessoryStickGrabberGrabTrigger) > 0.5)
		{
			RobotMap.grabber.set(RobotMap.grabberClose);
		}
		else if(RobotMap.controller.getRawAxis(RobotMap.accessoryStickGrabberReleaseTrigger) > 0.5)
		{
			RobotMap.grabber.set(RobotMap.grabberOpen);
		}
		else
		{
			RobotMap.grabber.set(DoubleSolenoid.Value.kOff);
		}
		
		
		// Gear shifting
		/////////////////////
		boolean triggerLowGear = RobotMap.driveStick.getTrigger();
		boolean buttonHighGear = RobotMap.driveStick.getTop();
		
		if (triggerLowGear) {
			RobotMap.shift.set(RobotMap.shiftLow);
			RobotMap.highGear = false;
			SmartDashboard.putBoolean("High Gear:", RobotMap.highGear);
		} else if (buttonHighGear) {
			RobotMap.shift.set(RobotMap.shiftHigh);
			RobotMap.highGear = true;
			SmartDashboard.putBoolean("High Gear:", RobotMap.highGear);
		} else {
			RobotMap.shift.set(DoubleSolenoid.Value.kOff);
		}
		
		
		// Limit switch override for reset.
		// Should be hard to trigger, we only want to do this
		// in the pit.
		///////////////////////////////////
		if(RobotMap.driveStick.getRawButton(7)
				&& RobotMap.driveStick.getRawButton(8)
				&& RobotMap.driveStick.getRawButton(9)
				&& RobotMap.driveStick.getRawButton(10))
		{
			RobotMap.climber.overrideLimitSwitchesEnable(false);
		}
		else
		{
			RobotMap.climber.overrideLimitSwitchesEnable(true);
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	/**
	 * Autonomous methods
	 */
	public void baseline() {
		SmartDashboard.putNumber("encoder", RobotMap.leftMotors[0].getSelectedSensorPosition(0));
		if(!RobotMap.hitBaseline) {
			RobotMap.encoderRatio = RobotMap.rightMotors[0].getSelectedSensorPosition(0)
					/ RobotMap.leftMotors[0].getSelectedSensorPosition(0);
			drive(0, -0.25, RobotMap.encoderRatio);
		}
		if(Math.abs(RobotMap.leftMotors[0].getSelectedSensorPosition(0)) >= RobotMap.baseline) {
			drive(0, 0, 1);
			RobotMap.hitBaseline = true;
		}
	}
	
	public void turn(int dir) {
		drive(dir, 0, 1);
		while(Math.abs(RobotMap.leftMotors[0].getSelectedSensorPosition(0))
				< RobotMap.turnVal);
		drive(0, 0, 1);
	}
	
	public void toScale() {
		drive(0, 1, 1);
		while(Math.abs(RobotMap.leftMotors[0].getSelectedSensorPosition(0))
				< RobotMap.scaleDist);
		drive(0, 0, 1);
	}
	
/*	public boolean pixVal() {
		double red = 0;
		double blue = 0;
		
		long status = RobotMap.cvSink.grabFrame(RobotMap.source);
		if(status == 0) {System.out.println(RobotMap.cvSink.getError());}
		else {System.out.println("OK");}
		
		int rows = RobotMap.source.rows();
		int cols = RobotMap.source.cols();
		double[] temp = new double[3];
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				temp = RobotMap.source.get(i, j);
				blue += temp[0];	// add the pixel values for blue
				red += temp[2];		// add the pixel values for red
			}
		}

		System.out.println("");
		
		if(red > blue)
			return true;
		else
			return false;
	}*/
	
	/**
	 * Drive methods
	 */
	public double deadzone(double x) {
		if(x > 0.20)
			x = (x - 0.20) * 1.25;
		else if(x < -0.20)
			x = (x + 0.20) * 1.25;
		else
			x = 0;
		
		return x;
	}
	
	public double[] deadzone(double x, double y) {	
		return new double[] {deadzone(x), deadzone(y)};
	}
	
	public void drive(double turn, double forward, double ratio) {
		double motorLeft;
		double motorRight;
		if (ratio < 1) {
			motorLeft = ((forward * ratio) + turn);
		} else {
			motorLeft = (forward + turn);
		}
		
		if (ratio > 1) {
			motorRight = ((forward / ratio) - turn);
		} else {
			motorRight = (forward - turn);
		}
		
		motorLeft *= RobotMap.driveMotorSpeedScale;
		motorRight *= RobotMap.driveMotorSpeedScale;
		
		double scaleFactor;
		
		if ((Math.max(Math.abs(motorLeft), Math.abs(motorRight)) > 1)) {
			scaleFactor = Math.max(Math.abs(motorLeft), Math.abs(motorRight));
		} else {
			scaleFactor = 1;
		}
		
		motorLeft = motorLeft / scaleFactor;
		motorRight = motorRight / scaleFactor;
	
		for(WPI_TalonSRX motor : RobotMap.leftMotors)
		{
			motor.set(motorLeft);
		}
		for(WPI_TalonSRX motor : RobotMap.rightMotors)
		{
			motor.set(motorRight);
		}
	}
}
