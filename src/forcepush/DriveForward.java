package forcepush;

public class DriveForward extends Autonomous
{
	public DriveForward(Robot robot)
	{
		super(robot, "Forward no tote");
	}
	
	public void run()
	{
    	this.robot.displayText(Robot.STATUS_MESSAGE, "Autonomous Forward");
    	
		this.robot.getDrivebase().left(0.5,3);
    	
    	this.robot.displayText(Robot.STATUS_MESSAGE, "Moving");
	}
}
