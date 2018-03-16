package org.usfirst.frc.team904.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPlaceCube {
	
	static double config_armRaiseTime = 2.0;
	static double config_grabberReleaseTime = 0.1;
	
	static double config_armRaiseSpeed = 0.2;
	
	SendableChooser<Character> startingPosition = new SendableChooser<Character>();
	boolean shouldPlaceCube = false;
	Timer armRaiseTimer = new Timer();
	Timer grabberReleaseTimer = new Timer();
	
	public void onRobotInit()
	{
		startingPosition.addObject("Left", 'L');
		startingPosition.addObject("Right", 'R');
		startingPosition.addObject("Don't Place", 'X');
		SmartDashboard.putData("Starting position", startingPosition);
	}
	
	public void onAutonomousInit()
	{
		armRaiseTimer.stop();
		armRaiseTimer.reset();
		
		grabberReleaseTimer.stop();
		grabberReleaseTimer.reset();
		
		String fieldSides = DriverStation
			.getInstance()
			.getGameSpecificMessage();
			
		char robotSide = startingPosition.getSelected();
		
		if(robotSide != 'X' && robotSide == fieldSides.charAt(0))
		{
			shouldPlaceCube = true;
		}
	}
	
	public void maybePlaceCube()
	{
		if(shouldPlaceCube)
		{
			if(armRaiseTimer.get() == 0)
			{
				armRaiseTimer.start();
				RobotMap.arms.set(config_armRaiseSpeed);
			}
			
			if(armRaiseTimer.get() > config_armRaiseTime)
			{
				armRaiseTimer.stop();
				RobotMap.arms.set(0);
				
				if(grabberReleaseTimer.get() == 0)
				{
					grabberReleaseTimer.start();
					RobotMap.grabber.set(RobotMap.grabberOpen);
				}
				
				if(grabberReleaseTimer.get() > config_grabberReleaseTime)
				{
					grabberReleaseTimer.stop();
					RobotMap.grabber.set(DoubleSolenoid.Value.kOff);
				}
			}
		}
	}
}
