package forcepush;

public class DriveBack extends Autonomous
{	
	public DriveBack(Robot robot)
	{
		super(robot, "Reverse no tote");
	}
	
	public void run()
	{
    	this.robot.displayText(Robot.STATUS_MESSAGE, "Autonomous Back");
    	
		this.robot.getDrivebase().reverse(0.4, 1.5);
		
    	
    	this.robot.displayText(Robot.STATUS_MESSAGE, "Moving");
	}
}
