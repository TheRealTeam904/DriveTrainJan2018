package org.usfirst.frc.team904.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPlaceCube {
	
	static double config_armRaiseTime = 0.5;
	static double config_grabberReleaseTime = 0.1;
	
	static double config_armRaiseSpeed = 0.2;
	
	boolean shouldPlaceCube = false;
	Timer armRaiseTimer = new Timer();
	Timer grabberReleaseTimer = new Timer();
	
	public void onAutonomousInit(String robotSide)
	{
		armRaiseTimer.stop();
		armRaiseTimer.reset();
		
		grabberReleaseTimer.stop();
		grabberReleaseTimer.reset();
		
		String fieldSides = DriverStation
			.getInstance()
			.getGameSpecificMessage();
			
		if(robotSide == null) {robotSide = "X";}
		if(robotSide.length() != 1) {robotSide = "X";}
		
		if(
				robotSide.charAt(0) != 'X'
				&& fieldSides.length() == 3
				&& robotSide.charAt(0) == fieldSides.charAt(0))
		{
			shouldPlaceCube = true;
		}
	}
	
	public boolean raiseArm()
	{
		if(shouldPlaceCube)
		{
			if(armRaiseTimer.get() == 0)
			{
				SmartDashboard.putString("Status", "raising arm");
				armRaiseTimer.start();
				RobotMap.arms.set(config_armRaiseSpeed);
			}
			if(armRaiseTimer.get() > config_armRaiseTime)
			{
				armRaiseTimer.stop();
				RobotMap.arms.set(0);
				return true;
			}
		}
		return false;
	}
	
	public void maybePlaceCube()
	{
		if(shouldPlaceCube)
		{
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
