/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team904.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
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
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	private WPI_TalonSRX leftMotor1 = new WPI_TalonSRX(2);
	private WPI_TalonSRX leftMotor2 = new WPI_TalonSRX(3);
	private WPI_TalonSRX leftMotor3 = new WPI_TalonSRX(4);
	private WPI_TalonSRX rightMotor1 = new WPI_TalonSRX(5);
	private WPI_TalonSRX rightMotor2 = new WPI_TalonSRX(6);
	private WPI_TalonSRX rightMotor3 = new WPI_TalonSRX(7);
	
	private Joystick stick = new Joystick(0);
	private DoubleSolenoid shift = new DoubleSolenoid(0, 1);
	
	private double xValueLeft;
	private double xValueRight;
	private double yValueLeft;
	private double yValueRight;
	private double motorLeft;
	private double motorRight;
	private double scaleFactor;
	
	private double yJoy;
	private double xJoy;
	private double zJoy;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		shift.set(DoubleSolenoid.Value.kReverse);
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
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		yJoy = stick.getY();
		xJoy = 0;	//stick.getX();
		zJoy = stick.getZ();
		double xVal, yVal;
		
		if(Math.abs(zJoy) > Math.abs(xJoy))
			xJoy = zJoy;
		
		if(xJoy > 0.20)
			xVal = (xJoy - 0.20) * 1.25;
		else if(xJoy < -0.20)
			xVal = (xJoy + 0.20) * 1.25;
		else
			xVal = 0;
		
		if(yJoy > 0.20)
			yVal = (yJoy - 0.20) * 1.25;
		else if(yJoy < -0.20)
			yVal = (yJoy + 0.20) * 1.25;
		else
			yVal = 0;
		
		drive(yVal, xVal);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	public void drive(double xValue, double yValue) {
		xValueLeft = xValue;
		xValueRight = -xValue;
		yValueLeft = -yValue;
		yValueRight = -yValue;
		motorLeft = (yValueLeft + xValueLeft);
		motorRight = (yValueRight + xValueRight);
		if ((Math.max(Math.abs(motorLeft), Math.abs(motorRight)) > 1)) {
			scaleFactor = Math.max(Math.abs(motorLeft), Math.abs(motorRight));
		} else {
			scaleFactor = 1;
		}
		if (yValueLeft < 0) {
			if (Math.abs(zJoy) < Math.abs(xJoy)) {
				double temp = motorLeft;
				motorLeft = motorRight;
				motorRight = temp;
			}
		}
		motorLeft = -motorLeft / scaleFactor;
		motorRight = -motorRight / scaleFactor;
	
		leftMotor1.set(motorLeft);
		leftMotor2.set(motorLeft);
		leftMotor3.set(motorLeft);
		rightMotor1.set(motorRight);
		rightMotor2.set(motorRight);
		rightMotor3.set(motorRight);
		
		boolean triggerHighGear = stick.getTrigger();
		boolean buttonLowGear = stick.getTop();
		
		if (triggerHighGear) {
			shift.set(DoubleSolenoid.Value.kForward);
		} else if (buttonLowGear) {
			shift.set(DoubleSolenoid.Value.kReverse);
		} else {
			shift.set(DoubleSolenoid.Value.kOff);
		}
	}
}
