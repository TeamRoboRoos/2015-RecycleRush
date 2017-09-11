package forcepush;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
//import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift // implements Runnable
{
	private Robot robot;
	
	//motor that lifts the thingy up and down
	private int motorControllerPort = 0;
	private Talon motor;
	
	// This assumes double solenoids. Fix if I've stuffed up
	private final int pcmCANid = 30;	
	
	//solenoids to control in and out
	private int leftSolenoidInPort = 4;
	private int leftSolenoidOutPort = 5;
	
	private int rightSolenoidInPort = 3;
	private int rightSolenoidOutPort = 2;
	
	private DoubleSolenoid leftArmSolenoid;
	private DoubleSolenoid rightArmSolenoid;  
	
	//Tells us if it goes too high or too low
	private int topLimiterPort = 6;
	private int bottomLimiterPort = 7;
	private DigitalInput topLimiter;
	private DigitalInput bottomLimiter;
	
	//direction that is heading, 1 = up, 0 = stationary, -1 = down
	private int direction = 0;
	
	//modifies the speed to the motor, 1 = full power
	private double multiplier;
	
	//tracks the height of the lift
	private Encoder encoder;
	
	//true if it is supposed to be stationary
	private boolean stopped = true;
	
	//height that the lift is stopped at
	private double stoppedHeight = 0;
	
	//amount of power to the thingy that makes the lift not fall
	public double raiseSpeed = 0;

	private boolean raisedATote = true;
	
	private int extraHeight = 50;

	//its obvious
	private boolean isMoving;
	private double movingSpeed;
	private double targetHeight;
	private double originalHeight;
	
	public Lift (Robot robot)
	{
		// Retain a reference to the main robot class
		this.robot = robot;
		
		//motor that drives the lift up and down
		this.motor = new Talon(this.motorControllerPort);
		
		//set the power to full speed
		this.multiplier = 1;
		
		//in or out arm functions
		this.leftArmSolenoid  = new DoubleSolenoid(this.pcmCANid, this.leftSolenoidInPort,  this.leftSolenoidOutPort);
		this.rightArmSolenoid = new DoubleSolenoid(this.pcmCANid, this.rightSolenoidInPort, this.rightSolenoidOutPort);
		
		//make sure we don't blow the robot up
		this.topLimiter = new DigitalInput(this.topLimiterPort);
		this.bottomLimiter = new DigitalInput(this.bottomLimiterPort);
		
		//not moving
		this.direction = 0;

		//sets up the encoder
		this.encoder = new Encoder(0, 1, false, Encoder.EncodingType.k1X);
		
		//guessing work
		this.encoder.setDistancePerPulse(0.09);
		this.encoder.setReverseDirection(false);
	
		//STOPS EVERYTHING
		this.isMoving = false;
		this.movingSpeed = 0;
		this.targetHeight = 0;
		this.originalHeight = 0;
	}
	
	// -----------------------------------------------------------------------------------------
	// Moving the arms up and down
	
	public void raise(double speed)
	{
		//checks to make sure Maddy doesn't put too high of a speed or about to blow things up
		if (speed >= 0 && speed <= 1 && this.topLimiter.get() == true)
		{
			
			this.stopped = false;
			this.robot.displayText(Robot.LIFT_MESSAGE, "Raising");
			this.direction = 1;
			this.motor.set(speed * multiplier * -1);
			this.raiseSpeed = 0;
		}
	}
	
	public void raiseATote(double speed)
	{
		//checks to make sure Maddy doesn't put too high of a speed or about to blow things up
		if (speed >= 0 && speed <= 1 && this.topLimiter.get() == true)
		{
			this.raiseSpeed = 0;
			this.originalHeight = this.encoder.getDistance();
			this.stopped = false;
			this.movingSpeed = speed * -1;
			this.robot.displayText(Robot.LIFT_MESSAGE, "Raising the height of a tote");
			this.direction = 1;
			this.motor.set(this.movingSpeed * multiplier);
			
			//set the target height to be the height of the tote
			this.targetHeight =  this.originalHeight + 340;
			if (this.encoder.getDistance() < 10)
			{
				this.targetHeight = 340 + extraHeight;
			}
			this.isMoving = true;
		}
	}
	
	public void lower(double speed)
	{
		//checks to make sure Maddy doesn't put too high of a speed or about to blow things up
		if (speed >= 0 && speed <= 1 && this.bottomLimiter.get() == true)
		{
			this.stopped = false;
			this.robot.displayText(Robot.LIFT_MESSAGE, "Lowering");
			this.direction = -1;
			this.motor.set(speed * multiplier);
			this.robot.getLift().raiseSpeed = 0;
		}
		
		//if it reaches the button, it resets the encoder's height
		else if (this.bottomLimiter.get() == false)
		{
			this.encoder.reset();
		}
	}
	
	public void lowerATote(double speed)
	{	
		// Can we move?
		if (this.bottomLimiter.get() == true)
		{
			this.originalHeight = this.encoder.getDistance();
			this.stopped = false;
			this.movingSpeed = speed;
			this.robot.displayText(Robot.LIFT_MESSAGE, "Lowering the height of a tote");
			this.direction = -1;
			this.motor.set(this.movingSpeed * multiplier);
			this.raiseSpeed = 0;
			
			//340 is the height of a tote
			this.targetHeight =  this.originalHeight - 340;
			this.isMoving = true;
		}
	}
	
	public void move()
	{
		// Are we moving?
		if (this.isMoving())
		{
			boolean mustStop = false;
			
			// Are we moving down?
			if (this.movingSpeed > 0)
			{
				//checks to see if we have reached the target distance or reached the bottom
				if (this.encoder.getDistance() < this.targetHeight || this.bottomLimiter.get() == false)
				{
					mustStop = true;
				}
			}
			//if it is moving up
			if (this.movingSpeed < 0)
			{
				//checks to see if we have reached the target distance or reached the top
				if (this.encoder.getDistance() > this.targetHeight || this.topLimiter.get() == false)
				{
					mustStop = true;
				}
			}
			
			if (mustStop == true)
			{
				this.stopped = true;
				this.isMoving = false;
				this.movingSpeed = 0;
				this.stoppedHeight = this.encoder.getDistance(); 
				this.motor.set(0);

				//resets encoder if it reaches the bottom
				if (this.bottomLimiter.get() == false)
				{
					this.encoder.reset();
				}
			}
			else
			{
				//if we don't have to stop then it makes sure the motor is still working
				this.motor.set(this.movingSpeed * multiplier);
			}
		}
	}
	
	
	public void stop()
	{
		this.robot.displayText(Robot.LIFT_MESSAGE, "Stopped");
		if (this.stopped == false)
		{
			this.stopped = true;
			this.direction = 0;
			this.motor.set(0);
			this.stoppedHeight = this.encoder.getDistance();
			this.robot.getLift().raiseSpeed = 0;
		}		
	}

	// -----------------------------------------------------------------------------------------
	// Moving the arms in and out
	
	public void moveOut()
	{
		this.leftArmSolenoid.set(DoubleSolenoid.Value.kForward);
		this.rightArmSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	public void moveIn()
	{
		this.leftArmSolenoid.set(DoubleSolenoid.Value.kReverse);
		this.rightArmSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void turnArmsOff()
	{
		//probably not needed but here just in case
		this.leftArmSolenoid.set(DoubleSolenoid.Value.kOff);
		this.rightArmSolenoid.set(DoubleSolenoid.Value.kOff);
	}
	
	// -----------------------------------------------------------------------------------------
	// Checking for top and bottom limiters
	
	public DigitalInput getTopLimiter()
	{
		return this.topLimiter;
	}
	
	public DigitalInput getBottomLimiter()
	{
		return this.bottomLimiter;
	}
	
	// -----------------------------------------------------------------------------------------
	//get methods
	
	public int getDirection()
	{
		return this.direction;
	}
	
	public Talon getMotor()
	{
		return this.motor;
	}
	
	public boolean getStoppedStatus()
	{
		return this.stopped;
	}
	
	public double getStoppedHeight()
	{
		return this.stoppedHeight;
	}
	
	public double getCurrentHeight()
	{
		return this.encoder.getDistance();
	}
	
	public boolean isMoving()
	{
		return this.isMoving;
	}
	
	
	/*
	public void run() 
	{
		try 
		{
			if (stopThread) throw new InterruptedException("Stopped");
			
			if (this.direction > 0 && 
					(this.topLimiter.get() == true || SmartDashboard.getBoolean("DB/Button 2")))
			{
				this.motor.set(0);
				this.direction = 0;
			}
			System.out.println(this.topLimiter.get());
			
			if (this.direction < 0 && this.bottomLimiter.get() == true)
			{
				//this.motor.set(0);
				this.motor.set(0);
				this.direction = 0;
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
		   
	public void start ()
	{
		if (t == null)
		{
			t = new Thread (this);
			t.start ();
		}
	}
	*/

}
