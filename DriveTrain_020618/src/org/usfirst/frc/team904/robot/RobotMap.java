package org.usfirst.frc.team904.robot;

import org.opencv.core.Mat;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

/**
 * TODO: On a press of a button make set of value's true to prepare for climbing.
 * Also, another button press to begin a set of climbing instructions.
 * @author DCube
 */

public class RobotMap {

	public static WPI_TalonSRX[] leftMotors = {new WPI_TalonSRX(2), new WPI_TalonSRX(3), new WPI_TalonSRX(4)};
	public static WPI_TalonSRX[] rightMotors = {new WPI_TalonSRX(5), new WPI_TalonSRX(6), new WPI_TalonSRX(7)};

	public static WPI_TalonSRX arms = new WPI_TalonSRX(9);
	public static WPI_TalonSRX climber = new WPI_TalonSRX(8);

	public static DoubleSolenoid shift = new DoubleSolenoid(0, 1);
	public static DoubleSolenoid.Value shiftLow = DoubleSolenoid.Value.kReverse;
	public static DoubleSolenoid.Value shiftHigh = DoubleSolenoid.Value.kForward;
	
	public static DoubleSolenoid grabber = new DoubleSolenoid(2, 3);
	public static DoubleSolenoid.Value grabberClose = DoubleSolenoid.Value.kReverse;
	public static DoubleSolenoid.Value grabberOpen = DoubleSolenoid.Value.kForward;
	
	public static Joystick driveStick = new Joystick(0);
	
	public static boolean highGear;
	
	public static double encoderRatio;

	// channels for controls on the drive controller,
	// shown as axes on the driver station when looking at USB devices
	public static int driveStickForwardAxis = driveStick.getYChannel();
	public static int driveStickTurnAxis = driveStick.getZChannel();
	
	public static XboxController controller = new XboxController(1);
	
	// channels for controls on the accessory controller,
	// shown as axes on the driver station when looking at USB devices
	public static int accessoryStickArmsAxis = 5;
	public static int accessoryStickClimbAxis = 1;
	public static int accessoryStickGrabberGrabTrigger = 3;
	public static int accessoryStickGrabberReleaseTrigger = 2;

	// motor speed control
	public static final double driveMotorSpeedScale = 0.5;
	// encoder values to disable high gear to resist tipping the robot
	public static final double elevation = 0.0; // climber elevation
	public static final double extend = 0.0; // arm extension
	
	// encoder values for auton
	public static final int left = -1;
	public static final int right = 1;
	public static final int baseline = 67000;
	public static boolean hitBaseline;
	public static final int turnVal = 1000;
	public static final int scaleDist = 21200;
	public static final int blueVal = 0xFF0000;
	public static final int redVal = 0x0000FF;
	
	// visual processing
	public static Mat source = new Mat();
	public static UsbCamera camera;
	public static CvSink cvSink;
	public static CvSource outputStream;
}
