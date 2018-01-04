package forcepush;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
//import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivebase 
{	
	private Robot robot;
    private RobotDrive robotDrive;
    
    // To reverse the direction of the robot, change this to 1/-1
    private int direction = 1;
    
	private double multiplier; //maximum 1.0, minimum 0.0 (negative inverts)
	private double defaultMultiplier; // Default value to reset to
	private double defaultTurnMultiplier;
	
	private boolean squaredInputs = true; //squares the inputs for fine-grained control
	
	private Joystick driveStick;
	
	private double axisX;
	private double axisY;
	private double axisZ;
	
	private long endMoveTime;
	
	private boolean isMoving;
	
	public enum Direction {
	    FORWARD, REVERSE, LEFT, RIGHT,
	    ANGLE, OTHER, NONE 
	}
	
	public Direction previousDirection;
	
	// ------------------------------------------------------------------------------------------------	
	// Constructor

	public Drivebase (Robot robot)
	{
		
		
		// Retain a reference to the main robot class
		this.robot = robot;

		// Setup the mecanum drive
		this.robotDrive = new RobotDrive( new CANTalon(11), 
										  new CANTalon(12),
										  new CANTalon(21), 
										  new CANTalon(22));
        
		this.robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		this.robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        
        //this.robotDrive.setSafetyEnabled(false);

		
		SmartDashboard.putNumber(Robot.SPEED_MULTIPLIER, 2.5);
        
        this.defaultMultiplier = 0.6f;
        this.defaultTurnMultiplier = 0.5f;
        
        this.isMoving = false;
        
        this.previousDirection = Direction.NONE;
	}

	// ------------------------------------------------------------------------------------------------	
	// Primary update loop
	
	public void update()
	{
		// Rest the multiplier to the default
		this.multiplier = this.defaultMultiplier;
		
		if (this.checkForHalfSpeed())
		{
			this.multiplier = this.defaultMultiplier / 2;
		}

		this.driveRobot(axisX, axisY, axisZ);
		
		//this.robot.displayText(Robot.MOTOR_MESSAGE, "" + this.axisX + ":" + this.axisY + ":" + this.axisZ);
		this.robot.accelerometerDisplay();
		
		if (this.endMoveTime == 0)
		{
			this.axisX = 0;
			this.axisY = 0;
			this.axisZ = 0;
		}
	}
	
	public void stop()
	{
		this.axisX = 0;
		this.axisY = 0;
		this.axisZ = 0;
		
		this.update();
	}
	
	// ------------------------------------------------------------------------------------------------
	// Driving forward
	
	public void forward(double speed)
	{
		if (speed <= 1 && speed >= 0)
		{
			this.axisY = -speed;
			this.update();
			this.previousDirection = Direction.FORWARD;
		}
	}
	
	public void forward(double speed, double time)
	{
		//Check to see if we are not going backwards, not going too fast, and that the time given is greater than 0
		if (time > 0 && speed <= 1 && speed >= 0) {
			
			//work out how long to move for.
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			
			//Record the fact that we are moving
			this.isMoving = true;
			
			//While we are allowed to move...
			while (this.isMoving) {
				
				//Move the robot and...
				this.forward(speed);
				
				//Check to see if the time is up
				if (this.endMoveTime < System.currentTimeMillis()) {
					//If it is, stop moving
					this.isMoving = false;
					this.endMoveTime = 0;
					this.stop();
				}
			}
		}
		
	}	
	
	// ------------------------------------------------------------------------------------------------
	// Driving in reverse
	
	public void reverse(double speed)
	{
		if (speed <= 1 && speed >= 0)
		{
			this.axisY = speed;
			this.update();
			this.previousDirection = Direction.REVERSE;
		}
	}
	
	public void reverse(double speed, double time)
	{
		//Check to see if we are not going backwards, not going too fast, and that the time given is greater than 0
		if (time > 0 && speed <= 1 && speed >= 0) {
			//work out how long to move for.
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			
			//Record the fact that we are moving
			this.isMoving = true;
			
			//While we are allowed to move...
			while (this.isMoving) {
				
				//Move the robot and...
				this.reverse(speed);
				
				//Check to see if the time is up
				if (this.endMoveTime < System.currentTimeMillis()) {
					//If it is, stop moving
					this.isMoving = false;
					this.endMoveTime = 0;
					this.stop();
				}
			}
		}
	}	
	
	// ------------------------------------------------------------------------------------------------
	// Driving right
	
	public void right(double speed)
	{
		if (speed <= 1 && speed >= 0)
		{
			this.axisX = speed;
			this.update();
			this.previousDirection = Direction.RIGHT;
		}
	}
	
	public void right(double speed, double time)
	{
		//Check to see if we are not going backwards, not going too fast, and that the time given is greater than 0
		if (time > 0 && speed <= 1 && speed >= 0) {
			//work out how long to move for.
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			
			//Record the fact that we are moving
			this.isMoving = true;
			
			//While we are allowed to move...
			while (this.isMoving) {
				
				//Move the robot and...
				this.right(speed);
				
				//Check to see if the time is up
				if (this.endMoveTime < System.currentTimeMillis()) {
					
					//If it is, stop moving
					this.isMoving = false;
					this.endMoveTime = 0;
					this.stop();
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------------------------------
	// Driving left
	
	public void left(double speed)
	{
		if (speed <= 1 && speed >= 0)
		{
			this.axisX = -speed;
			
			if (this.previousDirection == Direction.LEFT)
			{
				double x = this.robot.getAccelerometerX();
				if (x > 0) 
				{
					//this.axisY += x / 175;
					//this.axisY -= 0.01;
				}
				if (x < 0) 
				{
					//this.axisY += x / 175;
					//this.axisY += 0.01;
				}
			}
			
			this.update();
			
			this.previousDirection = Direction.LEFT;
		}
	}
	
	public void left(double speed, double time)
	{
		// Check to see if we are not going backwards, not going too fast, and that the time given is greater than 0
		if (time > 0 && speed <= 1 && speed >= 0)
		{
			// Work out how long to move for.
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			
			// Record the fact that we are moving
			this.isMoving = true;
			
			// While we are allowed to move ...
			while (this.isMoving)
			{
				// Move the robot and ...
				this.left(speed);
				
				// Check to see if the time is up.
				if (this.endMoveTime < System.currentTimeMillis())
				{
					// If it is, stop moving
					this.isMoving = false;
					this.endMoveTime = 0;
					this.stop();
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------------------------------
	// Driving at an angle
	
	public void angle(double xSpeed, double ySpeed)
	{
		if (xSpeed <= 1 && xSpeed >= -1 && ySpeed <= 1 && ySpeed >= -1)
		{
			this.axisX = xSpeed;
			this.axisY = ySpeed;
			this.update();
			this.previousDirection = Direction.ANGLE;
		}
	}
	
	public void angle(double xSpeed, double ySpeed, double time)
	{
		// Check to see if we are not going backwards, not going too fast, and that the time given is greater than 0
		if (time > 0 && xSpeed <= 1 && xSpeed >= -1 && ySpeed <= 1 && ySpeed >= -1)
		{
			// Work out how long to move for.
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			
			// Record the fact that we are moving
			this.isMoving = true;
			
			// While we are allowed to move ...
			while (this.isMoving)
			{
				// Move the robot and ...
				this.angle(xSpeed, ySpeed);
				
				// Check to see if the time is up.
				if (this.endMoveTime < System.currentTimeMillis())
				{
					// If it is, stop moving
					this.isMoving = false;
					this.endMoveTime = 0;
					this.stop();
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------------------------------
	// Turning clockwise 
	
	public void clockwise(double speed)
	{
		if (Math.abs(speed) <= 1)
		{
			this.axisZ = -speed;
			this.update();
			this.previousDirection = Direction.OTHER;
		}
	}
	
	public void clockwise(double speed, double time)
	{
		if (time > 0 && Math.abs(speed) <= 1)
		{
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			this.clockwise(speed);
			this.isMoving = true;
		}
	}
	
	// ------------------------------------------------------------------------------------------------
	// Turning anticlockwise 
	
	public void anticlockwise(double speed)
	{
		if (Math.abs(speed) <= 1)
		{
			this.axisZ = -speed;
			this.update();
			this.previousDirection = Direction.OTHER;
		}
	}
	
	public void anticlockwise(double speed, double time)
	{
		if (time > 0 && Math.abs(speed) <= 1)
		{
			this.endMoveTime = System.currentTimeMillis() + Math.round(time * 1000);
			this.anticlockwise(speed);
			this.isMoving = true;
		}
	}
	

	// ------------------------------------------------------------------------------------------------
	// Driving methods
	
	public void drive(double x, double y, double z)
	{
		this.axisX = x;
		this.axisY = y;
		this.axisZ = z;
		
		this.update();
	}
	
	private void driveRobot(double x, double y, double z)
	{
		//System.out.println("X: "+x+"\nY: "+y+"\nZ: "+z);
		double seedMultipler = SmartDashboard.getNumber(Robot.SPEED_MULTIPLIER);
		seedMultipler = (seedMultipler - 2.5 ) / 10.0;
		this.multiplier = this.defaultMultiplier + seedMultipler;
		
		//this.multiplier = this.defaultMultiplier;;
		if (this.checkForHalfSpeed())
		{
			this.multiplier = this.multiplier / 2;
		}
		
		this.robotDrive.mecanumDrive_Cartesian(x * this.multiplier * this.direction,
				                               y * this.multiplier * this.direction, 
				                               z * this.defaultTurnMultiplier * this.direction, 
				                               0);
	}
	
	public boolean checkForHalfSpeed()
	{
		return this.robot.getController().getSpeedTrigger();
	}
	
	public boolean isMoving()
	{
		return this.isMoving;
	}
	

	// ------------------------------------------------------------------------------------------------
	// Get methods
	
	public RobotDrive getRobotDrive()
	{
		return this.robotDrive;
	}
}
