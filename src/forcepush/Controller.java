package forcepush;

import edu.wpi.first.wpilibj.*;

public class Controller 
{
	private Robot robot;
	
    private Joystick driveStick;
    
    private final int TRIGGER = 1;
    private final int SEMI_AUTONOMOUS = 2;
    private final int ARMS_UP = 3;
    private final int ARMS_DOWN = 2;
    private final int ARMS_OUT = 4;
    private final int ARMS_IN = 5;
    
    private final int DRIVER_ASSIST = 7;
    
    public Controller(Robot robot, int joystickID) 
    {
    	this.robot = robot;
    	this.driveStick = new Joystick(joystickID);
    }
    
    public boolean getSpeedTrigger()
    {
    	boolean trigger = false;
    	
    	if (this.driveStick.getRawButton(this.TRIGGER) == true)
    	{
    		trigger = true;
    	}
    	
    	return trigger;
    }
    
    public boolean getSemiAutonomous()
    {
    	return this.driveStick.getRawButton(this.SEMI_AUTONOMOUS);
    }
    
    public boolean getUpButton()
    {
    	return this.driveStick.getRawButton(this.ARMS_UP);
    }
    
    public boolean getDownButton()
    {
    	return this.driveStick.getRawButton(this.ARMS_DOWN);
    }
    
    public boolean getInButton()
    {
    	return this.driveStick.getRawButton(this.ARMS_IN);
    }
    
    public boolean getOutButton()
    {
    	return this.driveStick.getRawButton(this.ARMS_OUT);
    }
    
    public double getLiftPower() 
    {
    	return this.driveStick.getZ();
    }

    
    public boolean getPOVLeft()
    {
    	return (this.driveStick.getPOV() == 270);
    }
    
    public boolean getPOVRight()
    {
    	return (this.driveStick.getPOV() == 90);
    }
    
    public boolean getPOVForward()
    {
    	return (this.driveStick.getPOV() == 0);
    }
    
    public boolean getPOVBack()
    {
    	return (this.driveStick.getPOV() == 180);
    }
}
