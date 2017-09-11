package forcepush;

public class Autonomous 
{
	Robot robot;
	String name = "Default";
	
	
	public Autonomous(Robot robot, String name)
	{
		this.robot = robot;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void run()
	{
		//------------------------------------------------------------------------------------------
		//								3 Totes 1 Robot
		//------------------------------------------------------------------------------------------
		//autonomous mode for robot, assumes that:
		//robot starts with arms at lowest position and aligned with the first tote
		//drivebase.right is perfectly right (e.g. gyro works)
		//robot.autoposition aligns the robot with the tote
		//raiseATote has been fixed to account for first raise to be lower
		// this assumes the speeds from Bryce's phone are accurate, any problems are Bryces fault
		this.robot.lift.raiseATote(1); //to get tote off ground
		this.robot.lift.raiseATote(1); //to get over bin it is about to pass
		this.robot.drivebase.right(0.75, 1.1);
		this.robot.lift.lowerATote(1);
		this.robot.drivebase.left(0.75, 0.1);
		double timeToLower = System.currentTimeMillis()+ 500;
		while(System.currentTimeMillis()< timeToLower) //for half a second lower so you dont bring the tote with you when reversing
		{
			this.robot.lift.lower(1);	
		}
		this.robot.drivebase.reverse(0.75, 0.5);// 0.5 MUST BE REPLACED with 
		//((distance from edge of the arm furthest from the robot to the edge of the tote closest to the robot)+20)/2 (2 is the speed backwards)
		this.robot.lift.lowerATote(1);
		this.robot.autoPosition();
		this.robot.lift.raiseATote(1); //to get tote off ground
		this.robot.lift.raiseATote(1); //to get over bin it is about to pass
		this.robot.drivebase.right(0.75, 1.1);
		this.robot.lift.lowerATote(1);
		this.robot.drivebase.left(0.75, 0.1);
		timeToLower = System.currentTimeMillis() + 500;
		while(System.currentTimeMillis()< timeToLower) //for half a second lower so you dont bring the tote with you when reversing
		{
			this.robot.lift.lower(1);	
		}
		this.robot.drivebase.reverse(0.75, 0.5);// 0.5 MUST BE REPLACED with 
		//((distance from edge of the arm furthest from the robot to the edge of the tote closest to the robot)+20)/2 (2 is the speed backwards)
		this.robot.lift.lowerATote(1);
		this.robot.autoPosition();
		this.robot.lift.raiseATote(1);
		this.robot.drivebase.reverse(0.75, 1.3589);
		this.robot.lift.lowerATote(1);
		this.robot.lift.lowerATote(1);
		//---------------------------------------------------------------------------------------------
		//								3 Totes 3 Robots
		//---------------------------------------------------------------------------------------------
		/*this.robot.lift.raiseATote(1);
		this.robot.drivebase.reverse(0.75, 1.5);
		this.robot.lift.lowerATote(1);
		this.robot.lift.lowerATote(1);
		*/
		//--------------------------------------------------------------------------------------------
		//								3 Bins 3 Robots
		//--------------------------------------------------------------------------------------------
		/*this.robot.lift.raiseATote(1);
		this.robot.lift.raiseATote(1);
		this.robot.drivebase.reverse(0.75,1.5);
		this.robot.lift.lowerATote(1);
		this.robot.lift.lowerATote(1);
		this.robot.lift.lowerATote(1);
		*/
		//-----------------------------------------------------------------------------------------------
		//								0 Totes 0 useful robots
		//------------------------------------------------------------------------------------------------
		//this.robot.drivebase.forward(0.75,0.5);
	}
}
