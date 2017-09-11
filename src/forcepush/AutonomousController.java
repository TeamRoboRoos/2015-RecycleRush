package forcepush;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousController 
{
	private Robot robot;
	
	public AutonomousController(Robot robot)
	{
		this.robot = robot;
	}
	
	public boolean autoPosition()
	{            
    	boolean isGood = false;
    	
    	if (this.robot.getSensorBar().getMode() == SensorBar.IDLE)
		{
			this.robot.getSensorBar().setMode(SensorBar.DRIVER_MODE);
		}
		
		this.robot.displayText(Robot.STATUS_MESSAGE, "Semi-Autonomous");
		
		int direction = this.robot.getSensorBar().getDirection();
		SmartDashboard.putString("DB/String 2", "" + direction);
		
		this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction);
		
		/*

		if (direction == SensorBar.FORWARD)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Forward");
			this.robot.getDrivebase().forward(0.4);
		}
		else if (direction == SensorBar.FORWARD_A_BIT)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Forward a bit");
			this.robot.getDrivebase().forward(0.2);
		}
		else if (direction == SensorBar.BACK)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Reverse");
			this.robot.getDrivebase().reverse(0.4);
		}
		else if (direction == SensorBar.BACK_A_BIT)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Reverse a bit");
			this.robot.getDrivebase().reverse(0.2);
		}
		
		else if (direction == SensorBar.LEFT)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Left");
			this.robot.getDrivebase().left(0.4);
		}
		else if (direction == SensorBar.RIGHT)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Right");
			this.robot.getDrivebase().right(0.4);
		}
		else if (direction == SensorBar.TURN_LEFT)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Clockwise");
			this.robot.getDrivebase().clockwise(0.25);
		}
		else if (direction == SensorBar.TURN_RIGHT)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Anticlockwise");
			this.robot.getDrivebase().anticlockwise(0.25);
		}
		else if (direction == SensorBar.GOOD)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "");
			isGood = true;
			this.robot.displayText(Robot.LIFT_MESSAGE, "Aligned");
			this.robot.setSemiAutonomous(false);
		}
		else if (direction == SensorBar.BAD_THINGS_HAPPENED)
		{
			this.robot.displayText(Robot.DIRECTION_MESSAGE, "" + direction + ": Error");
		}
		*/
		
		return isGood;
	}

}
